package ui.instructor;

// Touched to trigger IDE compilation re-index
import data.DataStore;
import model.Course;
import model.User;
import ui.shared.Refreshable;
import ui.shared.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InstructorCoursesPanel extends JPanel implements Refreshable {
    private DataStore dataStore;
    private User currentUser;
    private DefaultTableModel instructorCoursesTableModel;
    private JTable table;

    public InstructorCoursesPanel(DataStore dataStore, User currentUser) {
        this.dataStore = dataStore;
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 15));
        setBackground(UIUtils.WARM_BEIGE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initUI();
    }

    private void initUI() {
        instructorCoursesTableModel = new DefaultTableModel(new String[] { "Code", "Name", "Enrollments" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = UIUtils.createModernTable(instructorCoursesTableModel);
        
        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        UIUtils.setupSearch(searchWrap, instructorCoursesTableModel, table);
        
        add(searchWrap, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh();
    }

    @Override
    public void refresh() {
        if (instructorCoursesTableModel == null) return;
        instructorCoursesTableModel.setRowCount(0);
        List<Course> myCourses = dataStore.getCoursesByInstructor(currentUser.getUsername());
        myCourses.forEach(c -> instructorCoursesTableModel.addRow(new Object[] { c.getCourseCode(), c.getCourseName(),
                dataStore.countEnrollmentForCourse(c.getCourseCode()) }));
    }
}
