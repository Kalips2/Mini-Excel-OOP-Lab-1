package project;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyCell{
    String value;
    String expression;
    int row;
    int column;
    String name;
    public List<MyCell> IDependedFrom; //A3 = A2 + A5
    public List<MyCell> DependedCells;

    MyCell(int row, int column) {
        value = "";
        expression = "";
        this.row = row;
        this.column = column;
    }
    public MyCell(int row, int column, String value, String expression, String name) {
        this.name = name;
        this.value = value;
        this.expression = expression;
        this.row = row;
        this.column = column;
        IDependedFrom = new ArrayList<>();
        DependedCells = new ArrayList<>();
    }

}
