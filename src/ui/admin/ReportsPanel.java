package ui.admin;

import data.DataStore;
import model.Department;
import model.Enrollment;
import model.StudentProfile;
import ui.shared.Refreshable;
import ui.shared.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ReportsPanel extends JPanel implements Refreshable {
    private DataStore dataStore;
    private JLabel totalUsersLabel;
    private JLabel totalStudentsLabel;
    private JLabel totalCoursesLabel;
    private JLabel pendingEnrollmentsLabel;
    private JPanel warningPanel;
    private JComboBox<String> repDeptCombo;
    private JLabel dTotalLabel;
    private JLabel dSeniorsLabel;
    private JLabel dAvgCrdsLabel;

    public ReportsPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(0, 15));
        setBackground(UIUtils.WARM_BEIGE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setOpaque(false);

        totalUsersLabel = createStatCard("Total Users", "0");
        totalStudentsLabel = createStatCard("Total Students", "0");
        totalCoursesLabel = createStatCard("Total Courses", "0");
        pendingEnrollmentsLabel = createStatCard("Pending Enrollments", "0");

        statsPanel.add(totalUsersLabel.getParent());
        statsPanel.add(totalStudentsLabel.getParent());
        statsPanel.add(totalCoursesLabel.getParent());
        statsPanel.add(pendingEnrollmentsLabel.getParent());

        JPanel healthPanel = new JPanel(new BorderLayout());
        healthPanel.setBackground(Color.WHITE);
        healthPanel.setBorder(BorderFactory.createTitledBorder("System Health Checks"));
        
        warningPanel = new JPanel();
        warningPanel.setLayout(new BoxLayout(warningPanel, BoxLayout.Y_AXIS));
        warningPanel.setBackground(Color.WHITE);
        healthPanel.add(new JScrollPane(warningPanel), BorderLayout.CENTER);

        JPanel reportsPanel = new JPanel(new BorderLayout());
        reportsPanel.setBackground(Color.WHITE);
        reportsPanel.setBorder(BorderFactory.createTitledBorder("Department Reports"));
        
        JPanel rTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rTop.setOpaque(false);
        rTop.add(new JLabel("Department:"));
        repDeptCombo = new JComboBox<>();
        rTop.add(repDeptCombo);
        reportsPanel.add(rTop, BorderLayout.NORTH);

        JPanel rBody = new JPanel(new GridLayout(3, 1, 0, 10));
        rBody.setOpaque(false);
        rBody.setBorder(new EmptyBorder(10, 10, 10, 10));
        dTotalLabel = new JLabel("Total Students: 0");
        dSeniorsLabel = new JLabel("Seniors (Year 4): 0");
        dAvgCrdsLabel = new JLabel("Avg Enrolled Courses: 0.0");
        dTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dSeniorsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dAvgCrdsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rBody.add(dTotalLabel);
        rBody.add(dSeniorsLabel);
        rBody.add(dAvgCrdsLabel);
        reportsPanel.add(rBody, BorderLayout.CENTER);

        repDeptCombo.addActionListener(e -> updateDepartmentReport());

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(healthPanel);
        bottomPanel.add(reportsPanel);

        add(statsPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);

        refresh();
    }

    private JLabel createStatCard(String title, String initialValue) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setForeground(Color.DARK_GRAY);

        JLabel v = new JLabel(initialValue);
        v.setFont(new Font("Segoe UI", Font.BOLD, 24));
        v.setForeground(UIUtils.SAGE_GREEN);

        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return v;
    }

    private void updateDepartmentReport() {
        String code = (String) repDeptCombo.getSelectedItem();
        if (code == null) return;

        List<StudentProfile> students = dataStore.getStudents();
        long total = students.stream().filter(s -> code.equals(s.getDepartment())).count();
        long seniors = students.stream().filter(s -> code.equals(s.getDepartment()) && s.getYear() == 4).count();

        double avgEnr = 0;
        if (total > 0) {
            long enrCount = dataStore.getAllEnrollments().stream()
                    .filter(en -> {
                        StudentProfile s = dataStore.findStudentProfileByUsername(en.getStudentUsername());
                        return s != null && code.equals(s.getDepartment()) && "APPROVED".equals(en.getStatus());
                    }).count();
            avgEnr = (double) enrCount / total;
        }

        dTotalLabel.setText("Total Students: " + total);
        dSeniorsLabel.setText("Seniors (Year 4): " + seniors);
        dAvgCrdsLabel.setText(String.format("Avg Enrolled Courses: %.1f", avgEnr));
    }

    @Override
    public void refresh() {
        totalUsersLabel.setText(String.valueOf(dataStore.getUsers().size()));
        totalStudentsLabel.setText(String.valueOf(dataStore.getStudents().size()));
        totalCoursesLabel.setText(String.valueOf(dataStore.getCourses().size()));
        pendingEnrollmentsLabel.setText(String.valueOf(
                dataStore.getAllEnrollments().stream().filter(e -> "PENDING".equals(e.getStatus())).count()
        ));

        warningPanel.removeAll();
        boolean hasWarnings = false;

        for (StudentProfile s : dataStore.getStudents()) {
            if (dataStore.findUser(s.getUsername()) == null) {
                JLabel w = new JLabel("⚠ Student Profile " + s.getStudentId() + " has missing user account.");
                w.setForeground(Color.RED);
                warningPanel.add(w);
                hasWarnings = true;
            }
        }

        for (Enrollment en : dataStore.getAllEnrollments()) {
            if (dataStore.findStudentProfileByUsername(en.getStudentUsername()) == null) {
                JLabel w = new JLabel("⚠ Enrollment for missing student ID: " + en.getStudentUsername());
                w.setForeground(Color.RED);
                warningPanel.add(w);
                hasWarnings = true;
            }
        }

        if (!hasWarnings) {
            JLabel ok = new JLabel("✔ All system checks passed.");
            ok.setForeground(UIUtils.SAGE_GREEN);
            warningPanel.add(ok);
        }
        warningPanel.revalidate();
        warningPanel.repaint();

        Object sel = repDeptCombo.getSelectedItem();
        repDeptCombo.setModel(new DefaultComboBoxModel<>(dataStore.getDepartments().stream().map(Department::getCode).toArray(String[]::new)));
        if (sel != null) repDeptCombo.setSelectedItem(sel);
        updateDepartmentReport();
    }
}
