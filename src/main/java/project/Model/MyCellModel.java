package project.Model;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MyCellModel extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (isSelected || hasFocus || (table.getSelectedRow() == row && table.getSelectedColumn() == column)) {
            cell.setBackground(Color.CYAN);
        }
        else
            cell.setBackground(new Color(0xFFFFFF));

        return cell;
    }

}
