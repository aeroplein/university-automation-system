package ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatIntelliJLaf;
import data.DataStore;
import model.*;
import util.InputValidator;
import util.TranscriptExporter;

import javax.swing.*;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

/**
 * University Automation System - Premium Tabbed UI
 * Features the 'UniAuto' Earth-Tone Palette and Tabbed Dashboard.
 */
public class UniversityAutomationApp extends JFrame {

    private DataStore dataStore;
    private User currentUser;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private static final String LOGIN_CARD = "LOGIN";
    private static final String DASHBOARD_CARD = "DASHBOARD";

    // Earth-Tone Palette
    private static final Color COCOA_BROWN = new Color(0x2D241E);
    private static final Color SAGE_GREEN = new Color(0x7C9070);
    private static final Color WARM_BEIGE = new Color(0xFDFBF7);
    private static final Color CREAM_SAND = new Color(0xE9EDDF);
    private static final Color TERRACOTTA = new Color(0xD98880);
    private static final Color SKY_BLUE = new Color(0x5D9CEC);

    // Dynamic UI components for refreshing
    private JComboBox<String> studentUserCombo;
    private boolean studentPendingOnly = false;
    private DefaultTableModel studentModel;
    private JTable studentTable;
    private DefaultTableModel myCoursesTableModel;
    private DefaultTableModel transcriptTableModel;
    private JLabel gpaLabel;

    public UniversityAutomationApp() {
        dataStore = DataStore.getInstance();
        setupGlobalUI();
        initializeUI();
    }

    private void setupGlobalUI() {
        try {
            FlatIntelliJLaf.setup();
            UIManager.put("Button.arc", 5);
            UIManager.put("Component.arc", 5);
            UIManager.put("TextComponent.arc", 5);
            UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
            UIManager.put("TabbedPane.background", CREAM_SAND);
            UIManager.put("Table.selectionBackground", SAGE_GREEN);
            UIManager.put("Table.selectionForeground", Color.WHITE);
        } catch (Exception e) {
            System.err.println("FlatLaf not found, using System Look & Feel");
        }
    }

    private void initializeUI() {
        setTitle("University Automation System");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(CREAM_SAND);

        mainPanel.add(showLoginPanel(), LOGIN_CARD);

        add(mainPanel);
        cardLayout.show(mainPanel, LOGIN_CARD);
    }

    private JPanel showLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CREAM_SAND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("University Automation System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(COCOA_BROWN);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 50, 0);
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel userLbl = new JLabel("Username:");
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(userLbl, gbc);

        JTextField userField = new JTextField(20);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        panel.add(userField, gbc);

        JLabel passLbl = new JLabel("Password:");
        passLbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(passLbl, gbc);

        JPasswordField passField = new JPasswordField(20);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        panel.add(passField, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(SAGE_GREEN);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginBtn.setPreferredSize(new Dimension(150, 45));
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(40, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginBtn, gbc);

        ActionListener loginAction = e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            User user = dataStore.authenticate(username, password);
            if (user != null) {
                currentUser = user;
                showDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        };
        loginBtn.addActionListener(loginAction);
        passField.addActionListener(loginAction);

        return panel;
    }

    private void showDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(WARM_BEIGE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COCOA_BROWN);
        header.setPreferredSize(new Dimension(1100, 60));
        header.setBorder(new EmptyBorder(0, 15, 0, 15));

        JLabel title = new JLabel(currentUser.getRole() + " Dashboard - Welcome, " + currentUser.getFullName());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(title, BorderLayout.WEST);

        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        headerButtons.setOpaque(false);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(SKY_BLUE);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> showDashboard());
        headerButtons.add(refreshBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(TERRACOTTA);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> {
            currentUser = null;
            cardLayout.show(mainPanel, LOGIN_CARD);
        });
        headerButtons.add(logoutBtn);

        header.add(headerButtons, BorderLayout.EAST);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        if ("Admin".equalsIgnoreCase(currentUser.getRole())) {
            setupAdminTabs(tabbedPane);
        } else if ("Instructor".equalsIgnoreCase(currentUser.getRole())) {
            setupInstructorTabs(tabbedPane);
        } else if ("Student".equalsIgnoreCase(currentUser.getRole())) {
            setupStudentTabs(tabbedPane);
        }

        dashboard.add(header, BorderLayout.NORTH);
        dashboard.add(tabbedPane, BorderLayout.CENTER);

