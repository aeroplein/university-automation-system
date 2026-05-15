package ui.admin;

import data.DataStore;
import model.Course;
import model.Department;
import model.StudentProfile;
import ui.shared.Refreshable;
import ui.shared.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EnrollmentManagementPanel extends JPanel implements Refreshable {
    private DataStore dataStore;
    private DefaultTableModel enrollmentsTableModel;
    private JTable enrollmentsTable;
    private DefaultTableModel requestsTableModel;
    private JTable requestsTable;

    public EnrollmentManagementPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout());
        setBackground(UIUtils.WARM_BEIGE);

        initUI();
    }

    private void initUI() {
        JTabbedPane subTabs = new JTabbedPane();
        subTabs.addTab("Current Enrollments", createManageEnrollmentsSubPanel());
        subTabs.addTab("Pending Requests", createPendingRequestsSubPanel());
        subTabs.addTab("Automation Hub", createBulkEnrollmentSubPanel());
        
        add(subTabs, BorderLayout.CENTER);
        refresh();
    }

    private JPanel createManageEnrollmentsSubPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(UIUtils.WARM_BEIGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        enrollmentsTableModel = new DefaultTableModel(
                new String[] { "Student ID", "Student Name", "Course Code", "Course Name" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        enrollmentsTable = UIUtils.createModernTable(enrollmentsTableModel);
        
        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        UIUtils.setupSearch(searchWrap, enrollmentsTableModel, enrollmentsTable);
        
        panel.add(searchWrap, BorderLayout.NORTH);
        panel.add(new JScrollPane(enrollmentsTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton addBtn = new JButton("Add Individual");
        addBtn.setBackground(UIUtils.SAGE_GREEN);
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> showAddEnrollmentDialog());

        JButton deleteBtn = new JButton("Unenroll Selected");
        deleteBtn.setBackground(UIUtils.TERRACOTTA);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> {
            int row = enrollmentsTable.getSelectedRow();
            if (row >= 0) {
                String studentId = (String) enrollmentsTableModel.getValueAt(enrollmentsTable.convertRowIndexToModel(row), 0);
                String courseCode = (String) enrollmentsTableModel.getValueAt(enrollmentsTable.convertRowIndexToModel(row), 2);
                StudentProfile p = dataStore.getStudents().stream().filter(s -> s.getStudentId().equals(studentId)).findFirst().orElse(null);
                if (p != null && dataStore.removeEnrollment(p.getUsername(), courseCode)) {
                    refresh();
                }
            }
        });

        actions.add(addBtn);
        actions.add(deleteBtn);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPendingRequestsSubPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(UIUtils.WARM_BEIGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        requestsTableModel = new DefaultTableModel(
                new String[] { "Student ID", "Student Name", "Dept", "Course", "Status" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        requestsTable = UIUtils.createModernTable(requestsTableModel);
        
        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        UIUtils.setupSearch(searchWrap, requestsTableModel, requestsTable);
        
        panel.add(searchWrap, BorderLayout.NORTH);
        panel.add(new JScrollPane(requestsTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton appBtn = new JButton("Approve");
        appBtn.setBackground(UIUtils.SAGE_GREEN);
        appBtn.setForeground(Color.WHITE);
        appBtn.addActionListener(e -> {
            int row = requestsTable.getSelectedRow();
            if (row >= 0) {
                String studentId = (String) requestsTableModel.getValueAt(requestsTable.convertRowIndexToModel(row), 0);
                String courseCode = (String) requestsTableModel.getValueAt(requestsTable.convertRowIndexToModel(row), 3);
                StudentProfile p = dataStore.getStudents().stream().filter(s -> s.getStudentId().equals(studentId)).findFirst().orElse(null);
                if (p != null && dataStore.approveRequest(p.getUsername(), courseCode)) {
                    refresh();
                }
            }
        });

        JButton rejBtn = new JButton("Reject");
        rejBtn.setBackground(UIUtils.TERRACOTTA);
        rejBtn.setForeground(Color.WHITE);
        rejBtn.addActionListener(e -> {
            int row = requestsTable.getSelectedRow();
            if (row >= 0) {
                String studentId = (String) requestsTableModel.getValueAt(requestsTable.convertRowIndexToModel(row), 0);
                String courseCode = (String) requestsTableModel.getValueAt(requestsTable.convertRowIndexToModel(row), 3);
                StudentProfile p = dataStore.getStudents().stream().filter(s -> s.getStudentId().equals(studentId)).findFirst().orElse(null);
                if (p != null && dataStore.rejectRequest(p.getUsername(), courseCode)) {
                    refresh();
                }
            }
        });

        actions.add(appBtn);
        actions.add(rejBtn);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBulkEnrollmentSubPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.WARM_BEIGE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Target Department:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> deptCombo = new JComboBox<>(dataStore.getDepartments().stream().map(Department::getCode).toArray(String[]::new));
        panel.add(deptCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Target Year:"), gbc);
        gbc.gridx = 1;
        JComboBox<Integer> yearCombo = new JComboBox<>(new Integer[] { 1, 2, 3, 4 });
        panel.add(yearCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JTextArea info = new JTextArea("This will automatically enroll ALL students in the selected department and year into their mandatory curriculum courses.\nExisting enrollments will be skipped.");
        info.setEditable(false);
        info.setBackground(UIUtils.WARM_BEIGE);
        info.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        panel.add(info, gbc);

        gbc.gridy = 3;
        JButton bulkBtn = new JButton("Run Automated Bulk Enrollment");
        bulkBtn.setBackground(UIUtils.SKY_BLUE);
        bulkBtn.setForeground(Color.WHITE);
        bulkBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bulkBtn.setPreferredSize(new Dimension(300, 40));
        bulkBtn.addActionListener(e -> {
            String dept = (String) deptCombo.getSelectedItem();
            int year = (Integer) yearCombo.getSelectedItem();
            if (dept != null) {
                dataStore.bulkEnroll(dept, year);
                refresh();
                JOptionPane.showMessageDialog(this, "Bulk enrollment completed for " + dept + " Year " + year);
            }
        });
        panel.add(bulkBtn, gbc);

        return panel;
    }

    private void showAddEnrollmentDialog() {
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JComboBox<String> sCombo = new JComboBox<>(dataStore.getStudents().stream()
                .map(s -> s.getStudentId() + " - " + s.getFullName()).toArray(String[]::new));
        
        JComboBox<String> cCombo = new JComboBox<>(dataStore.getCourses().stream()
                .map(c -> c.getCourseCode() + " - " + c.getCourseName()).toArray(String[]::new));

        p.add(new JLabel("Student:"));
        p.add(sCombo);
        p.add(new JLabel("Course:"));
        p.add(cCombo);

        int result = JOptionPane.showConfirmDialog(this, p, "Add Enrollment", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String sSel = (String) sCombo.getSelectedItem();
            String cSel = (String) cCombo.getSelectedItem();
            if (sSel != null && cSel != null) {
                String sId = sSel.split(" - ")[0];
                String cCode = cSel.split(" - ")[0];
                
                StudentProfile profile = dataStore.getStudents().stream().filter(s -> s.getStudentId().equals(sId)).findFirst().orElse(null);
                if (profile != null) {
                    if (dataStore.enrollStudent(profile.getUsername(), cCode)) {
                        refresh();
                        JOptionPane.showMessageDialog(this, "Enrolled successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Enrollment failed (already enrolled or quota full).");
                    }
                }
            }
        }
    }

    @Override
    public void refresh() {
        if (enrollmentsTableModel != null) {
            enrollmentsTableModel.setRowCount(0);
            dataStore.getAllEnrollments().stream()
                    .filter(enr -> "APPROVED".equals(enr.getStatus()))
                    .forEach(enr -> {
                StudentProfile s = dataStore.findStudentProfileByUsername(enr.getStudentUsername());
                Course c = dataStore.findCourse(enr.getCourseCode());
                enrollmentsTableModel.addRow(new Object[] {
                    s != null ? s.getStudentId() : "N/A",
                    s != null ? s.getFullName() : enr.getStudentUsername(),
                    enr.getCourseCode(),
                    c != null ? c.getCourseName() : "N/A"
                });
            });
        }

        if (requestsTableModel != null) {
            requestsTableModel.setRowCount(0);
            dataStore.getAllEnrollments().stream()
                    .filter(enr -> "PENDING".equals(enr.getStatus()))
                    .forEach(enr -> {
                StudentProfile s = dataStore.findStudentProfileByUsername(enr.getStudentUsername());
                requestsTableModel.addRow(new Object[] {
                    s != null ? s.getStudentId() : "N/A",
                    s != null ? s.getFullName() : enr.getStudentUsername(),
                    s != null ? s.getDepartment() : "-",
                    enr.getCourseCode(),
                    enr.getStatus()
                });
            });
        }
    }
}
