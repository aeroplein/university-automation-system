package ui.instructor;

import data.DataStore;
import model.GradeRecord;
import ui.shared.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom Grade Distribution Chart.
 * Implemented using pure Java Graphics2D — zero external dependencies.
 * Belongs to the instructor subpackage since it is only used there.
 */
public class GradeDistributionChart extends JPanel {

    private final String courseCode;
    private Map<String, Integer> distribution;
    private int maxCount = 0;
    private final String[] grades = {"AA", "BA", "BB", "CB", "CC", "DC", "DD", "FD", "FF"};

    public GradeDistributionChart(String courseCode) {
        this.courseCode = courseCode;
        this.setBackground(UIUtils.WARM_BEIGE);
        calculateDistribution();
    }

    private void calculateDistribution() {
        distribution = new HashMap<>();
        for (String g : grades) distribution.put(g, 0);

        List<GradeRecord> records = DataStore.getInstance().getGradesByCourse(courseCode);
        for (GradeRecord r : records) {
            String letter = r.getLetterGrade();
            distribution.put(letter, distribution.getOrDefault(letter, 0) + 1);
        }

        for (int count : distribution.values()) {
            if (count > maxCount) maxCount = count;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int padding = 50;
        int width   = getWidth()  - 2 * padding;
        int height  = getHeight() - 2 * padding - 40;
        int barWidth = width / grades.length - 20;

        // Title
        g2.setColor(UIUtils.COCOA_BROWN);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        String title = "Grade Distribution: " + courseCode;
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (getWidth() - titleWidth) / 2, 35);

        // X-axis
        g2.setColor(UIUtils.CREAM_SAND);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(padding, height + padding, padding + width, height + padding);

        // Bars
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        for (int i = 0; i < grades.length; i++) {
            String grade = grades[i];
            int count     = distribution.get(grade);
            int barHeight = maxCount == 0 ? 0 : (int) ((double) count / maxCount * height);

            int x = padding + i * (width / grades.length) + 10;
            int y = height + padding - barHeight;

            if (count > 0) {
                GradientPaint gp = new GradientPaint(
                        x, y, UIUtils.SAGE_GREEN,
                        x, y + barHeight, UIUtils.SAGE_GREEN.darker());
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(x, y, barWidth, barHeight, 10, 10));

                g2.setColor(UIUtils.COCOA_BROWN);
                String countStr = String.valueOf(count);
                int countW = g2.getFontMetrics().stringWidth(countStr);
                g2.drawString(countStr, x + (barWidth - countW) / 2, y - 5);
            }

            // Grade label under bar
            g2.setColor(UIUtils.COCOA_BROWN);
            int labelW = g2.getFontMetrics().stringWidth(grade);
            g2.drawString(grade, x + (barWidth - labelW) / 2, height + padding + 20);
        }
    }

    /** Convenience factory used by GradeDistributionPanel. */
    public static JPanel buildPanel(String courseCode) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIUtils.WARM_BEIGE);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        container.add(new GradeDistributionChart(courseCode), BorderLayout.CENTER);
        return container;
    }
}
