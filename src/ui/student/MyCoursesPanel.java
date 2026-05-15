package ui.student;

import data.DataStore;
import model.Course;
import model.User;
import ui.shared.Refreshable;
import ui.shared.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MyCoursesPanel extends JPanel implements Refreshable {
    private DataStore dataStore;
    private User currentUser;
    private DefaultTableModel myCoursesTableModel;
    private JTable table;

    public MyCoursesPanel(DataStore dataStore, User currentUser) {
        this.dataStore = dataStore;
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 15));
        setBackground(UIUtils.WARM_BEIGE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initUI();
    }

    private void initUI() {
        myCoursesTableModel = new DefaultTableModel(new String[] { "Code", "Course Name", "Instructor" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIUtils.createModernTable(myCoursesTableModel);
        
        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        UIUtils.setupSearch(searchWrap, myCoursesTableModel, table);
        
        add(searchWrap, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh();
    }

    @Override
    public void refresh() {
        if (myCoursesTableModel == null) return;
        myCoursesTableModel.setRowCount(0);
        dataStore.getEnrollmentsByStudent(currentUser.getUsername()).stream()
            .filter(e -> "APPROVED".equals(e.getStatus()))
            .forEach(e -> {
                Course c = dataStore.findCourse(e.getCourseCode());
                if (c != null) {
                    myCoursesTableModel.addRow(new Object[] { c.getCourseCode(), c.getCourseName(), c.getInstructorUsername() });
                }
            });
    }
}
