package ui.shared;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Modern UI helper utilities for University Automation System.
 */
public class UIUtils {
    public static final Color COCOA_BROWN = new Color(0x2D241E);
    public static final Color SAGE_GREEN = new Color(0x7C9070);
    public static final Color WARM_BEIGE = new Color(0xFDFBF7);
    public static final Color CREAM_SAND = new Color(0xE9EDDF);
    public static final Color TERRACOTTA = new Color(0xD98880);
    public static final Color SKY_BLUE = new Color(0x5D9CEC);

    public static void setupSearch(JPanel container, DefaultTableModel model, JTable table) {
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

    public static JTable createModernTable(DefaultTableModel model) {
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
}
