package ui.instructor;

import data.DataStore;
import model.Course;
import model.GradeRecord;
import model.StudentProfile;
import model.User;
import ui.shared.Refreshable;
import ui.shared.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GradeEntryPanel extends JPanel implements Refreshable {
    private DataStore dataStore;
    private User currentUser;
    private DefaultTableModel gradeEntryTableModel;
    private JTable table;
    private JComboBox<String> gradeEntryCourseCombo;

    public GradeEntryPanel(DataStore dataStore, User currentUser) {
        this.dataStore = dataStore;
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 10));
        setBackground(UIUtils.WARM_BEIGE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initUI();
    }

    private void initUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        top.setOpaque(false);
        top.add(new JLabel("Select Course:"));

        gradeEntryCourseCombo = new JComboBox<>();
        top.add(gradeEntryCourseCombo);

        JButton loadBtn = new JButton("Load Students");
        loadBtn.setBackground(UIUtils.SKY_BLUE);
        loadBtn.setForeground(Color.WHITE);
        top.add(loadBtn);
        add(top, BorderLayout.NORTH);

        gradeEntryTableModel = new DefaultTableModel(
                new String[] { "Student Username", "Student Name", "Midterm", "Final", "Average", "Letter Grade" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = UIUtils.createModernTable(gradeEntryTableModel);
        
        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        UIUtils.setupSearch(searchWrap, gradeEntryTableModel, table);
        
        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(searchWrap, BorderLayout.NORTH);
        centerWrap.add(new JScrollPane(table), BorderLayout.CENTER);
        add(centerWrap, BorderLayout.CENTER);

        JButton entryBtn = new JButton("Enter Grade");
        entryBtn.setBackground(UIUtils.SKY_BLUE);
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
                JOptionPane.showMessageDialog(this, "Please select a student first.");
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
        add(btm, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> refreshGradeEntryTable());

        refresh();
    }

    private void showGradeEntryDialog(String username, String fullName, String courseCode) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Grade Entry - " + fullName, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(UIUtils.WARM_BEIGE);

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
        save.setBackground(UIUtils.SAGE_GREEN);
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
        if (sel == null) return;
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

    @Override
    public void refresh() {
        if (gradeEntryCourseCombo != null) {
            Object selected = gradeEntryCourseCombo.getSelectedItem();
            List<Course> courses = dataStore.getCoursesByInstructor(currentUser.getUsername());
            gradeEntryCourseCombo.setModel(new DefaultComboBoxModel<>(
                    courses.stream().map(c -> c.getCourseCode() + " - " + c.getCourseName()).toArray(String[]::new)));
            if (selected != null) {
                gradeEntryCourseCombo.setSelectedItem(selected);
            }
        }
        refreshGradeEntryTable();
    }
}