        mainPanel.add(dashboard, DASHBOARD_CARD);
        cardLayout.show(mainPanel, DASHBOARD_CARD);
    }

    private void setupAdminTabs(JTabbedPane tabs) {
        tabs.addTab("User Management", createUsersPanel());
        tabs.addTab("Structural Management", createStructurePanel());
        tabs.addTab("Curriculum Management", createCurriculumPanel());
        tabs.addTab("Student Management", createStudentManagementPanel());
        tabs.addTab("Course Management", createCoursesPanel());
        tabs.addTab("System Reports", createReportsPanel());
    }

    private void setupInstructorTabs(JTabbedPane tabs) {
        tabs.addTab("My Courses", createInstructorCoursesPanel());
        tabs.addTab("Grade Entry", createGradeEntryPanel());
        tabs.addTab("Grade Distribution", createDistributionPanel());
    }

    private void setupStudentTabs(JTabbedPane tabs) {
        tabs.addTab("Available Courses", createAvailableCoursesPanel());
        tabs.addTab("My Courses", createMyCoursesPanel());
        tabs.addTab("Transcript", createTranscriptPanel());
    }

    // ==================== Modules ====================

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

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
        JTextField nField = new JTextField(20);
        JTextField rIdField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(uField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
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

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(rCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(nField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Reference ID:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        JPanel rGrp = new JPanel(new BorderLayout(5, 0));
        rGrp.setOpaque(false);
        rGrp.add(rIdField, BorderLayout.CENTER);
        JButton genRBtn = new JButton("Gen");
        genRBtn.addActionListener(e -> {
            String role = (String) rCombo.getSelectedItem();
            String prefix = "ADM";
            if ("Instructor".equals(role))
                prefix = "INST";
            else if ("Student".equals(role))
                prefix = "STU";
            else if ("Admin".equals(role))
                prefix = "ADMIN";

            final String fPrefix = prefix;
            long count = dataStore.getUsers().stream()
                    .filter(u -> u.getReferenceId() != null && u.getReferenceId().startsWith(fPrefix))
                    .count();
            rIdField.setText(fPrefix + String.format("%03d", count + 1));
        });
        rGrp.add(genRBtn, BorderLayout.EAST);
        formPanel.add(rGrp, gbc);

        DefaultTableModel model = new DefaultTableModel(new String[] { "Username", "Role", "Full Name", "Ref ID" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = createModernTable(model);

        JPanel btnGrp = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnGrp.setOpaque(false);
        JButton addBtn = new JButton("Add User");
        addBtn.setBackground(SAGE_GREEN);
        addBtn.setForeground(Color.WHITE);
        JButton updBtn = new JButton("Update User");
        updBtn.setBackground(SKY_BLUE);
        updBtn.setForeground(Color.WHITE);
        JButton delBtn = new JButton("Delete User");
        delBtn.setBackground(TERRACOTTA);
        delBtn.setForeground(Color.WHITE);

        btnGrp.add(addBtn);
        btnGrp.add(updBtn);
        btnGrp.add(delBtn);

        JPanel topContainer = new JPanel(new BorderLayout(0, 10));
        topContainer.setOpaque(false);
        topContainer.add(formPanel, BorderLayout.CENTER);
        topContainer.add(btnGrp, BorderLayout.SOUTH);
        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Search
        JTextField search = new JTextField(20);
        JPanel searchP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchP.setOpaque(false);
        searchP.add(new JLabel("Search Users:"));
        searchP.add(search);
        panel.add(searchP, BorderLayout.SOUTH);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        search.addCaretListener(e -> {
            String val = search.getText();
            if (val.isEmpty())
                sorter.setRowFilter(null);
            else
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + val));
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;
            int row = table.getSelectedRow();
            if (row >= 0) {
                row = table.convertRowIndexToModel(row);
                uField.setText((String) model.getValueAt(row, 0));
                rCombo.setSelectedItem((String) model.getValueAt(row, 1));
                nField.setText((String) model.getValueAt(row, 2));
                rIdField.setText((String) model.getValueAt(row, 3));
                User u = dataStore.findUser((String) model.getValueAt(row, 0));
                if (u != null)
                    pField.setText(u.getPassword());
            }
        });

        addBtn.addActionListener(e -> {
            User u = new User(uField.getText(), new String(pField.getPassword()), (String) rCombo.getSelectedItem(),
                    nField.getText(),
                    rIdField.getText());
            if (dataStore.addUser(u)) {
                refreshUserTable(model, table);
                updateStudentUserCombo(studentPendingOnly);
                refreshStudentTable(studentModel, studentTable); // Update Ghost Rows
            } else
                JOptionPane.showMessageDialog(panel, "User already exists.");
        });

        updBtn.addActionListener(e -> {
            User u = new User(uField.getText(), new String(pField.getPassword()), (String) rCombo.getSelectedItem(),
                    nField.getText(),
                    rIdField.getText());
            int result = dataStore.updateUser(u);
            if (result == 1) {
                refreshUserTable(model, table);
                JOptionPane.showMessageDialog(panel, "User updated successfully.");
            } else if (result == 0) {
                JOptionPane.showMessageDialog(panel, "No changes detected. Nothing to update.", "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panel, "User not found. Cannot update.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        delBtn.addActionListener(e -> {
            String target = uField.getText().trim();
            if (target.isEmpty())
                return;

            int confirm = JOptionPane.showConfirmDialog(panel,
                    "Are you sure you want to delete user: " + target
                            + "?\nThis will also delete any associated student profiles and grades.",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (dataStore.deleteUser(target)) {
                    refreshUserTable(model, table);
                    updateStudentUserCombo(studentPendingOnly);
                    refreshStudentTable(studentModel, studentTable);
                    // Clear form
                    uField.setText("");
                    pField.setText("");
                    nField.setText("");
                    rIdField.setText("");
                    JOptionPane.showMessageDialog(panel, "User and all associated data deleted successfully.");
                } else {
                    if ("admin".equalsIgnoreCase(target)) {
                        JOptionPane.showMessageDialog(panel,
                                "The system 'admin' account is protected and cannot be deleted.", "Protection Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(panel, "User not found or could not be deleted.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        refreshUserTable(model, table);
        return panel;
    }

    private JPanel createStudentManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Manage Student Profiles", TitledBorder.LEFT, TitledBorder.TOP));
        formPanel.setBackground(WARM_BEIGE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField idField = new JTextField(15);
        JTextField nameField = new JTextField(20);
        nameField.setEditable(false);
        nameField.setBackground(new Color(0xF5F5F5)); // Light gray to indicate read-only
        JComboBox<String> deptCombo = new JComboBox<>(
                dataStore.getDepartments().stream().map(Department::getCode).toArray(String[]::new));
        JComboBox<Integer> yearCombo = new JComboBox<>(new Integer[] { 1, 2, 3, 4 });
        studentUserCombo = new JComboBox<>();
        updateStudentUserCombo(studentPendingOnly);

        JComboBox<String> secondDeptCombo = new JComboBox<>(
                dataStore.getDepartments().stream().map(Department::getCode).toArray(String[]::new));
        secondDeptCombo.insertItemAt("", 0);
        secondDeptCombo.setSelectedIndex(0);
        JComboBox<Integer> secondYearCombo = new JComboBox<>(new Integer[] { 0, 1, 2, 3, 4 });

        // Auto-populate data when user is selected
        studentUserCombo.addActionListener(e -> {
            String selectedItem = (String) studentUserCombo.getSelectedItem();
            if (selectedItem == null) return;
            
            // Extract actual username from "username (PENDING)" or "username"
            String selectedUser = selectedItem.split(" ")[0];
            
            if (selectedUser != null) {
                // 1. Get name from User account
                User u = dataStore.findUser(selectedUser);
                if (u != null) {
                    nameField.setText(u.getFullName());
                }

                // 2. Get profile data if it already exists
                StudentProfile p = dataStore.findStudentProfileByUsername(selectedUser);
                if (p != null) {
                    idField.setText(p.getStudentId());
                    deptCombo.setSelectedItem(p.getDepartment());
                    yearCombo.setSelectedItem(p.getYear());
                    secondDeptCombo.setSelectedItem(p.getSecondDepartment() == null ? "" : p.getSecondDepartment());
                    secondYearCombo.setSelectedItem(p.getSecondYear());
                } else {
                    // New profile for this user
                    idField.setText("");
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
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

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Primary Year:"), gbc);
        gbc.gridx = 3;
        formPanel.add(yearCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(studentUserCombo, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Second Dept:"), gbc);
        gbc.gridx = 3;
        formPanel.add(secondDeptCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Second Year:"), gbc);
        gbc.gridx = 1;
        formPanel.add(secondYearCombo, gbc);

        JPanel btnGrp = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnGrp.setOpaque(false);
        JButton saveBtn = new JButton("Save Profile");
        saveBtn.setBackground(SAGE_GREEN);
        saveBtn.setForeground(Color.WHITE);
        JButton deleteBtn = new JButton("Delete Profile");
        deleteBtn.setBackground(new Color(0xD9534F));
        deleteBtn.setForeground(Color.WHITE);

        btnGrp.add(saveBtn);
        btnGrp.add(deleteBtn);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(15, 10, 10, 10);
        formPanel.add(btnGrp, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        studentModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Major", "Major Yr", "2nd Major", "2nd Major Yr", "Username" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        studentTable = createModernTable(studentModel);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // Search panel
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setOpaque(false);
        bottom.add(new JLabel("Search Students:"));
        JTextField searchField = new JTextField(20);
        bottom.add(searchField);
        panel.add(bottom, BorderLayout.SOUTH);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(studentModel);
        studentTable.setRowSorter(sorter);
        searchField.addCaretListener(e -> {
            String txt = searchField.getText();
            if (txt.length() == 0)
                sorter.setRowFilter(null);
            else
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txt));
        });

        // Table selection listener
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = studentTable.getSelectedRow();
            if (row >= 0) {
                row = studentTable.convertRowIndexToModel(row);
                String id = (String) studentModel.getValueAt(row, 0);
                idField.setText("(PENDING)".equals(id) ? "" : id);
                // 1. Sync the Username dropdown first
                String username = (String) studentModel.getValueAt(row, 6);
                boolean found = false;
                for (int i = 0; i < studentUserCombo.getItemCount(); i++) {
                    String item = studentUserCombo.getItemAt(i);
                    if (item != null && item.split(" ")[0].equals(username)) {
                        studentUserCombo.setSelectedIndex(i);
                        found = true;
                        break;
                    }
                }
                
                // 2. If user not found in combo (data inconsistency), reset form to prevent wrong saves
                if (!found) {
                    studentUserCombo.setSelectedIndex(-1);
                    nameField.setText("");
                }

                // 3. Sync other fields (Name is auto-synced via studentUserCombo listener)
                deptCombo.setSelectedItem(studentModel.getValueAt(row, 2));
                yearCombo.setSelectedItem(studentModel.getValueAt(row, 3));
                
                Object sDept = studentModel.getValueAt(row, 4);
                secondDeptCombo.setSelectedItem("-".equals(sDept) ? "" : sDept);
                
                Object sYr = studentModel.getValueAt(row, 5);
                secondYearCombo.setSelectedItem(sYr);
            }
        });
        
        // Custom Renderer for Ghost Rows
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

        refreshStudentTable(studentModel, studentTable);

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

            // Smart Upsert: Update if exists, Create if new
            boolean exists = dataStore.findStudentProfileByUsername(actualUsername) != null || dataStore.findStudentProfileById(id) != null;
            if (exists) {
                int res = dataStore.updateStudentProfile(p);
                if (res == 1) {
                    refreshStudentTable(studentModel, studentTable);
                    updateStudentUserCombo(studentPendingOnly); // Refresh labels
                    JOptionPane.showMessageDialog(this, "Profile updated successfully.");
                } else if (res == 0) {
                    JOptionPane.showMessageDialog(this, "No changes detected. Nothing to update.", "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update. Check seniority (Primary Year >= Second Year).", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (dataStore.addStudentProfile(p)) {
                    refreshStudentTable(studentModel, studentTable);
                    updateStudentUserCombo(studentPendingOnly); // Refresh labels
                    JOptionPane.showMessageDialog(this, "Profile created successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create profile. Check for unique ID/Username.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            String selectedItem = (String) studentUserCombo.getSelectedItem();
            if (selectedItem == null) return;
            String actualUsername = selectedItem.split(" ")[0];
            
            if (JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this profile?") == JOptionPane.YES_OPTION) {
                if (dataStore.removeStudentProfile(actualUsername)) {
                    refreshStudentTable(studentModel, studentTable);
                    updateStudentUserCombo(studentPendingOnly); // Restore (PENDING) label
                    idField.setText("");
                    nameField.setText("");
                    studentUserCombo.setSelectedIndex(-1);
                }
            }
        });

        return panel;
    }

    private JPanel createGradeEntryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        top.setOpaque(false);
        top.add(new JLabel("Select Course:"));

        List<Course> courses = dataStore.getCoursesByInstructor(currentUser.getUsername());
        JComboBox<String> courseCombo = new JComboBox<>(
                courses.stream().map(c -> c.getCourseCode() + " - " + c.getCourseName()).toArray(String[]::new));
        top.add(courseCombo);

        JButton loadBtn = new JButton("Load Students");
        loadBtn.setBackground(SKY_BLUE);
        loadBtn.setForeground(Color.WHITE);
        top.add(loadBtn);
        panel.add(top, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[] { "Student Username", "Student Name", "Midterm", "Final", "Average", "Letter Grade" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return c == 2 || c == 3;
            }
        };
        JTable table = createModernTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton saveBtn = new JButton("Save All Grades");
        saveBtn.setBackground(SAGE_GREEN);
        saveBtn.setForeground(Color.WHITE);
        JPanel btm = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btm.setOpaque(false);
        btm.add(saveBtn);
        panel.add(btm, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> {
            model.setRowCount(0);
            String sel = (String) courseCombo.getSelectedItem();
            if (sel == null)
                return;
            String code = sel.split(" - ")[0];
            dataStore.getEnrollmentsByCourse(code).forEach(enr -> {
                StudentProfile s = dataStore.findStudentProfileByUsername(enr.getStudentUsername());
                GradeRecord g = dataStore.findGrade(enr.getStudentUsername(), code);
                model.addRow(new Object[] { enr.getStudentUsername(), s != null ? s.getFullName() : "-",
                        g != null ? g.getMidterm() : 0.0, g != null ? g.getFinalExam() : 0.0,
                        g != null ? String.format("%.2f", g.calculateAverage()) : "0.00",
                        g != null ? g.getLetterGrade() : "FF" });
            });
        });

        saveBtn.addActionListener(e -> {
            String sel = (String) courseCombo.getSelectedItem();
            if (sel == null)
                return;
            String code = sel.split(" - ")[0];
            for (int i = 0; i < model.getRowCount(); i++) {
                String user = (String) model.getValueAt(i, 0);
                try {
                    double m = Double.parseDouble(model.getValueAt(i, 2).toString());
                    double f = Double.parseDouble(model.getValueAt(i, 3).toString());
                    dataStore.upsertGrade(user, code, m, f);
                } catch (NumberFormatException nfe) {
                }
            }
            JOptionPane.showMessageDialog(this, "Grades saved successfully.");
        });

        return panel;
    }

    private JPanel createDistributionPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        top.setOpaque(false);
        top.add(new JLabel("Select Course for Chart:"));

        List<Course> myCourses = dataStore.getCoursesByInstructor(currentUser.getUsername());
        JComboBox<String> courseCombo = new JComboBox<>(
                myCourses.stream().map(c -> c.getCourseCode()).toArray(String[]::new));
        top.add(courseCombo);
        panel.add(top, BorderLayout.NORTH);

        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setOpaque(false);
        panel.add(chartContainer, BorderLayout.CENTER);

        courseCombo.addActionListener(e -> {
            String code = (String) courseCombo.getSelectedItem();
            if (code != null) {
                chartContainer.removeAll();
                chartContainer.add(GradeDistributionChart.buildPanel(code), BorderLayout.CENTER);
                chartContainer.revalidate();
                chartContainer.repaint();
            }
        });

        // Load initial chart
        if (courseCombo.getItemCount() > 0) {
            chartContainer.add(GradeDistributionChart.buildPanel((String) courseCombo.getSelectedItem()),
                    BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createTranscriptPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        gpaLabel = new JLabel("GPA: 0.00");
        gpaLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gpaLabel.setForeground(new Color(0x333399));
        top.add(gpaLabel, BorderLayout.WEST);

        panel.add(top, BorderLayout.NORTH);

        transcriptTableModel = new DefaultTableModel(
                new String[] { "Course Code", "Course Name", "Credit", "Midterm", "Final", "Average", "Letter Grade" },
                0);
        JTable table = createModernTable(transcriptTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton exportPdfBtn = new JButton("Export PDF Transcript");
        exportPdfBtn.setBackground(SKY_BLUE);
        exportPdfBtn.setForeground(Color.WHITE);
        exportPdfBtn.addActionListener(e -> {
            String path = "transcript_" + currentUser.getUsername() + ".pdf";
            String res = TranscriptExporter.export(currentUser.getUsername(), path);
            JOptionPane.showMessageDialog(this, res);
        });

        JButton exportTxtBtn = new JButton("Export TXT Transcript");
        exportTxtBtn.setBackground(SAGE_GREEN);
        exportTxtBtn.setForeground(Color.WHITE);
        exportTxtBtn.addActionListener(e -> {
            String path = "transcript_" + currentUser.getUsername() + ".txt";
            String res = TranscriptExporter.export(currentUser.getUsername(), path);
            JOptionPane.showMessageDialog(this, res);
        });

        JPanel btm = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btm.setOpaque(false);
        btm.add(exportTxtBtn);
        btm.add(exportPdfBtn);
        panel.add(btm, BorderLayout.SOUTH);

        refreshTranscript();
        return panel;
    }

    private void refreshTranscript() {
        transcriptTableModel.setRowCount(0);
        dataStore.getGradesByStudent(currentUser.getUsername()).forEach(g -> {
            Course c = dataStore.findCourse(g.getCourseCode());
            transcriptTableModel.addRow(new Object[] { g.getCourseCode(), c != null ? c.getCourseName() : "-",
                    c != null ? c.getCredit() : "-", g.getMidterm(), g.getFinalExam(),
                    String.format("%.2f", g.calculateAverage()), g.getLetterGrade() });
        });
        gpaLabel.setText(String.format("GPA: %.2f", dataStore.calculateGPA(currentUser.getUsername())));
    }

    private JPanel createStructurePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Form Section
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WARM_BEIGE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Manage Structural Entities"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JComboBox<String> typeCombo = new JComboBox<>(new String[] { "Faculty", "Department" });
        JTextField codeField = new JTextField(10);
        JTextField nameField = new JTextField(20);
        JLabel parentLabel = new JLabel("Parent Faculty:");
        JComboBox<String> fParentCombo = new JComboBox<>(
                dataStore.getFaculties().stream().map(Faculty::getCode).toArray(String[]::new));

        // Hide parent info by default (Faculty selected)
        parentLabel.setVisible(false);
        fParentCombo.setVisible(false);

        typeCombo.addActionListener(e -> {
            boolean isDept = "Department".equals(typeCombo.getSelectedItem());
            parentLabel.setVisible(isDept);
            fParentCombo.setVisible(isDept);
            // Reset fields when type changes
            codeField.setText("");
            nameField.setText("");
            formPanel.revalidate();
            formPanel.repaint();
        });

        gbc.insets = new Insets(5, 5, 5, 10);
        gbc.anchor = GridBagConstraints.EAST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(typeCombo, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Code:"), gbc);
        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(codeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(parentLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(fParentCombo, gbc);

        JButton saveBtn = new JButton("Save Structure");
        saveBtn.setBackground(SAGE_GREEN);
        saveBtn.setForeground(Color.WHITE);
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(saveBtn, gbc);

        JButton delBtn = new JButton("Delete Selected");
        delBtn.setBackground(TERRACOTTA);
        delBtn.setForeground(Color.WHITE);
        gbc.gridx = 3;
        gbc.gridy = 3;
        formPanel.add(delBtn, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        // Tables Section
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        tablesPanel.setOpaque(false);

        // Faculty Table
        JPanel fPanel = new JPanel(new BorderLayout(0, 10));
        fPanel.setOpaque(false);
        fPanel.setBorder(BorderFactory.createTitledBorder("Faculties"));
        DefaultTableModel fModel = new DefaultTableModel(new String[] { "Code", "Name" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable fTable = createModernTable(fModel);
        fPanel.add(new JScrollPane(fTable), BorderLayout.CENTER);
        tablesPanel.add(fPanel);

        // Department Table
        JPanel dPanel = new JPanel(new BorderLayout(0, 10));
        dPanel.setOpaque(false);
        dPanel.setBorder(BorderFactory.createTitledBorder("Departments"));
        DefaultTableModel dModel = new DefaultTableModel(new String[] { "Code", "Name", "Faculty" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable dTable = createModernTable(dModel);
        dPanel.add(new JScrollPane(dTable), BorderLayout.CENTER);
        tablesPanel.add(dPanel);

        panel.add(tablesPanel, BorderLayout.CENTER);

        // Selection Listeners
        fTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;
            int row = fTable.getSelectedRow();
            if (row >= 0) {
                row = fTable.convertRowIndexToModel(row);
                typeCombo.setSelectedItem("Faculty");
                codeField.setText((String) fModel.getValueAt(row, 0));
                nameField.setText((String) fModel.getValueAt(row, 1));
            }
        });

        dTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;
            int row = dTable.getSelectedRow();
            if (row >= 0) {
                row = dTable.convertRowIndexToModel(row);
                typeCombo.setSelectedItem("Department");
                codeField.setText((String) dModel.getValueAt(row, 0));
                nameField.setText((String) dModel.getValueAt(row, 1));
                fParentCombo.setSelectedItem((String) dModel.getValueAt(row, 2));
            }
        });

        saveBtn.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            String code = codeField.getText().toUpperCase();
            String name = nameField.getText();

            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Code and Name are required.");
                return;
            }

            if ("Faculty".equals(type)) {
                Faculty f = new Faculty(code, name);
                boolean exists = dataStore.findFaculty(code) != null;
                if (exists) {
                    int res = dataStore.updateFaculty(f);
                    if (res == 1) {
                        refreshStructure(fModel, fTable, dModel, dTable);
                        JOptionPane.showMessageDialog(panel, "Faculty updated successfully.");
                    } else if (res == 0) {
                        JOptionPane.showMessageDialog(panel, "No changes detected.", "Info",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(panel, "Error updating faculty.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    if (dataStore.addFaculty(f)) {
                        refreshStructure(fModel, fTable, dModel, dTable);
                        JOptionPane.showMessageDialog(panel, "Faculty added successfully.");
                    } else {
                        JOptionPane.showMessageDialog(panel, "Failed to add faculty.");
                    }
                }
            } else {
                String parent = (String) fParentCombo.getSelectedItem();
                if (parent == null) {
                    JOptionPane.showMessageDialog(panel, "Select a parent faculty.");
                    return;
                }
                Department d = new Department(code, name, parent);
                boolean exists = dataStore.findDepartment(code) != null;
                if (exists) {
                    int res = dataStore.updateDepartment(d);
                    if (res == 1) {
                        refreshStructure(fModel, fTable, dModel, dTable);
                        JOptionPane.showMessageDialog(panel, "Department updated successfully.");
                    } else if (res == 0) {
                        JOptionPane.showMessageDialog(panel, "No changes detected.", "Info",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(panel, "Error updating department.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    if (dataStore.addDepartment(d)) {
                        refreshStructure(fModel, fTable, dModel, dTable);
                        JOptionPane.showMessageDialog(panel, "Department added successfully.");
                    } else {
                        JOptionPane.showMessageDialog(panel, "Failed to add department.");
                    }
                }
            }
            // Update other combos in the app
            fParentCombo.setModel(new DefaultComboBoxModel<>(
                    dataStore.getFaculties().stream().map(Faculty::getCode).toArray(String[]::new)));
        });

        delBtn.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            String code = codeField.getText().toUpperCase();
            if (code.isEmpty())
                return;

            int confirm = JOptionPane.showConfirmDialog(panel, "Are you sure you want to delete this " + type + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = false;
                if ("Faculty".equals(type)) {
                    success = dataStore.deleteFaculty(code);
                } else {
                    success = dataStore.deleteDepartment(code);
                }

                if (success) {
                    refreshStructure(fModel, fTable, dModel, dTable);
                    codeField.setText("");
                    nameField.setText("");
                    JOptionPane.showMessageDialog(panel, type + " deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(panel, "Could not find " + type + " to delete.");
                }
            }
        });

        refreshStructure(fModel, fTable, dModel, dTable);
        return panel;
    }

    private void refreshStructure(DefaultTableModel fModel, JTable fTable, DefaultTableModel dModel, JTable dTable) {
        String selF = null;
        if (fTable != null && fTable.getSelectedRow() >= 0) {
            selF = (String) fModel.getValueAt(fTable.convertRowIndexToModel(fTable.getSelectedRow()), 0);
        }
        String selD = null;
        if (dTable != null && dTable.getSelectedRow() >= 0) {
            selD = (String) dModel.getValueAt(dTable.convertRowIndexToModel(dTable.getSelectedRow()), 0);
        }

        if (fModel != null) {
            fModel.setRowCount(0);
            dataStore.getFaculties().forEach(f -> fModel.addRow(new Object[] { f.getCode(), f.getName() }));
            if (selF != null) {
                for (int i = 0; i < fModel.getRowCount(); i++) {
                    if (selF.equals(fModel.getValueAt(i, 0))) {
                        fTable.setRowSelectionInterval(fTable.convertRowIndexToView(i),
                                fTable.convertRowIndexToView(i));
                        break;
                    }
                }
            }
        }
        if (dModel != null) {
            dModel.setRowCount(0);
            dataStore.getDepartments()
                    .forEach(d -> dModel.addRow(new Object[] { d.getCode(), d.getName(), d.getFacultyCode() }));
            if (selD != null) {
                for (int i = 0; i < dModel.getRowCount(); i++) {
                    if (selD.equals(dModel.getValueAt(i, 0))) {
                        dTable.setRowSelectionInterval(dTable.convertRowIndexToView(i),
                                dTable.convertRowIndexToView(i));
                        break;
                    }
                }
            }
        }
    }

    private JPanel createCurriculumPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        DefaultTableModel model = new DefaultTableModel(new String[] { "Dept", "Year", "Course Code" }, 0);
        JTable table = createModernTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        form.setOpaque(false);
        JComboBox<String> deptCombo = new JComboBox<>(
                dataStore.getDepartments().stream().map(Department::getCode).toArray(String[]::new));
        JComboBox<Integer> yearCombo = new JComboBox<>(new Integer[] { 1, 2, 3, 4 });
        JComboBox<String> courseCombo = new JComboBox<>(
                dataStore.getCourses().stream().map(Course::getCourseCode).toArray(String[]::new));

        JButton addBtn = new JButton("Assign to Curriculum");
        addBtn.setBackground(SAGE_GREEN);
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> {
            String d = (String) deptCombo.getSelectedItem();
            int y = (Integer) yearCombo.getSelectedItem();
            String c = (String) courseCombo.getSelectedItem();
            if (d != null && c != null) {
                if (dataStore.addCurriculumMapping(new CurriculumMapping(d, y, c))) {
                    refreshCurriculum(model, table);
                } else {
                    JOptionPane.showMessageDialog(this, "This course is already assigned to this department and year.",
                            "Duplicate", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        form.add(new JLabel("Dept:"));
        form.add(deptCombo);
        form.add(new JLabel("Year:"));
        form.add(yearCombo);
        form.add(new JLabel("Course:"));
        form.add(courseCombo);
        form.add(addBtn);

        panel.add(form, BorderLayout.NORTH);
        refreshCurriculum(model, table);
        return panel;
    }

    private void refreshCurriculum(DefaultTableModel model, JTable table) {
        String selectedKey = null;
        if (table != null && table.getSelectedRow() >= 0) {
            int row = table.convertRowIndexToModel(table.getSelectedRow());
            selectedKey = (String) model.getValueAt(row, 0) + "|" + model.getValueAt(row, 1) + "|"
                    + model.getValueAt(row, 2);
        }

        model.setRowCount(0);
        dataStore.getAllCurriculumMappings()
                .forEach(m -> model.addRow(new Object[] { m.getDepartmentCode(), m.getYear(), m.getCourseCode() }));

        if (selectedKey != null) {
            for (int i = 0; i < model.getRowCount(); i++) {
                String key = (String) model.getValueAt(i, 0) + "|" + model.getValueAt(i, 1) + "|"
                        + model.getValueAt(i, 2);
                if (selectedKey.equals(key)) {
                    int viewIdx = table.convertRowIndexToView(i);
                    if (viewIdx >= 0) {
                        table.setRowSelectionInterval(viewIdx, viewIdx);
                    }
                    break;
                }
            }
        }
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Top Metrics Row
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        metricsPanel.setOpaque(false);

        metricsPanel.add(createMetricCard("Total Users", String.valueOf(dataStore.getUsers().size()), COCOA_BROWN));
        metricsPanel
                .add(createMetricCard("Total Students", String.valueOf(dataStore.getStudents().size()), SAGE_GREEN));
        metricsPanel.add(createMetricCard("Active Courses", String.valueOf(dataStore.getCourses().size()), SKY_BLUE));

        double avgGpa = dataStore.getStudents().stream()
                .mapToDouble(s -> dataStore.calculateGPA(s.getUsername()))
                .average().orElse(0.0);
        metricsPanel.add(createMetricCard("Average GPA", String.format("%.2f", avgGpa), TERRACOTTA));

        panel.add(metricsPanel, BorderLayout.NORTH);

        // Center Content - Split View
        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        centerPanel.setOpaque(false);

        // 1. Department Stats
        JPanel deptStats = new JPanel(new BorderLayout(0, 10));
        deptStats.setOpaque(false);
        deptStats.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(CREAM_SAND),
                "Enrollment by Department"));

        DefaultTableModel deptModel = new DefaultTableModel(new String[] { "Department", "Students" }, 0);
        dataStore.getDepartments().forEach(d -> {
            long count = dataStore.getStudents().stream().filter(s -> s.getDepartment().equals(d.getName())).count();
            deptModel.addRow(new Object[] { d.getName(), count });
        });
        deptStats.add(new JScrollPane(createModernTable(deptModel)), BorderLayout.CENTER);

        // 2. Grade Distribution (System-wide)
        JPanel gradeStats = new JPanel(new BorderLayout(0, 10));
        gradeStats.setOpaque(false);
        gradeStats.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(CREAM_SAND), "Grade Distribution"));

        DefaultTableModel gradeModel = new DefaultTableModel(new String[] { "Grade", "Count" }, 0);
        String[] grades = { "AA", "BA", "BB", "CB", "CC", "DC", "DD", "FD", "FF" };
        java.util.Map<String, Integer> dist = new java.util.HashMap<>();
        for (String g : grades)
            dist.put(g, 0);
        dataStore.getAllGrades().forEach(g -> {
            String l = g.getLetterGrade();
            dist.put(l, dist.getOrDefault(l, 0) + 1);
        });
        for (String g : grades)
            gradeModel.addRow(new Object[] { g, dist.get(g) });
        gradeStats.add(new JScrollPane(createModernTable(gradeModel)), BorderLayout.CENTER);

        // 3. Recent Enrollments
        JPanel activityPanel = new JPanel(new BorderLayout(0, 10));
        activityPanel.setOpaque(false);
        activityPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(CREAM_SAND), "Recent Enrollments"));

        DefaultTableModel enrollModel = new DefaultTableModel(new String[] { "Student", "Course" }, 0);
        List<Enrollment> allEnroll = dataStore.getAllEnrollments();
        java.util.Collections.reverse(allEnroll);
        allEnroll.stream().limit(50).forEach(e -> {
            User u = dataStore.findUser(e.getStudentUsername());
            enrollModel.addRow(new Object[] {
                    u != null ? u.getFullName() : e.getStudentUsername(),
                    e.getCourseCode()
            });
        });
        activityPanel.add(new JScrollPane(createModernTable(enrollModel)), BorderLayout.CENTER);

        centerPanel.add(deptStats);
        centerPanel.add(gradeStats);
        centerPanel.add(activityPanel);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMetricCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(CREAM_SAND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDCDCDC), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tLbl.setForeground(COCOA_BROWN);

        JLabel vLbl = new JLabel(value);
        vLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        vLbl.setForeground(color);

        card.add(tLbl, BorderLayout.NORTH);
        card.add(vLbl, BorderLayout.CENTER);

        return card;
    }

    private JPanel createInstructorCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        DefaultTableModel model = new DefaultTableModel(new String[] { "Code", "Name", "Enrollments" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        panel.add(new JScrollPane(createModernTable(model)), BorderLayout.CENTER);
        List<Course> myCourses = dataStore.getCoursesByInstructor(currentUser.getUsername());
        myCourses.forEach(c -> model.addRow(new Object[] { c.getCourseCode(), c.getCourseName(),
                dataStore.countEnrollmentForCourse(c.getCourseCode()) }));
        return panel;
    }

    private JPanel createAvailableCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        DefaultTableModel model = new DefaultTableModel(
                new String[] { "Code", "Name", "Instructor", "Quota", "Status" }, 0);
        JTable table = createModernTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton enrollBtn = new JButton("Enroll in Selected Course");
        enrollBtn.setBackground(SAGE_GREEN);
        enrollBtn.setForeground(Color.WHITE);
        enrollBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String code = (String) model.getValueAt(row, 0);
                StudentProfile p = dataStore.findStudentProfileByUsername(currentUser.getUsername());
                Course c = dataStore.findCourse(code);
                String prefix = code.replaceAll("\\d.*", "");
                String primaryCode = dataStore.getDepartmentCodeByName(p.getDepartment());
                String secondCode = dataStore.getDepartmentCodeByName(p.getSecondDepartment());

                boolean majorMatch = (primaryCode != null && prefix.equalsIgnoreCase(primaryCode)) ||
                        (secondCode != null && prefix.equalsIgnoreCase(secondCode));

                if (!majorMatch) {
                    JOptionPane.showMessageDialog(this,
                            "Enrollment Failed: This course is not in your Curriculum (Major Restriction).", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (dataStore.countEnrollmentForCourse(code) >= c.getQuota()) {
                    JOptionPane.showMessageDialog(this, "Enrollment Failed: Course quota is full.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (dataStore.isStudentEnrolled(currentUser.getUsername(), code)) {
                    JOptionPane.showMessageDialog(this, "Enrollment Failed: You are already enrolled in this course.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else if (dataStore.enrollStudent(currentUser.getUsername(), code)) {
                    JOptionPane.showMessageDialog(this, "Successfully enrolled in " + code);
                    refreshAvailableCourses(model);
                } else {
                    JOptionPane.showMessageDialog(this, "Enrollment failed due to an unknown error.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(enrollBtn, BorderLayout.SOUTH);
        refreshAvailableCourses(model);
        return panel;
    }

    private void refreshAvailableCourses(DefaultTableModel model) {
        model.setRowCount(0);
        dataStore.getCourses().forEach(c -> {
            int count = dataStore.countEnrollmentForCourse(c.getCourseCode());
            boolean enrolled = dataStore.isStudentEnrolled(currentUser.getUsername(), c.getCourseCode());
            model.addRow(new Object[] { c.getCourseCode(), c.getCourseName(), c.getInstructorUsername(),
                    count + "/" + c.getQuota(), enrolled ? "Enrolled" : "Available" });
        });
    }

    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        myCoursesTableModel = new DefaultTableModel(new String[] { "Code", "Course Name", "Instructor" }, 0);
        panel.add(new JScrollPane(createModernTable(myCoursesTableModel)), BorderLayout.CENTER);
        refreshMyCoursesTable();
        return panel;
    }

    private void refreshMyCoursesTable() {
        myCoursesTableModel.setRowCount(0);
        dataStore.getEnrollmentsByStudent(currentUser.getUsername()).forEach(e -> {
            Course c = dataStore.findCourse(e.getCourseCode());
            if (c != null)
                myCoursesTableModel
                        .addRow(new Object[] { c.getCourseCode(), c.getCourseName(), c.getInstructorUsername() });
        });
    }

    private JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        DefaultTableModel model = new DefaultTableModel(
                new String[] { "Code", "Name", "Instructor", "Quota", "Credits" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = createModernTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton addBtn = new JButton("Add Course");
        addBtn.setBackground(SAGE_GREEN);
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> showAddCourseDialog(model, table));

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setBackground(TERRACOTTA);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String code = (String) model.getValueAt(table.convertRowIndexToModel(row), 0);
                if (dataStore.removeCourse(code)) {
                    refreshCourseTable(model, table);
                }
            }
        });

        actions.add(addBtn);
        actions.add(deleteBtn);
        panel.add(actions, BorderLayout.SOUTH);

        refreshCourseTable(model, table);
        return panel;
    }

    private void showAddCourseDialog(DefaultTableModel model, JTable table) {
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField cField = new JTextField();
        JTextField nField = new JTextField();
        JComboBox<String> iCombo = new JComboBox<>(dataStore.getUsers().stream()
                .filter(u -> "Instructor".equalsIgnoreCase(u.getRole()))
                .map(User::getUsername).toArray(String[]::new));
        JTextField qField = new JTextField("30");
        JTextField crField = new JTextField("3");

        p.add(new JLabel("Course Code:"));
        p.add(cField);
        p.add(new JLabel("Course Name:"));
        p.add(nField);
        p.add(new JLabel("Instructor:"));
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
                String inst = (String) iCombo.getSelectedItem();
                int q = Integer.parseInt(qField.getText());
                int cr = Integer.parseInt(crField.getText());

                if (dataStore.addCourse(new Course(code, name, cr, q, inst))) {
                    refreshCourseTable(model, table);
                } else {
                    JOptionPane.showMessageDialog(this, "Course code already exists.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid numeric input.");
            }
        }
    }

    private void refreshCourseTable(DefaultTableModel model, JTable table) {
        String selectedCode = null;
        if (table != null && table.getSelectedRow() >= 0) {
            selectedCode = (String) model.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), 0);
        }

        model.setRowCount(0);
        dataStore.getCourses().forEach(c -> model.addRow(new Object[] { c.getCourseCode(), c.getCourseName(),
                c.getInstructorUsername(), c.getQuota(), c.getCredit() }));

        if (selectedCode != null) {
            for (int i = 0; i < model.getRowCount(); i++) {
                if (selectedCode.equals(model.getValueAt(i, 0))) {
                    int viewIdx = table.convertRowIndexToView(i);
                    if (viewIdx >= 0) {
                        table.setRowSelectionInterval(viewIdx, viewIdx);
                    }
                    break;
                }
            }
        }
    }

    private void refreshUserTable(DefaultTableModel model, JTable table) {
        String selectedUsername = null;
        if (table != null && table.getSelectedRow() >= 0) {
            selectedUsername = (String) model.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), 0);
        }

        model.setRowCount(0);
        dataStore.getUsers().forEach(
                u -> model.addRow(new Object[] { u.getUsername(), u.getRole(), u.getFullName(), u.getReferenceId() }));

        if (selectedUsername != null) {
            for (int i = 0; i < model.getRowCount(); i++) {
                if (selectedUsername.equals(model.getValueAt(i, 0))) {
                    int viewIdx = table.convertRowIndexToView(i);
                    if (viewIdx >= 0) {
                        table.setRowSelectionInterval(viewIdx, viewIdx);
                    }
                    break;
                }
            }
        }
    }

    private void refreshStudentTable(DefaultTableModel model, JTable table) {
        String selectedId = null;
        if (table != null && table.getSelectedRow() >= 0) {
            selectedId = (String) model.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), 0);
        }

        model.setRowCount(0);
        
        // 1. Add existing Student Profiles
        dataStore.getStudents().forEach(s -> model.addRow(new Object[] {
                s.getStudentId(), s.getFullName(), s.getDepartment(), s.getYear(),
                s.getSecondDepartment() != null ? s.getSecondDepartment() : "-",
                s.getSecondYear(), s.getUsername()
        }));
        
        // 2. Add "Ghost Rows" for users without profiles
        dataStore.getUsers().stream()
            .filter(u -> "Student".equalsIgnoreCase(u.getRole()))
            .filter(u -> dataStore.findStudentProfileByUsername(u.getUsername()) == null)
            .forEach(u -> model.addRow(new Object[] {
                "(PENDING)", u.getFullName(), "-", "-", "-", 0, u.getUsername()
            }));

        if (selectedId != null) {
            for (int i = 0; i < model.getRowCount(); i++) {
                if (selectedId.equals(model.getValueAt(i, 0))) {
                    int viewIdx = table.convertRowIndexToView(i);
                    if (viewIdx >= 0) {
                        table.setRowSelectionInterval(viewIdx, viewIdx);
                    }
                    break;
                }
            }
        }
    }


    private JTable createModernTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setBackground(new Color(0xE0E0E0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Horizontal padding
                return this;
            }
        };
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        table.setDefaultRenderer(Object.class, leftRenderer);
        return table;
    }

    // Removed redundant showAddUserDialog as it's now integrated in
    // createUsersPanel

    private void updateStudentUserCombo(boolean pendingOnly) {
        if (studentUserCombo != null) {
            String currentSel = (String) studentUserCombo.getSelectedItem();
            studentUserCombo.removeAllItems();
            
            dataStore.getUsers().stream()
                .filter(u -> "Student".equalsIgnoreCase(u.getRole()))
                .forEach(u -> {
                    boolean hasProfile = dataStore.findStudentProfileByUsername(u.getUsername()) != null;
                    if (!pendingOnly || !hasProfile) {
                        String label = u.getUsername() + (hasProfile ? "" : " (PENDING)");
                        studentUserCombo.addItem(label);
                    }
                });
            
            if (currentSel != null) studentUserCombo.setSelectedItem(currentSel);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UniversityAutomationApp().setVisible(true);
        });
    }
}
