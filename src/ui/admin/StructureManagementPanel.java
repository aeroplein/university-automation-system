package ui.admin;

import data.DataStore;
import model.Department;
import model.Faculty;
import ui.shared.Refreshable;
import ui.shared.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StructureManagementPanel extends JPanel implements Refreshable {
    private DataStore dataStore;
    private DefaultTableModel facultiesTableModel;
    private JTable facultiesTable;
    private DefaultTableModel departmentsTableModel;
    private JTable departmentsTable;

    private JComboBox<String> fParentCombo;

    public StructureManagementPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(0, 15));
        setBackground(UIUtils.WARM_BEIGE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIUtils.WARM_BEIGE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Manage Structural Entities"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JComboBox<String> typeCombo = new JComboBox<>(new String[] { "Faculty", "Department" });
        JTextField codeField = new JTextField(10);
        JTextField nameField = new JTextField(20);
        JLabel parentLabel = new JLabel("Parent Faculty:");
        fParentCombo = new JComboBox<>(dataStore.getFaculties().stream().map(Faculty::getCode).toArray(String[]::new));

        parentLabel.setVisible(false);
        fParentCombo.setVisible(false);

        typeCombo.addActionListener(e -> {
            boolean isDept = "Department".equals(typeCombo.getSelectedItem());
            parentLabel.setVisible(isDept);
            fParentCombo.setVisible(isDept);
            codeField.setText("");
            nameField.setText("");
            formPanel.revalidate();
            formPanel.repaint();
        });

        gbc.insets = new Insets(5, 5, 5, 10);
        gbc.anchor = GridBagConstraints.EAST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(typeCombo, gbc);

        gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Code:"), gbc);
        gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(codeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(parentLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(fParentCombo, gbc);

        JButton saveBtn = new JButton("Save Structure");
        saveBtn.setBackground(UIUtils.SAGE_GREEN);
        saveBtn.setForeground(Color.WHITE);
        gbc.gridx = 2; gbc.gridy = 3; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(saveBtn, gbc);

        JButton delBtn = new JButton("Delete Selected");
        delBtn.setBackground(UIUtils.TERRACOTTA);
        delBtn.setForeground(Color.WHITE);
        gbc.gridx = 3; gbc.gridy = 3;
        formPanel.add(delBtn, gbc);

        add(formPanel, BorderLayout.NORTH);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        tablesPanel.setOpaque(false);

        JPanel fPanel = new JPanel(new BorderLayout(0, 10));
        fPanel.setOpaque(false);
        fPanel.setBorder(BorderFactory.createTitledBorder("Faculties"));
        facultiesTableModel = new DefaultTableModel(new String[] { "Code", "Name" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        facultiesTable = UIUtils.createModernTable(facultiesTableModel);
        UIUtils.setupSearch(fPanel, facultiesTableModel, facultiesTable);
        fPanel.add(new JScrollPane(facultiesTable), BorderLayout.CENTER);
        tablesPanel.add(fPanel);

        JPanel dPanel = new JPanel(new BorderLayout(0, 10));
        dPanel.setOpaque(false);
        dPanel.setBorder(BorderFactory.createTitledBorder("Departments"));
        departmentsTableModel = new DefaultTableModel(new String[] { "Code", "Name", "Faculty" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        departmentsTable = UIUtils.createModernTable(departmentsTableModel);
        UIUtils.setupSearch(dPanel, departmentsTableModel, departmentsTable);
        dPanel.add(new JScrollPane(departmentsTable), BorderLayout.CENTER);
        tablesPanel.add(dPanel);

        add(tablesPanel, BorderLayout.CENTER);

        facultiesTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = facultiesTable.getSelectedRow();
            if (row >= 0) {
                row = facultiesTable.convertRowIndexToModel(row);
                typeCombo.setSelectedItem("Faculty");
                codeField.setText((String) facultiesTableModel.getValueAt(row, 0));
                nameField.setText((String) facultiesTableModel.getValueAt(row, 1));
            }
        });

        departmentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = departmentsTable.getSelectedRow();
            if (row >= 0) {
                row = departmentsTable.convertRowIndexToModel(row);
                typeCombo.setSelectedItem("Department");
                codeField.setText((String) departmentsTableModel.getValueAt(row, 0));
                nameField.setText((String) departmentsTableModel.getValueAt(row, 1));
                fParentCombo.setSelectedItem((String) departmentsTableModel.getValueAt(row, 2));
            }
        });

        saveBtn.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            String code = codeField.getText().toUpperCase();
            String name = nameField.getText();

            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Code and Name are required.");
                return;
            }

            if ("Faculty".equals(type)) {
                Faculty f = new Faculty(code, name);
                boolean exists = dataStore.findFaculty(code) != null;
                if (exists) {
                    int res = dataStore.updateFaculty(f);
                    if (res == 1) {
                        refresh();
                        JOptionPane.showMessageDialog(this, "Faculty updated successfully.");
                    } else if (res == 0) {
                        JOptionPane.showMessageDialog(this, "No changes detected.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Error updating faculty.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    if (dataStore.addFaculty(f)) {
                        refresh();
                        JOptionPane.showMessageDialog(this, "Faculty added successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add faculty.");
                    }
                }
            } else {
                String parent = (String) fParentCombo.getSelectedItem();
                if (parent == null) {
                    JOptionPane.showMessageDialog(this, "Select a parent faculty.");
                    return;
                }
                Department d = new Department(code, name, parent);
                boolean exists = dataStore.findDepartment(code) != null;
                if (exists) {
                    int res = dataStore.updateDepartment(d);
                    if (res == 1) {
                        refresh();
                        JOptionPane.showMessageDialog(this, "Department updated successfully.");
                    } else if (res == 0) {
                        JOptionPane.showMessageDialog(this, "No changes detected.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Error updating department.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    if (dataStore.addDepartment(d)) {
                        refresh();
                        JOptionPane.showMessageDialog(this, "Department added successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add department.");
                    }
                }
            }
            // Update combo
            fParentCombo.setModel(new DefaultComboBoxModel<>(dataStore.getFaculties().stream().map(Faculty::getCode).toArray(String[]::new)));
        });

        delBtn.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            String code = codeField.getText().toUpperCase();
            if (code.isEmpty()) return;

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this " + type + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success;
                if ("Faculty".equals(type)) {
                    success = dataStore.deleteFaculty(code);
                } else {
                    success = dataStore.deleteDepartment(code);
                }

                if (success) {
                    refresh();
                    codeField.setText("");
                    nameField.setText("");
                    JOptionPane.showMessageDialog(this, type + " deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Could not find " + type + " to delete.");
                }
            }
        });

        refresh();
    }

    @Override
    public void refresh() {
        String selF = null;
        if (facultiesTable != null && facultiesTable.getSelectedRow() >= 0) {
            selF = (String) facultiesTableModel.getValueAt(facultiesTable.convertRowIndexToModel(facultiesTable.getSelectedRow()), 0);
        }
        String selD = null;
        if (departmentsTable != null && departmentsTable.getSelectedRow() >= 0) {
            selD = (String) departmentsTableModel.getValueAt(departmentsTable.convertRowIndexToModel(departmentsTable.getSelectedRow()), 0);
        }

        if (facultiesTableModel != null) {
            facultiesTableModel.setRowCount(0);
            dataStore.getFaculties().forEach(f -> facultiesTableModel.addRow(new Object[] { f.getCode(), f.getName() }));
            if (selF != null) {
                for (int i = 0; i < facultiesTableModel.getRowCount(); i++) {
                    if (selF.equals(facultiesTableModel.getValueAt(i, 0))) {
                        facultiesTable.setRowSelectionInterval(facultiesTable.convertRowIndexToView(i),
                                facultiesTable.convertRowIndexToView(i));
                        break;
                    }
                }
            }
        }
        if (departmentsTableModel != null) {
            departmentsTableModel.setRowCount(0);
            dataStore.getDepartments().forEach(d -> departmentsTableModel.addRow(new Object[] { d.getCode(), d.getName(), d.getFacultyCode() }));
            if (selD != null) {
                for (int i = 0; i < departmentsTableModel.getRowCount(); i++) {
                    if (selD.equals(departmentsTableModel.getValueAt(i, 0))) {
                        departmentsTable.setRowSelectionInterval(departmentsTable.convertRowIndexToView(i),
                                departmentsTable.convertRowIndexToView(i));
                        break;
                    }
                }
            }
        }

        if (fParentCombo != null) {
            Object selected = fParentCombo.getSelectedItem();
            fParentCombo.setModel(new DefaultComboBoxModel<>(dataStore.getFaculties().stream().map(Faculty::getCode).toArray(String[]::new)));
            if (selected != null) fParentCombo.setSelectedItem(selected);
        }
    }
}
