package ui;


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

    // Admin Module Fields
    private DefaultTableModel usersTableModel;
    private JTable usersTable;
    private DefaultTableModel facultiesTableModel;
    private JTable facultiesTable;
    private DefaultTableModel departmentsTableModel;
    private JTable departmentsTable;
    private DefaultTableModel curriculumTableModel;
    private JTable curriculumTable;
    private JComboBox<String> currDeptCombo;
    private JComboBox<Integer> currYearCombo;
    private DefaultTableModel adminCoursesTableModel;
    private JTable adminCoursesTable;
    private DefaultTableModel enrollmentsTableModel;
    private JTable enrollmentsTable;
    private DefaultTableModel requestsTableModel;
    private JTable requestsTable;
    private JPanel reportsPanel;

    // Instructor Module Fields
    private DefaultTableModel instructorCoursesTableModel;
    private DefaultTableModel gradeEntryTableModel;
    private JComboBox<String> gradeEntryCourseCombo;
    private JComboBox<String> gradeDistCourseCombo;
    private JPanel gradeDistChartContainer;

    // Student Module Fields
    private DefaultTableModel availableCoursesTableModel;

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

        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            if (index != -1) {
                String tabTitle = tabbedPane.getTitleAt(index);
                refreshTabContent(tabTitle);
            }
        });

        mainPanel.add(dashboard, DASHBOARD_CARD);
        cardLayout.show(mainPanel, DASHBOARD_CARD);
    }

    private void refreshTabContent(String title) {
        if (title == null) return;
        
        switch (title) {
            case "User Management":
                refreshUserTable(usersTableModel, usersTable);
                break;
            case "Structural Management":
                refreshStructure(facultiesTableModel, facultiesTable, departmentsTableModel, departmentsTable);
                break;
            case "Curriculum Management":
                if (currDeptCombo != null && currYearCombo != null) {
                    refreshCurriculum(curriculumTableModel, curriculumTable, (String)currDeptCombo.getSelectedItem(), (Integer)currYearCombo.getSelectedItem());
                }
                break;
            case "Student Management":
                refreshStudentTable(studentModel, studentTable);
                break;
            case "Course Management":
                refreshCourseTable(adminCoursesTableModel, adminCoursesTable);
                break;
            case "Enrollment Management":
                refreshEnrollmentTable();
                refreshRequestsTable();
                break;
            case "System Reports":
                refreshReportsPanel();
                break;
            case "My Courses":
                if ("Instructor".equalsIgnoreCase(currentUser.getRole())) {
                    refreshInstructorCoursesTable();
                } else if ("Student".equalsIgnoreCase(currentUser.getRole())) {
                    refreshMyCoursesTable();
                }
                break;
            case "Grade Entry":
                refreshGradeEntryTable();
                break;
            case "Grade Distribution":
                refreshGradeDistribution();
                break;
            case "Available Courses":
                refreshAvailableCourses(availableCoursesTableModel);
                break;
            case "Transcript":
                refreshTranscript();
                break;
        }
    }

    private void setupAdminTabs(JTabbedPane tabs) {
        tabs.addTab("User Management", createUsersPanel());
        tabs.addTab("Structural Management", createStructurePanel());
        tabs.addTab("Curriculum Management", createCurriculumPanel());
        tabs.addTab("Student Management", createStudentManagementPanel());
        tabs.addTab("Course Management", createCoursesPanel());
        tabs.addTab("Enrollment Management", createEnrollmentManagementPanel());
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

    private void setupSearch(JPanel container, DefaultTableModel model, JTable table) {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search: "));
        JTextField searchField = new JTextField(15);
        searchPanel.add(searchField);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        searchField.addCaretListener(e -> {
            String text = searchField.getText();
            if (text.trim().length() == 0) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
        
        container.add(searchPanel, BorderLayout.NORTH);
    }

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
        JComboBox<String> uDeptCombo = new JComboBox<>(dataStore.getDepartments().stream().map(Department::getCode).toArray(String[]::new));
        uDeptCombo.insertItemAt("-", 0);
        uDeptCombo.setSelectedIndex(0);
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
        formPanel.add(new JLabel("Dept:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(uDeptCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(nField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
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
        usersTable = createModernTable(usersTableModel);

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

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.add(formPanel, BorderLayout.NORTH);
        
        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        setupSearch(searchWrap, usersTableModel, usersTable);
        topWrapper.add(searchWrap, BorderLayout.CENTER);
        topWrapper.add(btnGrp, BorderLayout.SOUTH);
        
        panel.add(topWrapper, BorderLayout.NORTH);
        panel.add(new JScrollPane(usersTable), BorderLayout.CENTER);

        usersTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;
            int row = usersTable.getSelectedRow();
            if (row >= 0) {
                row = usersTable.convertRowIndexToModel(row);
                uField.setText((String) usersTableModel.getValueAt(row, 0));
                rCombo.setSelectedItem((String) usersTableModel.getValueAt(row, 1));
                uDeptCombo.setSelectedItem((String) usersTableModel.getValueAt(row, 2));
                nField.setText((String) usersTableModel.getValueAt(row, 3));
                rIdField.setText((String) usersTableModel.getValueAt(row, 4));
                User u = dataStore.findUser((String) usersTableModel.getValueAt(row, 0));
                if (u != null)
                    pField.setText(u.getPassword());
            }
        });

        addBtn.addActionListener(e -> {
            User u = new User(uField.getText(), new String(pField.getPassword()), (String) rCombo.getSelectedItem(),
                    nField.getText(),
                    rIdField.getText(),
                    (String) uDeptCombo.getSelectedItem());
            int result = dataStore.addUser(u);
            if (result == 1) {
                refreshUserTable(usersTableModel, usersTable);
                updateStudentUserCombo(studentPendingOnly);
                refreshStudentTable(studentModel, studentTable);
                JOptionPane.showMessageDialog(panel, "User added successfully.");
            } else if (result == 0) {
                JOptionPane.showMessageDialog(panel, "Username cannot be empty.");
            } else {
                JOptionPane.showMessageDialog(panel, "User already exists.");
            }
        });

        updBtn.addActionListener(e -> {
            User u = new User(uField.getText(), new String(pField.getPassword()), (String) rCombo.getSelectedItem(),
                    nField.getText(),
                    rIdField.getText(),
                    (String) uDeptCombo.getSelectedItem());
            int result = dataStore.updateUser(u);
            if (result == 1) {
                refreshUserTable(usersTableModel, usersTable);
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
                    refreshUserTable(usersTableModel, usersTable);
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

        refreshUserTable(usersTableModel, usersTable);
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

        studentModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Major", "Major Yr", "2nd Major", "2nd Major Yr", "Username" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        studentTable = createModernTable(studentModel);

        JPanel topWrapper = new JPanel(new BorderLayout(0, 5));
        topWrapper.setOpaque(false);
        topWrapper.add(formPanel, BorderLayout.NORTH);
        
        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        setupSearch(searchWrap, studentModel, studentTable);
        topWrapper.add(searchWrap, BorderLayout.CENTER);
        
        panel.add(topWrapper, BorderLayout.NORTH);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // Table selection listener
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

                // 1. Sync the Username dropdown
                boolean found = false;
                for (int i = 0; i < studentUserCombo.getItemCount(); i++) {
                    String item = studentUserCombo.getItemAt(i);
                    if (item != null && item.split(" ")[0].equals(username)) {
                        studentUserCombo.setSelectedIndex(i);
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    studentUserCombo.setSelectedIndex(-1);
                }

                // 2. Sync other fields
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
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a student from the table first.");
                return;
            }
            
            if (JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this profile?\nThis will also delete the associated user account and all academic records.") == JOptionPane.YES_OPTION) {
                if (dataStore.deleteStudent(id)) {
                    refreshStudentTable(studentModel, studentTable);
                    updateStudentUserCombo(studentPendingOnly); 
                    idField.setText("");
                    nameField.setText("");
                    studentUserCombo.setSelectedIndex(-1);
                    JOptionPane.showMessageDialog(this, "Student profile deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting student profile.", "Error", JOptionPane.ERROR_MESSAGE);
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
        gradeEntryCourseCombo = new JComboBox<>(
                courses.stream().map(c -> c.getCourseCode() + " - " + c.getCourseName()).toArray(String[]::new));
        top.add(gradeEntryCourseCombo);

        JButton loadBtn = new JButton("Load Students");
        loadBtn.setBackground(SKY_BLUE);
        loadBtn.setForeground(Color.WHITE);
        top.add(loadBtn);
        panel.add(top, BorderLayout.NORTH);

        gradeEntryTableModel = new DefaultTableModel(
                new String[] { "Student Username", "Student Name", "Midterm", "Final", "Average", "Letter Grade" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = createModernTable(gradeEntryTableModel);
        setupSearch(panel, gradeEntryTableModel, table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton entryBtn = new JButton("Enter Grade");
        entryBtn.setBackground(SKY_BLUE);
        entryBtn.setForeground(Color.WHITE);
        entryBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                row = table.convertRowIndexToModel(row);
                String user = (String) gradeEntryTableModel.getValueAt(row, 0);
                String name = (String) gradeEntryTableModel.getValueAt(row, 1);
                String selCourse = (String) gradeEntryCourseCombo.getSelectedItem();
                if (selCourse != null) {
                    showGradeEntryDialog(user, name, selCourse.split(" - ")[0]);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a student first.");
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    entryBtn.doClick();
                }
            }
        });

        JPanel btm = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btm.setOpaque(false);
        btm.add(entryBtn);
        panel.add(btm, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> refreshGradeEntryTable());

        return panel;
    }

    private void showGradeEntryDialog(String username, String fullName, String courseCode) {
        JDialog dialog = new JDialog(this, "Grade Entry - " + fullName, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(WARM_BEIGE);

        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        GradeRecord existing = dataStore.findGrade(username, courseCode);
        JTextField mField = new JTextField(existing != null ? String.valueOf(existing.getMidterm()) : "0.0", 10);
        JTextField fField = new JTextField(existing != null ? String.valueOf(existing.getFinalExam()) : "0.0", 10);
        JLabel avgLbl = new JLabel("Average: 0.00");
        JLabel ltrLbl = new JLabel("Letter Grade: FF");
        avgLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ltrLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        Runnable updateCalc = () -> {
            try {
                double m = Double.parseDouble(mField.getText());
                double f = Double.parseDouble(fField.getText());
                double avg = (m * 0.4) + (f * 0.6);
                avgLbl.setText(String.format("Average: %.2f", avg));
                ltrLbl.setText("Letter Grade: " + GradeRecord.calculateLetterGradeStatic(avg));
            } catch (Exception ex) {
                avgLbl.setText("Average: Error");
            }
        };

        javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateCalc.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateCalc.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateCalc.run(); }
        };
        mField.getDocument().addDocumentListener(dl);
        fField.getDocument().addDocumentListener(dl);
        updateCalc.run();

        gbc.gridx = 0; gbc.gridy = 0; p.add(new JLabel("Student:"), gbc);
        gbc.gridx = 1; p.add(new JLabel(fullName + " (" + username + ")"), gbc);
        gbc.gridx = 0; gbc.gridy = 1; p.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1; p.add(new JLabel(courseCode), gbc);
        gbc.gridx = 0; gbc.gridy = 2; p.add(new JLabel("Midterm (40%):"), gbc);
        gbc.gridx = 1; p.add(mField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; p.add(new JLabel("Final (60%):"), gbc);
        gbc.gridx = 1; p.add(fField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; p.add(avgLbl, gbc);
        gbc.gridx = 1; p.add(ltrLbl, gbc);

        JButton save = new JButton("Save Grade");
        save.setBackground(SAGE_GREEN);
        save.setForeground(Color.WHITE);
        save.addActionListener(e -> {
            try {
                double m = Double.parseDouble(mField.getText());
                double f = Double.parseDouble(fField.getText());
                if (m < 0 || m > 100 || f < 0 || f > 100) {
                    JOptionPane.showMessageDialog(dialog, "Grades must be between 0 and 100.");
                    return;
                }
                dataStore.upsertGrade(username, courseCode, m, f);
                refreshGradeEntryTable();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numeric values.");
            }
        });

        dialog.add(p, BorderLayout.CENTER);
        dialog.add(save, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void refreshGradeEntryTable() {
        if (gradeEntryTableModel == null || gradeEntryCourseCombo == null) return;
        gradeEntryTableModel.setRowCount(0);
        String sel = (String) gradeEntryCourseCombo.getSelectedItem();
        if (sel == null)
            return;
        String code = sel.split(" - ")[0];
        dataStore.getEnrollmentsByCourse(code).forEach(enr -> {
            StudentProfile s = dataStore.findStudentProfileByUsername(enr.getStudentUsername());
            GradeRecord g = dataStore.findGrade(enr.getStudentUsername(), code);
            gradeEntryTableModel.addRow(new Object[] { enr.getStudentUsername(), s != null ? s.getFullName() : "-",
                    g != null ? g.getMidterm() : 0.0, g != null ? g.getFinalExam() : 0.0,
                    g != null ? String.format("%.2f", g.calculateAverage()) : "0.00",
                    g != null ? g.getLetterGrade() : "FF" });
        });
    }

    private JPanel createDistributionPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        top.setOpaque(false);
        top.add(new JLabel("Select Course for Chart:"));

        List<Course> myCourses = dataStore.getCoursesByInstructor(currentUser.getUsername());
        gradeDistCourseCombo = new JComboBox<>(
                myCourses.stream().map(c -> c.getCourseCode()).toArray(String[]::new));
        top.add(gradeDistCourseCombo);
        panel.add(top, BorderLayout.NORTH);

        gradeDistChartContainer = new JPanel(new BorderLayout());
        gradeDistChartContainer.setOpaque(false);
        panel.add(gradeDistChartContainer, BorderLayout.CENTER);

        gradeDistCourseCombo.addActionListener(e -> refreshGradeDistribution());

        // Load initial chart
        refreshGradeDistribution();

        return panel;
    }

    private void refreshGradeDistribution() {
        if (gradeDistCourseCombo == null || gradeDistChartContainer == null) return;
        String code = (String) gradeDistCourseCombo.getSelectedItem();
        if (code != null) {
            gradeDistChartContainer.removeAll();
            gradeDistChartContainer.add(GradeDistributionChart.buildPanel(code), BorderLayout.CENTER);
            gradeDistChartContainer.revalidate();
            gradeDistChartContainer.repaint();
        }
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
        setupSearch(panel, transcriptTableModel, table);
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
        facultiesTableModel = new DefaultTableModel(new String[] { "Code", "Name" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        facultiesTable = createModernTable(facultiesTableModel);
        setupSearch(fPanel, facultiesTableModel, facultiesTable);
        fPanel.add(new JScrollPane(facultiesTable), BorderLayout.CENTER);
        tablesPanel.add(fPanel);

        // Department Table
        JPanel dPanel = new JPanel(new BorderLayout(0, 10));
        dPanel.setOpaque(false);
        dPanel.setBorder(BorderFactory.createTitledBorder("Departments"));
        departmentsTableModel = new DefaultTableModel(new String[] { "Code", "Name", "Faculty" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        departmentsTable = createModernTable(departmentsTableModel);
        setupSearch(dPanel, departmentsTableModel, departmentsTable);
        dPanel.add(new JScrollPane(departmentsTable), BorderLayout.CENTER);
        tablesPanel.add(dPanel);

        panel.add(tablesPanel, BorderLayout.CENTER);

        // Selection Listeners
        facultiesTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;
            int row = facultiesTable.getSelectedRow();
            if (row >= 0) {
                row = facultiesTable.convertRowIndexToModel(row);
                typeCombo.setSelectedItem("Faculty");
                codeField.setText((String) facultiesTableModel.getValueAt(row, 0));
                nameField.setText((String) facultiesTableModel.getValueAt(row, 1));
            }
        });

        departmentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;
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
                JOptionPane.showMessageDialog(panel, "Code and Name are required.");
                return;
            }

            if ("Faculty".equals(type)) {
                Faculty f = new Faculty(code, name);
                boolean exists = dataStore.findFaculty(code) != null;
                if (exists) {
                    int res = dataStore.updateFaculty(f);
                    if (res == 1) {
                        refreshStructure(facultiesTableModel, facultiesTable, departmentsTableModel, departmentsTable);
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
                        refreshStructure(facultiesTableModel, facultiesTable, departmentsTableModel, departmentsTable);
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
                        refreshStructure(facultiesTableModel, facultiesTable, departmentsTableModel, departmentsTable);
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
                        refreshStructure(facultiesTableModel, facultiesTable, departmentsTableModel, departmentsTable);
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
                    refreshStructure(facultiesTableModel, facultiesTable, departmentsTableModel, departmentsTable);
                    codeField.setText("");
                    nameField.setText("");
                    JOptionPane.showMessageDialog(panel, type + " deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(panel, "Could not find " + type + " to delete.");
                }
            }
        });

        refreshStructure(facultiesTableModel, facultiesTable, departmentsTableModel, departmentsTable);
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

        curriculumTableModel = new DefaultTableModel(new String[] { "Dept", "Year", "Course Code" }, 0);
        curriculumTable = createModernTable(curriculumTableModel);
        setupSearch(panel, curriculumTableModel, curriculumTable);
        panel.add(new JScrollPane(curriculumTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        form.setOpaque(false);
        currDeptCombo = new JComboBox<>(
                dataStore.getDepartments().stream().map(Department::getCode).toArray(String[]::new));
        currYearCombo = new JComboBox<>(new Integer[] { 1, 2, 3, 4 });
        JComboBox<String> courseCombo = new JComboBox<>(
                dataStore.getCourses().stream().map(Course::getCourseCode).toArray(String[]::new));

        currDeptCombo.addActionListener(e -> refreshCurriculum(curriculumTableModel, curriculumTable, (String)currDeptCombo.getSelectedItem(), (Integer)currYearCombo.getSelectedItem()));
        currYearCombo.addActionListener(e -> refreshCurriculum(curriculumTableModel, curriculumTable, (String)currDeptCombo.getSelectedItem(), (Integer)currYearCombo.getSelectedItem()));

        JButton addBtn = new JButton("Assign to Curriculum");
        addBtn.setBackground(SAGE_GREEN);
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> {
            String d = (String) currDeptCombo.getSelectedItem();
            int y = (Integer) currYearCombo.getSelectedItem();
            String c = (String) courseCombo.getSelectedItem();
            if (d != null && c != null) {
                if (dataStore.addCurriculumMapping(new CurriculumMapping(d, y, c))) {
                    refreshCurriculum(curriculumTableModel, curriculumTable, d, y);
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
        setupSearch(searchWrap, curriculumTableModel, curriculumTable);
        topWrapper.add(searchWrap, BorderLayout.CENTER);
        
        panel.add(topWrapper, BorderLayout.NORTH);

        refreshCurriculum(curriculumTableModel, curriculumTable, (String)currDeptCombo.getSelectedItem(), (Integer)currYearCombo.getSelectedItem());
        return panel;
    }

    private void refreshCurriculum(DefaultTableModel model, JTable table, String dept, Integer year) {
        model.setRowCount(0);
        List<CurriculumMapping> mappings = dataStore.getAllCurriculumMappings();
        for (CurriculumMapping m : mappings) {
            if (dept != null && !m.getDepartmentCode().equals(dept)) continue;
            if (year != null && m.getYear() != year) continue;
            model.addRow(new Object[] { m.getDepartmentCode(), m.getYear(), m.getCourseCode() });
        }
    }



    private JPanel createReportsPanel() {
        reportsPanel = new JPanel(new BorderLayout());
        reportsPanel.setBackground(WARM_BEIGE);
        refreshReportsPanel();
        return reportsPanel;
    }

    private void refreshReportsPanel() {
        if (reportsPanel != null) {
            reportsPanel.removeAll();
            reportsPanel.add(buildReportsContent(), BorderLayout.CENTER);
            reportsPanel.revalidate();
            reportsPanel.repaint();
        }
    }

    private JPanel buildReportsContent() {
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

        DefaultTableModel deptModel = new DefaultTableModel(new String[] { "Department", "Enrollments" }, 0);
        dataStore.getDepartments().forEach(d -> {
            long count = dataStore.getAllEnrollments().stream()
                    .filter(e -> "APPROVED".equals(e.getStatus()))
                    .filter(e -> e.getCourseCode().startsWith(d.getCode()))
                    .count();
            deptModel.addRow(new Object[] { d.getName(), count });
        });
        JTable deptTable = createModernTable(deptModel);
        setupSearch(deptStats, deptModel, deptTable);
        deptStats.add(new JScrollPane(deptTable), BorderLayout.CENTER);

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
        for (String g : grades) {
            gradeModel.addRow(new Object[] { g, dist.get(g) });
        }
        JTable gTable = createModernTable(gradeModel);
        setupSearch(gradeStats, gradeModel, gTable);
        gradeStats.add(new JScrollPane(gTable), BorderLayout.CENTER);

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
        JTable activityTable = createModernTable(enrollModel);
        setupSearch(activityPanel, enrollModel, activityTable);
        activityPanel.add(new JScrollPane(activityTable), BorderLayout.CENTER);

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
        instructorCoursesTableModel = new DefaultTableModel(new String[] { "Code", "Name", "Enrollments" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = createModernTable(instructorCoursesTableModel);
        setupSearch(panel, instructorCoursesTableModel, table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        refreshInstructorCoursesTable();
        return panel;
    }

    private void refreshInstructorCoursesTable() {
        if (instructorCoursesTableModel == null) return;
        instructorCoursesTableModel.setRowCount(0);
        List<Course> myCourses = dataStore.getCoursesByInstructor(currentUser.getUsername());
        myCourses.forEach(c -> instructorCoursesTableModel.addRow(new Object[] { c.getCourseCode(), c.getCourseName(),
                dataStore.countEnrollmentForCourse(c.getCourseCode()) }));
    }

    private JPanel createAvailableCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topInfo.setOpaque(false);
        int current = dataStore.getStudentTotalCredits(currentUser.getUsername());
        int limit = dataStore.calculateCreditLimit(currentUser.getUsername());
        JLabel ectsLbl = new JLabel("Current Load: " + current + " / " + limit + " ECTS");
        ectsLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topInfo.add(ectsLbl);
        panel.add(topInfo, BorderLayout.NORTH);

        availableCoursesTableModel = new DefaultTableModel(
                new String[] { "Code", "Name", "Instructor", "Credits", "Quota", "Status" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createModernTable(availableCoursesTableModel);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Filter by Instructor:"));
        JComboBox<String> instFilter = new JComboBox<>();
        instFilter.addItem("All Instructors");
        dataStore.getUsers().stream()
            .filter(u -> "Instructor".equalsIgnoreCase(u.getRole()))
            .forEach(u -> instFilter.addItem(u.getUsername()));
        
        instFilter.addActionListener(e -> {
            String selected = (String) instFilter.getSelectedItem();
            @SuppressWarnings("unchecked")
            DefaultRowSorter<DefaultTableModel, Integer> sorter = (DefaultRowSorter<DefaultTableModel, Integer>) table.getRowSorter();
            if ("All Instructors".equals(selected)) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(selected, 2));
            }
        });
        filterPanel.add(instFilter);

        JPanel topWrap = new JPanel(new BorderLayout());
        topWrap.setOpaque(false);
        setupSearch(topWrap, availableCoursesTableModel, table);
        topWrap.add(filterPanel, BorderLayout.WEST);
        
        JPanel northBox = new JPanel(new BorderLayout());
        northBox.setOpaque(false);
        northBox.add(topInfo, BorderLayout.NORTH);
        northBox.add(topWrap, BorderLayout.SOUTH);
        panel.add(northBox, BorderLayout.NORTH);
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton enrollBtn = new JButton("Request Enrollment");
        enrollBtn.setBackground(SAGE_GREEN);
        enrollBtn.setForeground(Color.WHITE);
        enrollBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String code = (String) availableCoursesTableModel.getValueAt(table.convertRowIndexToModel(row), 0);
                
                if (dataStore.isStudentEnrolled(currentUser.getUsername(), code)) {
                    JOptionPane.showMessageDialog(this, "You are already enrolled or have a pending request for this course.");
                    return;
                }

                if (dataStore.requestEnrollment(currentUser.getUsername(), code)) {
                    JOptionPane.showMessageDialog(this, "Enrollment request submitted to Admin.");
                    refreshAvailableCourses(availableCoursesTableModel);
                    int current_new = dataStore.getStudentTotalCredits(currentUser.getUsername());
                    int limit_new = dataStore.calculateCreditLimit(currentUser.getUsername());
                    ectsLbl.setText("Current Load: " + current_new + " / " + limit_new + " ECTS");
                } else {
                    JOptionPane.showMessageDialog(this, "Request failed. Check ECTS limits or major restrictions.");
                }
            }
        });
        panel.add(enrollBtn, BorderLayout.SOUTH);
        refreshAvailableCourses(availableCoursesTableModel);
        return panel;
    }

    private void refreshAvailableCourses(DefaultTableModel model) {
        if (model == null) return;
        model.setRowCount(0);
        dataStore.getCourses().forEach(c -> {
            int count = dataStore.countEnrollmentForCourse(c.getCourseCode());
            Enrollment enr = dataStore.getAllEnrollments().stream()
                    .filter(e -> e.getStudentUsername().equals(currentUser.getUsername()) && e.getCourseCode().equals(c.getCourseCode()))
                    .findFirst().orElse(null);
            
            String status = "Available";
            if (enr != null) {
                status = enr.getStatus();
            } else if (count >= c.getQuota()) {
                status = "Full";
            }
            
            model.addRow(new Object[] { c.getCourseCode(), c.getCourseName(), c.getInstructorUsername(),
                    c.getCredit(), count + "/" + c.getQuota(), status });
        });
    }

    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        myCoursesTableModel = new DefaultTableModel(new String[] { "Code", "Course Name", "Instructor" }, 0);
        JTable table = createModernTable(myCoursesTableModel);
        setupSearch(panel, myCoursesTableModel, table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
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

        adminCoursesTableModel = new DefaultTableModel(
                new String[] { "Code", "Name", "Instructor", "Quota", "Credits" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        adminCoursesTable = createModernTable(adminCoursesTableModel);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Filter by Instructor:"));
        JComboBox<String> instFilter = new JComboBox<>();
        instFilter.addItem("All Instructors");
        dataStore.getUsers().stream()
            .filter(u -> "Instructor".equalsIgnoreCase(u.getRole()))
            .forEach(u -> instFilter.addItem(u.getUsername()));
        
        instFilter.addActionListener(e -> {
            String selected = (String) instFilter.getSelectedItem();
            @SuppressWarnings("unchecked")
            DefaultRowSorter<DefaultTableModel, Integer> sorter = (DefaultRowSorter<DefaultTableModel, Integer>) adminCoursesTable.getRowSorter();
            if ("All Instructors".equals(selected)) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(selected, 2));
            }
        });
        filterPanel.add(instFilter);
        
        JPanel topWrap = new JPanel(new BorderLayout());
        topWrap.setOpaque(false);
        setupSearch(topWrap, adminCoursesTableModel, adminCoursesTable);
        topWrap.add(filterPanel, BorderLayout.WEST);
        panel.add(topWrap, BorderLayout.NORTH);
        
        panel.add(new JScrollPane(adminCoursesTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton addBtn = new JButton("Add Course");
        addBtn.setBackground(SAGE_GREEN);
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> showAddCourseDialog(adminCoursesTableModel, adminCoursesTable));

        JButton updBtn = new JButton("Update Course");
        updBtn.setBackground(SKY_BLUE);
        updBtn.setForeground(Color.WHITE);
        updBtn.addActionListener(e -> showUpdateCourseDialog(adminCoursesTableModel, adminCoursesTable));

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setBackground(TERRACOTTA);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> {
            int row = adminCoursesTable.getSelectedRow();
            if (row >= 0) {
                String code = (String) adminCoursesTableModel.getValueAt(adminCoursesTable.convertRowIndexToModel(row), 0);
                if (dataStore.removeCourse(code)) {
                    refreshCourseTable(adminCoursesTableModel, adminCoursesTable);
                }
            }
        });

        actions.add(addBtn);
        actions.add(updBtn);
        actions.add(deleteBtn);
        panel.add(actions, BorderLayout.SOUTH);

        refreshCourseTable(adminCoursesTableModel, adminCoursesTable);
        return panel;
    }

    private JPanel createEnrollmentManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WARM_BEIGE);
        
        JTabbedPane subTabs = new JTabbedPane();
        subTabs.addTab("Current Enrollments", createManageEnrollmentsSubPanel());
        subTabs.addTab("Pending Requests", createPendingRequestsSubPanel());
        subTabs.addTab("Automation Hub", createBulkEnrollmentSubPanel());
        
        panel.add(subTabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createManageEnrollmentsSubPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        enrollmentsTableModel = new DefaultTableModel(
                new String[] { "Student ID", "Student Name", "Course Code", "Course Name" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        enrollmentsTable = createModernTable(enrollmentsTableModel);
        setupSearch(panel, enrollmentsTableModel, enrollmentsTable);
        panel.add(new JScrollPane(enrollmentsTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton addBtn = new JButton("Add Individual");
        addBtn.setBackground(SAGE_GREEN);
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> showAddEnrollmentDialog());

        JButton deleteBtn = new JButton("Unenroll Selected");
        deleteBtn.setBackground(TERRACOTTA);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> {
            int row = enrollmentsTable.getSelectedRow();
            if (row >= 0) {
                String studentId = (String) enrollmentsTableModel.getValueAt(enrollmentsTable.convertRowIndexToModel(row), 0);
                String courseCode = (String) enrollmentsTableModel.getValueAt(enrollmentsTable.convertRowIndexToModel(row), 2);
                StudentProfile p = dataStore.getStudents().stream().filter(s -> s.getStudentId().equals(studentId)).findFirst().orElse(null);
                if (p != null && dataStore.removeEnrollment(p.getUsername(), courseCode)) {
                    refreshEnrollmentTable();
                }
            }
        });

        actions.add(addBtn);
        actions.add(deleteBtn);
        panel.add(actions, BorderLayout.SOUTH);

        refreshEnrollmentTable();
        return panel;
    }

    private JPanel createPendingRequestsSubPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(WARM_BEIGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        requestsTableModel = new DefaultTableModel(
                new String[] { "Student ID", "Student Name", "Dept", "Course", "Status" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        requestsTable = createModernTable(requestsTableModel);
        setupSearch(panel, requestsTableModel, requestsTable);
        panel.add(new JScrollPane(requestsTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton appBtn = new JButton("Approve");
        appBtn.setBackground(SAGE_GREEN);
        appBtn.setForeground(Color.WHITE);
        appBtn.addActionListener(e -> {
            int row = requestsTable.getSelectedRow();
            if (row >= 0) {
                String studentId = (String) requestsTableModel.getValueAt(requestsTable.convertRowIndexToModel(row), 0);
                String courseCode = (String) requestsTableModel.getValueAt(requestsTable.convertRowIndexToModel(row), 3);
                StudentProfile p = dataStore.getStudents().stream().filter(s -> s.getStudentId().equals(studentId)).findFirst().orElse(null);
                if (p != null && dataStore.approveRequest(p.getUsername(), courseCode)) {
                    refreshRequestsTable();
                    refreshEnrollmentTable();
                }
            }
        });

        JButton rejBtn = new JButton("Reject");
        rejBtn.setBackground(TERRACOTTA);
        rejBtn.setForeground(Color.WHITE);
        rejBtn.addActionListener(e -> {
            int row = requestsTable.getSelectedRow();
            if (row >= 0) {
                String studentId = (String) requestsTableModel.getValueAt(requestsTable.convertRowIndexToModel(row), 0);
                String courseCode = (String) requestsTableModel.getValueAt(requestsTable.convertRowIndexToModel(row), 3);
                StudentProfile p = dataStore.getStudents().stream().filter(s -> s.getStudentId().equals(studentId)).findFirst().orElse(null);
                if (p != null && dataStore.rejectRequest(p.getUsername(), courseCode)) {
                    refreshRequestsTable();
                }
            }
        });

        actions.add(appBtn);
        actions.add(rejBtn);
        panel.add(actions, BorderLayout.SOUTH);

        refreshRequestsTable();
        return panel;
    }

    private JPanel createBulkEnrollmentSubPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(WARM_BEIGE);
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
        info.setBackground(WARM_BEIGE);
        info.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        panel.add(info, gbc);

        gbc.gridy = 3;
        JButton bulkBtn = new JButton("Run Automated Bulk Enrollment");
        bulkBtn.setBackground(SKY_BLUE);
        bulkBtn.setForeground(Color.WHITE);
        bulkBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bulkBtn.setPreferredSize(new Dimension(300, 40));
        bulkBtn.addActionListener(e -> {
            String dept = (String) deptCombo.getSelectedItem();
            int year = (Integer) yearCombo.getSelectedItem();
            if (dept != null) {
                dataStore.bulkEnroll(dept, year);
                refreshEnrollmentTable();
                refreshReportsPanel();
                JOptionPane.showMessageDialog(this, "Bulk enrollment completed for " + dept + " Year " + year);
            }
        });
        panel.add(bulkBtn, gbc);

        return panel;
    }

    private void refreshEnrollmentTable() {
        if (enrollmentsTableModel == null) return;
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

    private void refreshRequestsTable() {
        if (requestsTableModel == null) return;
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
                        refreshEnrollmentTable();
                        JOptionPane.showMessageDialog(this, "Enrolled successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Enrollment failed (already enrolled or quota full).");
                    }
                }
            }
        }
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
        
        // Instructor Search Logic
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
                    refreshCourseTable(model, table);
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
                    refreshCourseTable(model, table);
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
                u -> model.addRow(new Object[] { u.getUsername(), u.getRole(), u.getDepartment(), u.getFullName(), u.getReferenceId() }));

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
