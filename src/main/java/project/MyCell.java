package project;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyCell{
    String value;
    String expression;
    int row;
    int column;
    ArrayList<MyCell> listeners;
    ArrayList<MyCell> references;

    MyCell(int row, int column) {
        value = "";
        expression = "";
        this.row = row;
        this.column = column;
        listeners = new ArrayList<>();
        references = new ArrayList<>();
    }
    public MyCell(int row, int column, String value, String expression) {
        this.value = value;
        this.expression = expression;
        this.row = row;
        this.column = column;
        listeners = new ArrayList<>();
        references = new ArrayList<>();
    }

}
