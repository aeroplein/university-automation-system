package ui.admin;

import data.DataStore;
import model.Course;
import model.CurriculumMapping; // Model for linking course to department & year
import model.Department;
import ui.shared.Refreshable;
import ui.shared.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CurriculumManagementPanel extends JPanel implements Refreshable {
    private DataStore dataStore;
    private DefaultTableModel curriculumTableModel;
    private JTable curriculumTable;
    private JComboBox<String> currDeptCombo;
    private JComboBox<Integer> currYearCombo;
    private JComboBox<String> courseCombo;
    private JLabel totalsLabel;

    public CurriculumManagementPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(0, 15));
        setBackground(UIUtils.WARM_BEIGE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        curriculumTableModel = new DefaultTableModel(new String[] { "Dept", "Year", "Course Code", "Course Name", "Credits", "ECTS" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        curriculumTable = UIUtils.createModernTable(curriculumTableModel);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        form.setOpaque(false);
        currDeptCombo = new JComboBox<>(dataStore.getDepartments().stream().map(Department::getCode).toArray(String[]::new));
        currYearCombo = new JComboBox<>(new Integer[] { 1, 2, 3, 4 });
        courseCombo = new JComboBox<>(dataStore.getCourses().stream().map(Course::getCourseCode).toArray(String[]::new));

        currDeptCombo.addActionListener(e -> refresh());
        currYearCombo.addActionListener(e -> refresh());

        JButton addBtn = new JButton("Assign to Curriculum");
        addBtn.setBackground(UIUtils.SAGE_GREEN);
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> {
            String d = (String) currDeptCombo.getSelectedItem();
            Integer y = (Integer) currYearCombo.getSelectedItem();
            String c = (String) courseCombo.getSelectedItem();
            if (d != null && y != null && c != null) {
                if (dataStore.addCurriculumMapping(new CurriculumMapping(d, y, c))) {
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this, "This course is already assigned to this department and year.",
                            "Duplicate", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        JButton delBtn = new JButton("Delete from Curriculum");
        delBtn.setBackground(UIUtils.TERRACOTTA);
        delBtn.setForeground(Color.WHITE);
        delBtn.addActionListener(e -> {
            int row = curriculumTable.getSelectedRow();
            if (row >= 0) {
                row = curriculumTable.convertRowIndexToModel(row);
                String d = (String) curriculumTableModel.getValueAt(row, 0);
                Integer y = (Integer) curriculumTableModel.getValueAt(row, 1);
                String c = (String) curriculumTableModel.getValueAt(row, 2);

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to remove course " + c + " from " + d + " Year " + y + " curriculum?",
                        "Confirm Removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    dataStore.removeCurriculumMapping(d, y, c);
                    refresh();
                    JOptionPane.showMessageDialog(this, "Course successfully removed from curriculum.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a curriculum mapping from the table to delete.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(new JLabel("Dept: "));
        top.add(currDeptCombo);
        top.add(new JLabel("Year: "));
        top.add(currYearCombo);
        top.add(new JLabel("Course: "));
        top.add(courseCombo);
        top.add(addBtn);
        top.add(delBtn);

        JPanel topWrapper = new JPanel(new BorderLayout(0, 5));
        topWrapper.setOpaque(false);
        topWrapper.add(top, BorderLayout.NORTH);

        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        UIUtils.setupSearch(searchWrap, curriculumTableModel, curriculumTable);
        topWrapper.add(searchWrap, BorderLayout.CENTER);

        add(topWrapper, BorderLayout.NORTH);
        add(new JScrollPane(curriculumTable), BorderLayout.CENTER);

        totalsLabel = new JLabel("Curriculum Totals - Selected Credits: 0 | Selected ECTS: 0");
        totalsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalsLabel.setForeground(UIUtils.COCOA_BROWN);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(totalsLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        refresh();
    }

    @Override
    public void refresh() {
        if (curriculumTableModel != null) {
            curriculumTableModel.setRowCount(0);
            String dept = (String) currDeptCombo.getSelectedItem();
            Integer year = (Integer) currYearCombo.getSelectedItem();

            int totalCredits = 0;
            int totalEcts = 0;
            List<CurriculumMapping> mappings = dataStore.getAllCurriculumMappings();
            for (CurriculumMapping m : mappings) {
                if (dept != null && !m.getDepartmentCode().equals(dept)) continue;
                if (year != null && m.getYear() != year) continue;
                Course c = dataStore.findCourse(m.getCourseCode());
                String cName = c != null ? c.getCourseName() : "-";
                int ects = c != null ? c.getCredit() : 0;
                int credits = c != null ? Math.max(1, (int) Math.round(ects * 0.6)) : 0;
                
                totalCredits += credits;
                totalEcts += ects;
                
                curriculumTableModel.addRow(new Object[] { m.getDepartmentCode(), m.getYear(), m.getCourseCode(), cName, credits, ects });
            }
            if (totalsLabel != null) {
                totalsLabel.setText("Curriculum Totals - Selected Credits: " + totalCredits + " | Selected ECTS: " + totalEcts);
            }
        }

        // Update dept and course combo lists
        if (currDeptCombo != null) {
            Object sel = currDeptCombo.getSelectedItem();
            currDeptCombo.setModel(new DefaultComboBoxModel<>(dataStore.getDepartments().stream().map(Department::getCode).toArray(String[]::new)));
            if (sel != null) currDeptCombo.setSelectedItem(sel);
        }

        if (courseCombo != null) {
            Object sel = courseCombo.getSelectedItem();
            courseCombo.setModel(new DefaultComboBoxModel<>(dataStore.getCourses().stream().map(Course::getCourseCode).toArray(String[]::new)));
            if (sel != null) courseCombo.setSelectedItem(sel);
        }
    }
}
