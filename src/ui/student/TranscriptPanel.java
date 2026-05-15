package ui.student;

import data.DataStore;
import model.Course;
import model.User;
import util.TranscriptExporter;
import ui.shared.Refreshable;
import ui.shared.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TranscriptPanel extends JPanel implements Refreshable {
    private DataStore dataStore;
    private User currentUser;
    private DefaultTableModel transcriptTableModel;
    private JTable table;
    private JLabel gpaLabel;

    public TranscriptPanel(DataStore dataStore, User currentUser) {
        this.dataStore = dataStore;
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 15));
        setBackground(UIUtils.WARM_BEIGE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initUI();
    }

    private void initUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        gpaLabel = new JLabel("GPA: 0.00");
        gpaLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gpaLabel.setForeground(new Color(0x333399));
        top.add(gpaLabel, BorderLayout.WEST);

        add(top, BorderLayout.NORTH);

        transcriptTableModel = new DefaultTableModel(
                new String[] { "Course Code", "Course Name", "Credit", "Midterm", "Final", "Average", "Letter Grade" },
                0);
        table = UIUtils.createModernTable(transcriptTableModel);
        
        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        UIUtils.setupSearch(searchWrap, transcriptTableModel, table);
        
        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(searchWrap, BorderLayout.NORTH);
        centerWrap.add(new JScrollPane(table), BorderLayout.CENTER);
        add(centerWrap, BorderLayout.CENTER);

        JButton exportPdfBtn = new JButton("Export PDF Transcript");
        exportPdfBtn.setBackground(UIUtils.SKY_BLUE);
        exportPdfBtn.setForeground(Color.WHITE);
        exportPdfBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save PDF Transcript");
            fileChooser.setSelectedFile(new java.io.File("transcript_" + currentUser.getUsername() + ".pdf"));
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Documents", "pdf"));
            
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                String path = fileToSave.getAbsolutePath();
                if (!path.toLowerCase().endsWith(".pdf")) {
                    path += ".pdf";
                }
                String res = TranscriptExporter.export(currentUser.getUsername(), path);
                JOptionPane.showMessageDialog(this, res);
            }
        });

        JButton exportTxtBtn = new JButton("Export TXT Transcript");
        exportTxtBtn.setBackground(UIUtils.SAGE_GREEN);
        exportTxtBtn.setForeground(Color.WHITE);
        exportTxtBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save TXT Transcript");
            fileChooser.setSelectedFile(new java.io.File("transcript_" + currentUser.getUsername() + ".txt"));
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Documents", "txt"));
            
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                String path = fileToSave.getAbsolutePath();
                if (!path.toLowerCase().endsWith(".txt")) {
                    path += ".txt";
                }
                String res = TranscriptExporter.export(currentUser.getUsername(), path);
                JOptionPane.showMessageDialog(this, res);
            }
        });

        JPanel btm = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btm.setOpaque(false);
        btm.add(exportTxtBtn);
        btm.add(exportPdfBtn);
        add(btm, BorderLayout.SOUTH);

        refresh();
    }

    @Override
    public void refresh() {
        if (transcriptTableModel == null || gpaLabel == null) return;
        transcriptTableModel.setRowCount(0);
        dataStore.getGradesByStudent(currentUser.getUsername()).forEach(g -> {
            Course c = dataStore.findCourse(g.getCourseCode());
            transcriptTableModel.addRow(new Object[] { g.getCourseCode(), c != null ? c.getCourseName() : "-",
                    c != null ? c.getCredit() : "-", g.getMidterm(), g.getFinalExam(),
                    String.format("%.2f", g.calculateAverage()), g.getLetterGrade() });
        });
        gpaLabel.setText(String.format("GPA: %.2f", dataStore.calculateGPA(currentUser.getUsername())));
    }
}
