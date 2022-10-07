package project.ParserToSave;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import project.Model.MainModel;
import project.MyCell;

import java.io.*;
import java.util.Arrays;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeneratorFile {

    MainModel model;
    String path;

    public GeneratorFile(MainModel model, String path) {
        this.model = model;
        this.path = path;
    }

    public void createTableHowText(MainModel model, String path) throws IOException {
        this.model = model;
        this.path = path;

        File file = new File(path);
        FileWriter fr = new FileWriter(file);
        BufferedWriter br = new BufferedWriter(fr);
        String text = "";

        int row, column;
        row = model.getROW_SIZE();
        column = model.getCOLUMN_SIZE();
        text = text + row + " " + column + '\n';
        for(int i = 0; i < row; i++) {
            for(int j = 0; j < column; j++) {
                text+="\"" + model.getValueAt(i, j) + "\"," + "\"" + model.getExpressionAt(i, j) + "\"; ";
            }
            text+="\n";
        }

        br.write(text);
        br.flush();
        br.close();
        fr.close();

    }

    public static MainModel createTableFromText(String path) throws IOException {
        File file = new File(path);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        int row, column;
        String text1 = bufferedReader.readLine();
        row = Integer.parseInt(text1.split("\\s+")[0]);
        column = Integer.parseInt(text1.split("\\s+")[1]);
        MainModel newModel = new MainModel(row, column);
        MyCell[][] newData = new MyCell[row][column];
        StringBuilder text = new StringBuilder("");
        for(int i = 0; i < row; i++) {
            String[] values = bufferedReader.readLine().toString().split(";");
            System.out.println(Arrays.toString(values));
            for(int j = 0; j < column; j++) {
                String f = values[j].split(",")[0].trim();
                String s = values[j].split(",")[1].trim();
                String first = f.equals("") ? "" : f.substring(1, f.length() - 1);
                String second =s.equals("") ? "" : s.substring(1, s.length() - 1);
                //System.out.println("   \'" + first + "\' " + "\'" + second + "\'");
                newData[i][j] = new MyCell(i, j, first,second);
            }
        }
        newModel.setData(newData);
        return newModel;
    }
}
