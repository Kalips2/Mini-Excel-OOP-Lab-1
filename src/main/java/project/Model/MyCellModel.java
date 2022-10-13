package project.Model;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MyCellModel extends DefaultTableCellRenderer {
    private JTextField textField;
    public MyCellModel(JTextField textField) {
        this.textField = textField;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        textField.requestFocus();
        JComponent c = (JComponent) super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);


        if (isSelected || hasFocus || (table.getSelectedRow() == row && table.getSelectedColumn() == column)) {
            cell.setBackground(new Color(182, 239, 250));
            Border insideMargin = BorderFactory.createEmptyBorder(3, 3, 3, 3);
            Border thickBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
            Border borderWithMargin =
                    BorderFactory.createCompoundBorder(thickBorder, insideMargin);
            c.setBorder(borderWithMargin);

        }
        else {
            cell.setBackground(new Color(0xFFFFFF));
            cell.setForeground(Color.BLACK);
        }

        return cell;
    }



}
