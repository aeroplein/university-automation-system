package ui.admin;

import data.DataStore;
import model.Course;
import ui.shared.Refreshable;
import ui.shared.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CourseManagementPanel extends JPanel implements Refreshable {
    private DataStore dataStore;
    private DefaultTableModel adminCoursesTableModel;
    private JTable adminCoursesTable;

    public CourseManagementPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(0, 15));
        setBackground(UIUtils.WARM_BEIGE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        adminCoursesTableModel = new DefaultTableModel(new String[] { "Code", "Name", "Instructor", "Quota", "Credits" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        adminCoursesTable = UIUtils.createModernTable(adminCoursesTableModel);

        JPanel topWrap = new JPanel(new BorderLayout());
        topWrap.setOpaque(false);
        UIUtils.setupSearch(topWrap, adminCoursesTableModel, adminCoursesTable);
        add(topWrap, BorderLayout.NORTH);

        add(new JScrollPane(adminCoursesTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton addBtn = new JButton("Add Course");
        addBtn.setBackground(UIUtils.SAGE_GREEN);
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> showAddCourseDialog(adminCoursesTableModel, adminCoursesTable));

        JButton updBtn = new JButton("Update Course");
        updBtn.setBackground(UIUtils.SKY_BLUE);
        updBtn.setForeground(Color.WHITE);
        updBtn.addActionListener(e -> showUpdateCourseDialog(adminCoursesTableModel, adminCoursesTable));

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setBackground(UIUtils.TERRACOTTA);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> {
            int row = adminCoursesTable.getSelectedRow();
            if (row >= 0) {
                String code = (String) adminCoursesTableModel.getValueAt(adminCoursesTable.convertRowIndexToModel(row), 0);
                if (dataStore.removeCourse(code)) {
                    refresh();
                }
            }
        });

        actions.add(addBtn);
        actions.add(updBtn);
        actions.add(deleteBtn);
        add(actions, BorderLayout.SOUTH);

        refresh();
    }

    private void showAddCourseDialog(DefaultTableModel model, JTable table) {
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField cField = new JTextField();
        JTextField nField = new JTextField();
        JTextField qField = new JTextField("30");
        JTextField crField = new JTextField("3");

        p.add(new JLabel("Course Code:"));
        p.add(cField);
        p.add(new JLabel("Course Name:"));
        p.add(nField);

        JLabel iLbl = new JLabel("Search Instructor:");
        JTextField iSearch = new JTextField();
        JComboBox<String> iCombo = new JComboBox<>();

        Runnable refreshInstructors = () -> {
            String filter = iSearch.getText().toLowerCase();
            iCombo.removeAllItems();
            dataStore.getUsers().stream()
                .filter(u -> "Instructor".equalsIgnoreCase(u.getRole()))
                .filter(u -> u.getFullName().toLowerCase().contains(filter) || u.getUsername().toLowerCase().contains(filter))
                .forEach(u -> iCombo.addItem(u.getUsername() + " (" + u.getFullName() + ") - " + (u.getDepartment().isEmpty() ? "N/A" : u.getDepartment())));
        };

        refreshInstructors.run();
        iSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshInstructors.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshInstructors.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshInstructors.run(); }
        });

        p.add(iLbl);
        p.add(iSearch);
        p.add(new JLabel("Select Instructor:"));
        p.add(iCombo);

        p.add(new JLabel("Quota:"));
        p.add(qField);
        p.add(new JLabel("Credits:"));
        p.add(crField);

        int result = JOptionPane.showConfirmDialog(this, p, "Add Course", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String code = cField.getText();
                String name = nField.getText();
                String sel = (String) iCombo.getSelectedItem();
                String inst = (sel != null) ? sel.split(" ")[0] : null;
                int q = Integer.parseInt(qField.getText());
                int cr = Integer.parseInt(crField.getText());

                if (dataStore.addCourse(new Course(code, name, cr, q, inst))) {
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this, "Course code already exists.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid numeric input.");
            }
        }
    }

    private void showUpdateCourseDialog(DefaultTableModel model, JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course to update.");
            return;
        }
        row = table.convertRowIndexToModel(row);
        String code = (String) model.getValueAt(row, 0);
        Course existing = dataStore.findCourse(code);
        if (existing == null) return;

        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField cField = new JTextField(code);
        cField.setEditable(false);
        JTextField nField = new JTextField(existing.getCourseName());

        JLabel iLbl = new JLabel("Search Instructor:");
        JTextField iSearch = new JTextField();
        JComboBox<String> iCombo = new JComboBox<>();

        Runnable refreshInstructors = () -> {
            String filter = iSearch.getText().toLowerCase();
            iCombo.removeAllItems();
            dataStore.getUsers().stream()
                .filter(u -> "Instructor".equalsIgnoreCase(u.getRole()))
                .filter(u -> u.getFullName().toLowerCase().contains(filter) || u.getUsername().toLowerCase().contains(filter))
                .forEach(u -> iCombo.addItem(u.getUsername() + " (" + u.getFullName() + ") - " + (u.getDepartment().isEmpty() ? "N/A" : u.getDepartment())));
        };

        refreshInstructors.run();
        // Set existing instructor
        for (int i = 0; i < iCombo.getItemCount(); i++) {
            if (iCombo.getItemAt(i).startsWith(existing.getInstructorUsername())) {
                iCombo.setSelectedIndex(i);
                break;
            }
        }

        iSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshInstructors.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshInstructors.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshInstructors.run(); }
        });

        JTextField qField = new JTextField(String.valueOf(existing.getQuota()));
        JTextField crField = new JTextField(String.valueOf(existing.getCredit()));

        p.add(new JLabel("Course Code:"));
        p.add(cField);
        p.add(new JLabel("Course Name:"));
        p.add(nField);
        p.add(iLbl);
        p.add(iSearch);
        p.add(new JLabel("Select Instructor:"));
        p.add(iCombo);
        p.add(new JLabel("Quota:"));
        p.add(qField);
        p.add(new JLabel("Credits:"));
        p.add(crField);

        int result = JOptionPane.showConfirmDialog(this, p, "Update Course", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nField.getText();
                String sel = (String) iCombo.getSelectedItem();
                String inst = (sel != null) ? sel.split(" ")[0] : null;
                int q = Integer.parseInt(qField.getText());
                int cr = Integer.parseInt(crField.getText());

                Course updated = new Course(code, name, cr, q, inst);
                int res = dataStore.updateCourse(updated);
                if (res == 1) {
                    refresh();
                    JOptionPane.showMessageDialog(this, "Course updated successfully.");
                } else if (res == 0) {
                    JOptionPane.showMessageDialog(this, "No changes detected.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error updating course.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid numeric input.");
            }
        }
    }

    @Override
    public void refresh() {
        String selectedCode = null;
        if (adminCoursesTable != null && adminCoursesTable.getSelectedRow() >= 0) {
            selectedCode = (String) adminCoursesTableModel.getValueAt(adminCoursesTable.convertRowIndexToModel(adminCoursesTable.getSelectedRow()), 0);
        }

        if (adminCoursesTableModel != null) {
            adminCoursesTableModel.setRowCount(0);
            dataStore.getCourses().forEach(c -> adminCoursesTableModel.addRow(new Object[] { c.getCourseCode(), c.getCourseName(),
                    c.getInstructorUsername(), c.getQuota(), c.getCredit() }));

            if (selectedCode != null) {
                for (int i = 0; i < adminCoursesTableModel.getRowCount(); i++) {
                    if (selectedCode.equals(adminCoursesTableModel.getValueAt(i, 0))) {
                        int viewIdx = adminCoursesTable.convertRowIndexToView(i);
                        if (viewIdx >= 0) {
                            adminCoursesTable.setRowSelectionInterval(viewIdx, viewIdx);
                        }
                        break;
                    }
                }
            }
        }
    }
}
