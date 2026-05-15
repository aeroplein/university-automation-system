package ui.admin;

import data.DataStore;
import model.Course;
import model.CurriculumMapping;
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

    public CurriculumManagementPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(0, 15));
        setBackground(UIUtils.WARM_BEIGE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        curriculumTableModel = new DefaultTableModel(new String[] { "Dept", "Year", "Course Code" }, 0) {
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

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(new JLabel("Dept: "));
        top.add(currDeptCombo);
        top.add(new JLabel("Year: "));
        top.add(currYearCombo);
        top.add(new JLabel("Course: "));
        top.add(courseCombo);
        top.add(addBtn);

        JPanel topWrapper = new JPanel(new BorderLayout(0, 5));
        topWrapper.setOpaque(false);
        topWrapper.add(top, BorderLayout.NORTH);

        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        UIUtils.setupSearch(searchWrap, curriculumTableModel, curriculumTable);
        topWrapper.add(searchWrap, BorderLayout.CENTER);

        add(topWrapper, BorderLayout.NORTH);
        add(new JScrollPane(curriculumTable), BorderLayout.CENTER);

        refresh();
    }

    @Override
    public void refresh() {
        if (curriculumTableModel != null) {
            curriculumTableModel.setRowCount(0);
            String dept = (String) currDeptCombo.getSelectedItem();
            Integer year = (Integer) currYearCombo.getSelectedItem();

            List<CurriculumMapping> mappings = dataStore.getAllCurriculumMappings();
            for (CurriculumMapping m : mappings) {
                if (dept != null && !m.getDepartmentCode().equals(dept)) continue;
                if (year != null && m.getYear() != year) continue;
                curriculumTableModel.addRow(new Object[] { m.getDepartmentCode(), m.getYear(), m.getCourseCode() });
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
