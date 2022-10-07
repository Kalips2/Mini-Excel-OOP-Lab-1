package project.Model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import project.MyCell;

import javax.swing.table.DefaultTableModel;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainModel extends DefaultTableModel {

    MyCell[][] data;
    String[] columnNames;
    int ROW_SIZE;
    int COLUMN_SIZE;
    String path = "";
    boolean saved = false;

    public MainModel(int ROW_SIZE, int COLUMN_SIZE) {
        this.ROW_SIZE = ROW_SIZE;
        this.COLUMN_SIZE = COLUMN_SIZE;
        char A = 'A';
        columnNames = new String[COLUMN_SIZE];
        for(int i = 0; i < COLUMN_SIZE; i++) {
            columnNames[i] = Character.toString(A++);
        }

        data = new MyCell[ROW_SIZE][COLUMN_SIZE];
        for(int i = 0; i < ROW_SIZE; i++) {
            for(int j = 0; j < COLUMN_SIZE; j++) {
                data[i][j] = new MyCell(i, j, Integer.toString(i + j), String.valueOf("A" + i + " " + "B+ "));
            }
        }

    }

    @Override
    public int getRowCount() {
        return ROW_SIZE;
    }

    @Override
    public int getColumnCount() {
        return COLUMN_SIZE;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex].getValue();
    }

    public String getExpressionAt(int row, int column) {
        return data[row][column].getExpression();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data[rowIndex][columnIndex].setValue(aValue.toString());
        super.fireTableRowsUpdated(rowIndex, columnIndex);
    }
    public void setExpressionAt(Object aValue, int rowIndex, int columnIndex) {
        String exp = aValue.toString().trim();
        System.out.print("\'" + exp + "\'" + " - value from TextBox" + " check to 1: ");
        System.out.println(exp.equals("1"));
        if(exp.equals("")) setValueAt("DEAD INSIDE",rowIndex, columnIndex);
        else if(exp.equals("1")) setValueAt("New", rowIndex, columnIndex);
        else setValueAt("I AM ALIVE", rowIndex, columnIndex);
        data[rowIndex][columnIndex].setExpression(aValue.toString());
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public void addRow(Object[] rowData) {
        MyCell[][] newData = new MyCell[getROW_SIZE() + 1][getCOLUMN_SIZE()];
        for(int i = 0; i < getROW_SIZE(); i++) {
            for(int j = 0; j < getCOLUMN_SIZE(); j++) {
                newData[i][j] = data[i][j];
            }
        }
        for(int i = 0; i < getCOLUMN_SIZE(); i++) {
            newData[getROW_SIZE()][i] = (MyCell) rowData[i];
        }

        data = newData;
        setROW_SIZE(getROW_SIZE()+1);

        fireTableRowsInserted(getROW_SIZE() - 1, getCOLUMN_SIZE() - 1);
    }

    @Override
    public void addColumn(Object columnName) {

        System.out.println(getROW_SIZE() + " " + getCOLUMN_SIZE());
        MyCell[][] newData = new MyCell[getROW_SIZE()][getCOLUMN_SIZE() + 1];
        for(int i = 0; i < getROW_SIZE(); i++) {
            for(int j = 0; j < getCOLUMN_SIZE(); j++) {
                newData[i][j] = data[i][j];
            }
        }
        for(int i = 0; i < getROW_SIZE(); i++) {
            newData[i][getCOLUMN_SIZE()] = new MyCell(i, getCOLUMN_SIZE(), "", "");
        }
        data = newData;

        String[] newColumnsName = new String[getCOLUMN_SIZE() + 1];
        for(int i = 0; i < getCOLUMN_SIZE(); i++)
            newColumnsName[i] = columnNames[i];
        setCOLUMN_SIZE(getCOLUMN_SIZE() + 1);
        newColumnsName[getCOLUMN_SIZE() - 1] = Character.toString('A' + getCOLUMN_SIZE());
        columnNames = newColumnsName;

        fireTableStructureChanged();


    }

    public void removeRow() {
        MyCell[][] newTable = new MyCell[getROW_SIZE() - 1][getCOLUMN_SIZE()];

        for(int i = 0; i < getROW_SIZE() - 1; i++) {
            for(int j = 0; j <getCOLUMN_SIZE(); j++)
                newTable[i][j] = data[i][j];
        }
        data = newTable;
        setROW_SIZE(getROW_SIZE() - 1);

        fireTableRowsDeleted(getROW_SIZE() + 1, getROW_SIZE() + 1);
    }

    public void removeColumn() {
        MyCell[][] newTable = new MyCell[getROW_SIZE()][getCOLUMN_SIZE() - 1];
        for(int i = 0; i < getROW_SIZE(); i++) {
            for(int j = 0; j <getCOLUMN_SIZE() - 1; j++)
                newTable[i][j] = data[i][j];
        }
        data = newTable;
        setCOLUMN_SIZE(getCOLUMN_SIZE() - 1);

        fireTableStructureChanged();

    }


}
