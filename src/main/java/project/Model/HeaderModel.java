package project.Model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.swing.table.DefaultTableModel;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HeaderModel extends DefaultTableModel {
    int[][] value;
    int row;
    int column = 1;
    public HeaderModel(int row, int column) {
        this.row = row;
        value = new int[row][this.column];
        for(int i = 0; i < getRow(); i++){
            value[i][0] = i+1;
        }
        System.out.println("row in header = " + row);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    @Override
    public int getRowCount() {
        return row;
    }
    @Override
    public int getColumnCount() {
        return column;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return value[rowIndex][columnIndex];
    }

    public void addRow() {
        int[] newRow2 = new int[1];
        newRow2[0] = row+1;
        int[][] newValue = new int[getRow() + 1][getColumn()];
        for(int i = 0; i < getRow(); i++) {
            for(int j = 0; j < getColumn(); j++) {
                newValue[i][j] = value[i][j];
            }
        }
        for(int i = 0; i < getColumn(); i++) {
            newValue[getRow()][i] = (int) newRow2[i];
        }

        value = newValue;
        setRow(getRow()+1);

        fireTableRowsInserted(getRow() - 1, getColumn() - 1);
    }

    public void removeRow() {
        int[][] newTable = new int[getRow() - 1][getColumn()];

        for(int i = 0; i < getRow() - 1; i++) {
            newTable[i][0] = value[i][0];
        }
        value = newTable;
        setRow(getRow() - 1);

        fireTableRowsDeleted(getRow() + 1, getRow() + 1);
    }
}
