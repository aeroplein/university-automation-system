package ui.admin;

import data.DataStore;
import model.Department;
import model.StudentProfile;
import model.User;
import ui.shared.Refreshable;
import ui.shared.UIUtils;
import util.InputValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentManagementPanel extends JPanel implements Refreshable {
    private DataStore dataStore;
    private DefaultTableModel studentModel;
    private JTable studentTable;
    private JComboBox<String> studentUserCombo;
    private JTextField idField;
    private JTextField nameField;
    private JComboBox<String> deptCombo;
    private JComboBox<Integer> yearCombo;
    private JComboBox<String> secondDeptCombo;
    private JComboBox<Integer> secondYearCombo;

    private boolean studentPendingOnly = false;

    public StudentManagementPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(0, 10));
        setBackground(UIUtils.WARM_BEIGE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initUI();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Manage Student Profiles", TitledBorder.LEFT, TitledBorder.TOP));
        formPanel.setBackground(UIUtils.WARM_BEIGE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        idField = new JTextField(15);
        nameField = new JTextField(20);
        nameField.setEditable(false);
        nameField.setBackground(new Color(0xF5F5F5));
        deptCombo = new JComboBox<>(dataStore.getDepartments().stream().map(Department::getCode).toArray(String[]::new));
        yearCombo = new JComboBox<>(new Integer[] { 1, 2, 3, 4 });
        studentUserCombo = new JComboBox<>();

        secondDeptCombo = new JComboBox<>(dataStore.getDepartments().stream().map(Department::getCode).toArray(String[]::new));
        secondDeptCombo.insertItemAt("", 0);
        secondDeptCombo.setSelectedIndex(0);
        secondYearCombo = new JComboBox<>(new Integer[] { 0, 1, 2, 3, 4 });

        studentUserCombo.addActionListener(e -> {
            String selectedItem = (String) studentUserCombo.getSelectedItem();
            if (selectedItem == null) return;

            String selectedUser = selectedItem.split(" ")[0];

            if (selectedUser != null) {
                User u = dataStore.findUser(selectedUser);
                if (u != null) {
                    nameField.setText(u.getFullName());
                }

                StudentProfile p = dataStore.findStudentProfileByUsername(selectedUser);
                if (p != null) {
                    idField.setText(p.getStudentId());
                    deptCombo.setSelectedItem(p.getDepartment());
                    yearCombo.setSelectedItem(p.getYear());
                    secondDeptCombo.setSelectedItem(p.getSecondDepartment() == null ? "" : p.getSecondDepartment());
                    secondYearCombo.setSelectedItem(p.getSecondYear());
                } else {
                    idField.setText("");
                }
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        JPanel idGrp = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        idGrp.setOpaque(false);
        idGrp.add(idField);
        JButton genBtn = new JButton("Generate");
        genBtn.addActionListener(e -> {
            int currentYear = 2025;
            int studyYear = (Integer) yearCombo.getSelectedItem();
            int entranceYear = currentYear - studyYear + 1;
            idField.setText(InputValidator.generateStudentId(entranceYear, (String) deptCombo.getSelectedItem()));
        });
        idGrp.add(genBtn);
        formPanel.add(idGrp, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Primary Dept:"), gbc);
        gbc.gridx = 3;
        formPanel.add(deptCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Primary Year:"), gbc);
        gbc.gridx = 3;
        formPanel.add(yearCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(studentUserCombo, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Second Dept:"), gbc);
        gbc.gridx = 3;
        formPanel.add(secondDeptCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Second Year:"), gbc);
        gbc.gridx = 1;
        formPanel.add(secondYearCombo, gbc);

        JPanel btnGrp = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnGrp.setOpaque(false);
        JButton saveBtn = new JButton("Save Profile");
        saveBtn.setBackground(UIUtils.SAGE_GREEN);
        saveBtn.setForeground(Color.WHITE);
        JButton deleteBtn = new JButton("Delete Profile");
        deleteBtn.setBackground(new Color(0xD9534F));
        deleteBtn.setForeground(Color.WHITE);

        btnGrp.add(saveBtn);
        btnGrp.add(deleteBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        gbc.insets = new Insets(15, 10, 10, 10);
        formPanel.add(btnGrp, gbc);

        studentModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Major", "Major Yr", "2nd Major", "2nd Major Yr", "Username" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        studentTable = UIUtils.createModernTable(studentModel);

        JPanel topWrapper = new JPanel(new BorderLayout(0, 5));
        topWrapper.setOpaque(false);
        topWrapper.add(formPanel, BorderLayout.NORTH);

        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        UIUtils.setupSearch(searchWrap, studentModel, studentTable);
        topWrapper.add(searchWrap, BorderLayout.CENTER);

        add(topWrapper, BorderLayout.NORTH);
        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = studentTable.getSelectedRow();
            if (row >= 0) {
                row = studentTable.convertRowIndexToModel(row);
                String id = (String) studentModel.getValueAt(row, 0);
                String fullName = (String) studentModel.getValueAt(row, 1);
                String username = (String) studentModel.getValueAt(row, 6);

                idField.setText("(PENDING)".equals(id) ? "" : id);
                nameField.setText(fullName);

                boolean found = false;
                for (int i = 0; i < studentUserCombo.getItemCount(); i++) {
                    String item = studentUserCombo.getItemAt(i);
                    if (item != null && item.split(" ")[0].equals(username)) {
                        studentUserCombo.setSelectedIndex(i);
                        found = true;
                        break;
                    }
                }

                if (!found) studentUserCombo.setSelectedIndex(-1);

                deptCombo.setSelectedItem(studentModel.getValueAt(row, 2));
                yearCombo.setSelectedItem(studentModel.getValueAt(row, 3));

                Object sDept = studentModel.getValueAt(row, 4);
                secondDeptCombo.setSelectedItem("-".equals(sDept) ? "" : sDept);

                Object sYr = studentModel.getValueAt(row, 5);
                secondYearCombo.setSelectedItem(sYr);
            }
        });

        studentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                String id = (String) t.getModel().getValueAt(t.convertRowIndexToModel(row), 0);
                if ("(PENDING)".equals(id)) {
                    c.setForeground(Color.GRAY);
                    c.setFont(c.getFont().deriveFont(Font.ITALIC));
                } else {
                    c.setForeground(t.getForeground());
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                }
                if (isSelected) {
                    c.setBackground(t.getSelectionBackground());
                    c.setForeground(t.getSelectionForeground());
                } else {
                    c.setBackground(t.getBackground());
                }
                ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        saveBtn.addActionListener(e -> {
            String id = idField.getText();
            String err = InputValidator.validateStudentId(id);
            if (err != null) {
                JOptionPane.showMessageDialog(this, err, "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedItem = (String) studentUserCombo.getSelectedItem();
            if (selectedItem == null) return;
            String actualUsername = selectedItem.split(" ")[0];

            StudentProfile p = new StudentProfile(id, nameField.getText(), (String) deptCombo.getSelectedItem(),
                    (String) secondDeptCombo.getSelectedItem(), (Integer) yearCombo.getSelectedItem(),
                    (Integer) secondYearCombo.getSelectedItem(), actualUsername);

            boolean exists = dataStore.findStudentProfileByUsername(actualUsername) != null || dataStore.findStudentProfileById(id) != null;
            if (exists) {
                int res = dataStore.updateStudentProfile(p);
                if (res == 1) {
                    refresh();
                    JOptionPane.showMessageDialog(this, "Profile updated successfully.");
                } else if (res == 0) {
                    JOptionPane.showMessageDialog(this, "No changes detected. Nothing to update.", "Information", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update. Check seniority (Primary Year >= Second Year).", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (dataStore.addStudentProfile(p)) {
                    refresh();
                    JOptionPane.showMessageDialog(this, "Profile created successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create profile. Check for unique ID/Username.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a student from the table first.");
                return;
            }

            if (JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this profile?\nThis will also delete the associated user account and all academic records.") == JOptionPane.YES_OPTION) {
                if (dataStore.deleteStudent(id)) {
                    refresh();
                    idField.setText("");
                    nameField.setText("");
                    studentUserCombo.setSelectedIndex(-1);
                    JOptionPane.showMessageDialog(this, "Student profile deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting student profile.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        refresh();
    }

    private void updateStudentUserCombo() {
        if (studentUserCombo != null) {
            String currentSel = (String) studentUserCombo.getSelectedItem();
            studentUserCombo.removeAllItems();

            dataStore.getUsers().stream()
                .filter(u -> "Student".equalsIgnoreCase(u.getRole()))
                .forEach(u -> {
                    boolean hasProfile = dataStore.findStudentProfileByUsername(u.getUsername()) != null;
                    if (!studentPendingOnly || !hasProfile) {
                        String label = u.getUsername() + (hasProfile ? "" : " (PENDING)");
                        studentUserCombo.addItem(label);
                    }
                });

            if (currentSel != null) studentUserCombo.setSelectedItem(currentSel);
        }
    }

    @Override
    public void refresh() {
        String selectedId = null;
        if (studentTable != null && studentTable.getSelectedRow() >= 0) {
            selectedId = (String) studentModel.getValueAt(studentTable.convertRowIndexToModel(studentTable.getSelectedRow()), 0);
        }

        if (studentModel != null) {
            studentModel.setRowCount(0);

            dataStore.getStudents().forEach(s -> studentModel.addRow(new Object[] {
                    s.getStudentId(), s.getFullName(), s.getDepartment(), s.getYear(),
                    s.getSecondDepartment() != null ? s.getSecondDepartment() : "-",
                    s.getSecondYear(), s.getUsername()
            }));

            dataStore.getUsers().stream()
                .filter(u -> "Student".equalsIgnoreCase(u.getRole()))
                .filter(u -> dataStore.findStudentProfileByUsername(u.getUsername()) == null)
                .forEach(u -> studentModel.addRow(new Object[] {
                    "(PENDING)", u.getFullName(), "-", "-", "-", 0, u.getUsername()
                }));

            if (selectedId != null) {
                for (int i = 0; i < studentModel.getRowCount(); i++) {
                    if (selectedId.equals(studentModel.getValueAt(i, 0))) {
                        int viewIdx = studentTable.convertRowIndexToView(i);
                        if (viewIdx >= 0) {
                            studentTable.setRowSelectionInterval(viewIdx, viewIdx);
                        }
                        break;
                    }
                }
            }
        }

        updateStudentUserCombo();

        if (deptCombo != null) {
            Object selectedDept = deptCombo.getSelectedItem();
            deptCombo.setModel(new DefaultComboBoxModel<>(dataStore.getDepartments().stream().map(Department::getCode).toArray(String[]::new)));
            if (selectedDept != null) deptCombo.setSelectedItem(selectedDept);
        }
        if (secondDeptCombo != null) {
            Object selectedSDept = secondDeptCombo.getSelectedItem();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(dataStore.getDepartments().stream().map(Department::getCode).toArray(String[]::new));
            model.insertElementAt("", 0);
            secondDeptCombo.setModel(model);
            if (selectedSDept != null) secondDeptCombo.setSelectedItem(selectedSDept);
        }
    }
}
