package ui.student;

import data.DataStore;

import model.Enrollment;
import model.User;
import ui.shared.Refreshable;
import ui.shared.UIUtils; // Import modern UI helper utilities

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AvailableCoursesPanel extends JPanel implements Refreshable {
    private DataStore dataStore;
    private User currentUser;
    private DefaultTableModel availableCoursesTableModel;
    private JTable table;
    private JLabel ectsLbl;
    private JComboBox<String> instFilter;
    private JComboBox<String> deptFilter;

    public AvailableCoursesPanel(DataStore dataStore, User currentUser) {
        this.dataStore = dataStore;
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 15));
        setBackground(UIUtils.WARM_BEIGE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initUI();
    }

    private void initUI() {
        JPanel topInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topInfo.setOpaque(false);
        int current = dataStore.getStudentTotalCredits(currentUser.getUsername());
        int limit = dataStore.calculateCreditLimit(currentUser.getUsername());
        ectsLbl = new JLabel("Current Load: " + current + " / " + limit + " ECTS");
        ectsLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topInfo.add(ectsLbl);

        availableCoursesTableModel = new DefaultTableModel(
                new String[] { "Code", "Dept", "Name", "Instructor", "Credits", "Quota", "Status" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = UIUtils.createModernTable(availableCoursesTableModel);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Filter by Instructor:"));
        instFilter = new JComboBox<>();
        instFilter.addItem("All Instructors");
        dataStore.getUsers().stream()
                .filter(u -> "Instructor".equalsIgnoreCase(u.getRole()))
                .forEach(u -> instFilter.addItem(u.getUsername()));

        filterPanel.add(instFilter);

        filterPanel.add(new JLabel("Department:"));
        deptFilter = new JComboBox<>();
        deptFilter.addItem("All Departments");
        deptFilter.addItem("My Departments");
        dataStore.getDepartments().forEach(d -> deptFilter.addItem(d.getCode()));
        
        deptFilter.addActionListener(e -> applyFilters());
        filterPanel.add(deptFilter);
        
        // Default to "My Departments" if available
        deptFilter.setSelectedItem("My Departments");

        JPanel topWrap = new JPanel(new BorderLayout());
        topWrap.setOpaque(false);
        UIUtils.setupSearch(topWrap, availableCoursesTableModel, table);
        topWrap.add(filterPanel, BorderLayout.WEST);

        JPanel northBox = new JPanel(new BorderLayout());
        northBox.setOpaque(false);
        northBox.add(topInfo, BorderLayout.NORTH);
        northBox.add(topWrap, BorderLayout.SOUTH);
        add(northBox, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton enrollBtn = new JButton("Request Enrollment");
        enrollBtn.setBackground(UIUtils.SAGE_GREEN);
        enrollBtn.setForeground(Color.WHITE);
        enrollBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String code = (String) availableCoursesTableModel.getValueAt(table.convertRowIndexToModel(row), 0);

                if (dataStore.isStudentEnrolled(currentUser.getUsername(), code)) {
                    JOptionPane.showMessageDialog(this,
                            "You are already enrolled or have a pending request for this course.");
                    return;
                }

                if (dataStore.requestEnrollment(currentUser.getUsername(), code)) {
                    JOptionPane.showMessageDialog(this, "Enrollment request submitted to Admin.");
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this, "Request failed. Check ECTS limits or major restrictions.");
                }
            }
        });
        add(enrollBtn, BorderLayout.SOUTH);

        refresh();
    }

    @Override
    public void refresh() {
        if (availableCoursesTableModel == null)
            return;
        availableCoursesTableModel.setRowCount(0);
        dataStore.getCourses().forEach(c -> {
            int count = dataStore.countEnrollmentForCourse(c.getCourseCode());
            Enrollment enr = dataStore.getAllEnrollments().stream()
                    .filter(e -> e.getStudentUsername().equals(currentUser.getUsername())
                            && e.getCourseCode().equals(c.getCourseCode()))
                    .findFirst().orElse(null);

            String status = "Available";
            if (enr != null) {
                status = enr.getStatus();
            } else if (count >= c.getQuota()) {
                status = "Full";
            }

            String dept = c.getCourseCode().replaceAll("\\d+", "");
            availableCoursesTableModel
                    .addRow(new Object[] { c.getCourseCode(), dept, c.getCourseName(), c.getInstructorUsername(),
                            c.getCredit(), count + "/" + c.getQuota(), status });
        });

        applyFilters();

        int current = dataStore.getStudentTotalCredits(currentUser.getUsername());
        int limit = dataStore.calculateCreditLimit(currentUser.getUsername());
        if (ectsLbl != null) {
            ectsLbl.setText("Current Load: " + current + " / " + limit + " ECTS");
        }

        if (instFilter != null) {
            Object selected = instFilter.getSelectedItem();
            instFilter.removeAllItems();
            instFilter.addItem("All Instructors");
            dataStore.getUsers().stream()
                    .filter(u -> "Instructor".equalsIgnoreCase(u.getRole()))
                    .forEach(u -> instFilter.addItem(u.getUsername()));
            if (selected != null) {
                instFilter.setSelectedItem(selected);
            }
        }
        
        if (deptFilter != null) {
            Object selected = deptFilter.getSelectedItem();
            deptFilter.removeAllItems();
            deptFilter.addItem("All Departments");
            deptFilter.addItem("My Departments");
            dataStore.getDepartments().forEach(d -> deptFilter.addItem(d.getCode()));
            if (selected != null) {
                deptFilter.setSelectedItem(selected);
            }
        }
    }

    private void applyFilters() {
        if (table == null || availableCoursesTableModel == null) return;
        
        @SuppressWarnings("unchecked")
        DefaultRowSorter<DefaultTableModel, Integer> sorter = (DefaultRowSorter<DefaultTableModel, Integer>) table.getRowSorter();
        if (sorter == null) return;

        String instSelected = (String) instFilter.getSelectedItem();
        String deptSelected = (String) deptFilter.getSelectedItem();
        
        java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();
        
        // Instructor Filter (Column 3 now)
        if (instSelected != null && !"All Instructors".equals(instSelected)) {
            filters.add(RowFilter.regexFilter(instSelected, 3));
        }
        
        // Department Filter (Column 1)
        if (deptSelected != null && !"All Departments".equals(deptSelected)) {
            if ("My Departments".equals(deptSelected)) {
                model.StudentProfile profile = dataStore.findStudentProfileByUsername(currentUser.getUsername());
                if (profile != null) {
                    String d1 = profile.getDepartment();
                    String d2 = profile.getSecondDepartment();
                    if (d2 != null && !d2.isEmpty()) {
                        filters.add(RowFilter.regexFilter("^(" + d1 + "|" + d2 + ")$", 1));
                    } else {
                        filters.add(RowFilter.regexFilter("^" + d1 + "$", 1));
                    }
                }
            } else {
                filters.add(RowFilter.regexFilter("^" + deptSelected + "$", 1));
            }
        }
        
        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }
}
