package ui;


import com.formdev.flatlaf.FlatIntelliJLaf;
import data.DataStore;
import model.User;
import ui.admin.*;
import ui.instructor.*;
import ui.student.*;
import ui.shared.Refreshable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;


/**
 * University Automation System - Premium Tabbed UI
 * Features the 'UniAuto' Earth-Tone Palette and Tabbed Dashboard.
 * Modularized Architecture.
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

        // The dashboard itself needs a reference to its tabs to trigger refresh from the refresh button
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(SKY_BLUE);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            if (index != -1) {
                Component comp = tabbedPane.getComponentAt(index);
                if (comp instanceof Refreshable) {
                    ((Refreshable) comp).refresh();
                }
            }
        });
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
                Component comp = tabbedPane.getComponentAt(index);
                if (comp instanceof Refreshable) {
                    ((Refreshable) comp).refresh();
                }
            }
        });

        mainPanel.add(dashboard, DASHBOARD_CARD);
        cardLayout.show(mainPanel, DASHBOARD_CARD);
    }

    private void setupAdminTabs(JTabbedPane tabs) {
        tabs.addTab("User Management", new UserManagementPanel(dataStore));
        tabs.addTab("Structural Management", new StructureManagementPanel(dataStore));
        tabs.addTab("Curriculum Management", new CurriculumManagementPanel(dataStore));
        tabs.addTab("Student Management", new StudentManagementPanel(dataStore));
        tabs.addTab("Course Management", new CourseManagementPanel(dataStore));
        tabs.addTab("Enrollment Management", new EnrollmentManagementPanel(dataStore));
        tabs.addTab("System Reports", new ReportsPanel(dataStore));
    }

    private void setupInstructorTabs(JTabbedPane tabs) {
        tabs.addTab("My Courses", new InstructorCoursesPanel(dataStore, currentUser));
        tabs.addTab("Grade Entry", new GradeEntryPanel(dataStore, currentUser));
        tabs.addTab("Grade Distribution", new GradeDistributionPanel(dataStore, currentUser));
    }

    private void setupStudentTabs(JTabbedPane tabs) {
        tabs.addTab("Available Courses", new AvailableCoursesPanel(dataStore, currentUser));
        tabs.addTab("My Courses", new MyCoursesPanel(dataStore, currentUser));
        tabs.addTab("Transcript", new TranscriptPanel(dataStore, currentUser));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UniversityAutomationApp().setVisible(true);
        });
    }
}
