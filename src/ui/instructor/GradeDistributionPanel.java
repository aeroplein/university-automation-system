package ui.instructor;

import data.DataStore;
import model.Course;
import model.User;
import ui.GradeDistributionChart;
import ui.shared.Refreshable;
import ui.shared.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class GradeDistributionPanel extends JPanel implements Refreshable {
    private DataStore dataStore;
    private User currentUser;
    private JComboBox<String> gradeDistCourseCombo;
    private JPanel gradeDistChartContainer;

    public GradeDistributionPanel(DataStore dataStore, User currentUser) {
        this.dataStore = dataStore;
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 15));
        setBackground(UIUtils.WARM_BEIGE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initUI();
    }

    private void initUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        top.setOpaque(false);
        top.add(new JLabel("Select Course for Chart:"));

        gradeDistCourseCombo = new JComboBox<>();
        top.add(gradeDistCourseCombo);
        add(top, BorderLayout.NORTH);

        gradeDistChartContainer = new JPanel(new BorderLayout());
        gradeDistChartContainer.setOpaque(false);
        add(gradeDistChartContainer, BorderLayout.CENTER);

        gradeDistCourseCombo.addActionListener(e -> refreshGradeDistribution());

        refresh();
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

    @Override
    public void refresh() {
        if (gradeDistCourseCombo != null) {
            Object selected = gradeDistCourseCombo.getSelectedItem();
            List<Course> myCourses = dataStore.getCoursesByInstructor(currentUser.getUsername());
            gradeDistCourseCombo.setModel(new DefaultComboBoxModel<>(
                    myCourses.stream().map(c -> c.getCourseCode()).toArray(String[]::new)));
            if (selected != null) {
                gradeDistCourseCombo.setSelectedItem(selected);
            }
        }
        refreshGradeDistribution();
    }
}
