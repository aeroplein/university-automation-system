package ui.admin;

import data.DataStore;
import model.Department;
import model.User;
import ui.shared.Refreshable;
import ui.shared.UIUtils;
import util.InputValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserManagementPanel extends JPanel implements Refreshable {
    private DataStore dataStore;
    private DefaultTableModel usersTableModel;
    private JTable usersTable;

    public UserManagementPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(0, 15));
        setBackground(UIUtils.WARM_BEIGE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder("User Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField uField = new JTextField(20);
        JPasswordField pField = new JPasswordField(20);
        JComboBox<String> rCombo = new JComboBox<>(new String[] { "Admin", "Instructor", "Student" });
        JComboBox<String> uDeptCombo = new JComboBox<>(dataStore.getDepartments().stream().map(Department::getCode).toArray(String[]::new));
        uDeptCombo.insertItemAt("-", 0);
        uDeptCombo.setSelectedIndex(0);
        JTextField nField = new JTextField(20);
        JTextField rIdField = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(uField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel pGrp = new JPanel(new BorderLayout(5, 0));
        pGrp.setOpaque(false);
        pGrp.add(pField, BorderLayout.CENTER);
        JButton showPBtn = new JButton("Show");
        showPBtn.setPreferredSize(new Dimension(70, 20));
        showPBtn.addActionListener(e -> {
            if (pField.getEchoChar() == (char) 0) {
                pField.setEchoChar('●');
                showPBtn.setText("Show");
            } else {
                pField.setEchoChar((char) 0);
                showPBtn.setText("Hide");
            }
        });
        pGrp.add(showPBtn, BorderLayout.EAST);
        formPanel.add(pGrp, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(rCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Dept:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(uDeptCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(nField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        formPanel.add(new JLabel("Reference ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPanel rGrp = new JPanel(new BorderLayout(5, 0));
        rGrp.setOpaque(false);
        rGrp.add(rIdField, BorderLayout.CENTER);
        JButton genRBtn = new JButton("Gen");
        genRBtn.addActionListener(e -> {
            String role = (String) rCombo.getSelectedItem();
            int count = (int) dataStore.getUsers().stream()
                    .filter(u -> u.getRole().equalsIgnoreCase(role))
                    .count();
            rIdField.setText(InputValidator.generateReferenceId(role, count + 1));
        });
        rGrp.add(genRBtn, BorderLayout.EAST);
        formPanel.add(rGrp, gbc);

        usersTableModel = new DefaultTableModel(new String[] { "Username", "Role", "Dept", "Full Name", "Ref ID" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        usersTable = UIUtils.createModernTable(usersTableModel);

        JPanel btnGrp = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnGrp.setOpaque(false);
        JButton addBtn = new JButton("Add User");
        addBtn.setBackground(UIUtils.SAGE_GREEN);
        addBtn.setForeground(Color.WHITE);
        JButton updBtn = new JButton("Update User");
        updBtn.setBackground(UIUtils.SKY_BLUE);
        updBtn.setForeground(Color.WHITE);
        JButton delBtn = new JButton("Delete User");
        delBtn.setBackground(UIUtils.TERRACOTTA);
        delBtn.setForeground(Color.WHITE);

        btnGrp.add(addBtn);
        btnGrp.add(updBtn);
        btnGrp.add(delBtn);

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.add(formPanel, BorderLayout.NORTH);

        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        UIUtils.setupSearch(searchWrap, usersTableModel, usersTable);
        topWrapper.add(searchWrap, BorderLayout.CENTER);
        topWrapper.add(btnGrp, BorderLayout.SOUTH);

        add(topWrapper, BorderLayout.NORTH);
        add(new JScrollPane(usersTable), BorderLayout.CENTER);

        usersTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = usersTable.getSelectedRow();
            if (row >= 0) {
                row = usersTable.convertRowIndexToModel(row);
                uField.setText((String) usersTableModel.getValueAt(row, 0));
                rCombo.setSelectedItem((String) usersTableModel.getValueAt(row, 1));
                uDeptCombo.setSelectedItem((String) usersTableModel.getValueAt(row, 2));
                nField.setText((String) usersTableModel.getValueAt(row, 3));
                rIdField.setText((String) usersTableModel.getValueAt(row, 4));
                User u = dataStore.findUser((String) usersTableModel.getValueAt(row, 0));
                if (u != null) pField.setText(u.getPassword());
            }
        });

        addBtn.addActionListener(e -> {
            User u = new User(uField.getText(), new String(pField.getPassword()), (String) rCombo.getSelectedItem(),
                    nField.getText(), rIdField.getText(), (String) uDeptCombo.getSelectedItem());
            int result = dataStore.addUser(u);
            if (result == 1) {
                refresh();
                JOptionPane.showMessageDialog(this, "User added successfully.");
            } else if (result == 0) {
                JOptionPane.showMessageDialog(this, "Username cannot be empty.");
            } else {
                JOptionPane.showMessageDialog(this, "User already exists.");
            }
        });

        updBtn.addActionListener(e -> {
            User u = new User(uField.getText(), new String(pField.getPassword()), (String) rCombo.getSelectedItem(),
                    nField.getText(), rIdField.getText(), (String) uDeptCombo.getSelectedItem());
            int result = dataStore.updateUser(u);
            if (result == 1) {
                refresh();
                JOptionPane.showMessageDialog(this, "User updated successfully.");
            } else if (result == 0) {
                JOptionPane.showMessageDialog(this, "No changes detected. Nothing to update.", "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "User not found. Cannot update.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        delBtn.addActionListener(e -> {
            String target = uField.getText().trim();
            if (target.isEmpty()) return;

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete user: " + target + "?\nThis will also delete any associated student profiles and grades.",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (dataStore.deleteUser(target)) {
                    refresh();
                    uField.setText("");
                    pField.setText("");
                    nField.setText("");
                    rIdField.setText("");
                    JOptionPane.showMessageDialog(this, "User and all associated data deleted successfully.");
                } else {
                    if ("admin".equalsIgnoreCase(target)) {
                        JOptionPane.showMessageDialog(this, "The system 'admin' account is protected and cannot be deleted.", "Protection Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "User not found or could not be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        refresh();
    }

    @Override
    public void refresh() {
        String selectedUsername = null;
        if (usersTable != null && usersTable.getSelectedRow() >= 0) {
            selectedUsername = (String) usersTableModel.getValueAt(usersTable.convertRowIndexToModel(usersTable.getSelectedRow()), 0);
        }

        if (usersTableModel != null) {
            usersTableModel.setRowCount(0);
            dataStore.getUsers().forEach(
                    u -> usersTableModel.addRow(new Object[]{u.getUsername(), u.getRole(), u.getDepartment(), u.getFullName(), u.getReferenceId()}));

            if (selectedUsername != null) {
                for (int i = 0; i < usersTableModel.getRowCount(); i++) {
                    if (selectedUsername.equals(usersTableModel.getValueAt(i, 0))) {
                        int viewIdx = usersTable.convertRowIndexToView(i);
                        if (viewIdx >= 0) {
                            usersTable.setRowSelectionInterval(viewIdx, viewIdx);
                        }
                        break;
                    }
                }
            }
        }
    }
}
