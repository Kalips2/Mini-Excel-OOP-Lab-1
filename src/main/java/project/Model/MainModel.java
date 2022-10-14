package project.Model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import project.Calculator.MyParser;
import project.MyCell;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.html.parser.Parser;
import java.text.DecimalFormat;
import java.util.ArrayList;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainModel extends DefaultTableModel {

    MyCell[][] data;
    String[] columnNames;
    int ROW_SIZE;
    int COLUMN_SIZE;
    String path = "";
    boolean saved = false;
    MyParser parser;

    public int counter = 0;

    public MainModel(int ROW_SIZE, int COLUMN_SIZE) {
        parser = new MyParser();
        this.ROW_SIZE = ROW_SIZE;
        this.COLUMN_SIZE = COLUMN_SIZE;
        char A = 'A';
        columnNames = new String[COLUMN_SIZE];
        for (int i = 0; i < COLUMN_SIZE; i++) {
            columnNames[i] = Character.toString(A++);
        }

        data = new MyCell[ROW_SIZE][COLUMN_SIZE];
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COLUMN_SIZE; j++) {
                String name = (j / 26 < 1 ? "" : Character.valueOf((char) (j / 26 + 64)).toString()) + Character.valueOf((char) (j % 26 + 65)).toString() + (i + 1);
                data[i][j] = new MyCell(i, j, "", "", name);
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

    public void setExpressionAt(Object aValue, int rowIndex, int columnIndex) throws Exception {
        MyCell currentCell = data[rowIndex][columnIndex];
        if (currentCell.DependedCells.size() == 0) {
            calculateAndSetUp(aValue, rowIndex, columnIndex);
        } else {
            int k = 0;
            calculateAndSetUp(aValue, rowIndex, columnIndex);
            while (data[rowIndex][columnIndex].DependedCells.size() != k) {
                for (int i = 0; i < currentCell.DependedCells.size(); i++) {
                    setExpressionAt(currentCell.DependedCells.get(i).getExpression(), currentCell.DependedCells.get(i).getRow(), currentCell.DependedCells.get(i).getColumn());
                }
                k++;
            }
        }

        fireTableDataChanged();

    }

    private void calculateAndSetUp(Object aValue, int rowIndex, int columnIndex) throws Exception {

        String text = aValue.toString();
        if (text.length() > 1 && text.charAt(0) == '=') text = text.substring(1);
        double value = parser.Calculate(text, data, data[rowIndex][columnIndex]);
        String newValue = String.valueOf(value);
        data[rowIndex][columnIndex].setExpression(aValue.toString());
        data[rowIndex][columnIndex].setValue(newValue);
    }

    @Override
    public void addRow(Object[] rowData) {
        MyCell[][] newData = new MyCell[getROW_SIZE() + 1][getCOLUMN_SIZE()];
        for (int i = 0; i < getROW_SIZE(); i++) {
            for (int j = 0; j < getCOLUMN_SIZE(); j++) {
                newData[i][j] = data[i][j];
            }
        }
        for (int i = 0; i < getCOLUMN_SIZE(); i++) {
            newData[getROW_SIZE()][i] = (MyCell) rowData[i];
        }
        data = newData;
        setROW_SIZE(getROW_SIZE() + 1);

        fireTableRowsInserted(getROW_SIZE() - 1, getCOLUMN_SIZE() - 1);
    }

    @Override
    public void addColumn(Object columnName) {

        MyCell[][] newData = new MyCell[getROW_SIZE()][getCOLUMN_SIZE() + 1];
        for (int i = 0; i < getROW_SIZE(); i++) {
            for (int j = 0; j < getCOLUMN_SIZE(); j++) {
                newData[i][j] = data[i][j];
            }
        }
        for (int i = 0; i < getROW_SIZE(); i++) {
            String name = (getCOLUMN_SIZE() / 26 < 1 ? "" : Character.valueOf((char) (getCOLUMN_SIZE() / 26 + 64)).toString()) + Character.valueOf((char) (getCOLUMN_SIZE() % 26 + 65)).toString() + (i + 1);
            newData[i][getCOLUMN_SIZE()] = new MyCell(i, getCOLUMN_SIZE(), "", "", name);
        }
        data = newData;

        String[] newColumnsName = new String[getCOLUMN_SIZE() + 1];
        for (int i = 0; i < getCOLUMN_SIZE(); i++)
            newColumnsName[i] = columnNames[i];
        setCOLUMN_SIZE(getCOLUMN_SIZE() + 1);
        newColumnsName[getCOLUMN_SIZE() - 1] = Character.toString('A' + getCOLUMN_SIZE());
        columnNames = newColumnsName;

        fireTableStructureChanged();


    }

    public void removeRow() {
        MyCell[][] newTable = new MyCell[getROW_SIZE() - 1][getCOLUMN_SIZE()];

        for (int i = 0; i < getROW_SIZE() - 1; i++) {
            for (int j = 0; j < getCOLUMN_SIZE(); j++)
                newTable[i][j] = data[i][j];
        }
        data = newTable;
        setROW_SIZE(getROW_SIZE() - 1);

        fireTableRowsDeleted(getROW_SIZE() + 1, getROW_SIZE() + 1);
    }

    public void removeColumn() {
        MyCell[][] newTable = new MyCell[getROW_SIZE()][getCOLUMN_SIZE() - 1];
        for (int i = 0; i < getROW_SIZE(); i++) {
            for (int j = 0; j < getCOLUMN_SIZE() - 1; j++)
                newTable[i][j] = data[i][j];
        }
        data = newTable;
        setCOLUMN_SIZE(getCOLUMN_SIZE() - 1);
        fireTableStructureChanged();
    }

}
