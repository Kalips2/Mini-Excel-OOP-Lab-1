package project.Calculator;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import project.MyCell;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lexer {

    MyCell[][] data;
    String p_input;

    Token cur_token;
    String cur_token_text;

    MyCell currentCell;
    String bank = "PLUS,\n" +
            "    MINUS,\n" +
            "    MUL,\n" +
            "    DIV,\n" +
            "    MOD,\n" +
            "    POW," + "LP";

    String buffer; //
    int i = 0;
    int length;

    private Token get_token() throws RuntimeException {
        buffer = "";

        if (i >= length || length == 0) return Token.END_OF;

        char c = p_input.charAt(i);
        while (i < length && Character.isSpaceChar(c)) c = p_input.charAt(++i);

        if (Character.isLetter(c)) {
            buffer = String.valueOf(c);
            i++;
            while (i < length && Character.isLetterOrDigit(p_input.charAt(i))) {
                c = p_input.charAt(i);
                buffer += c;
                i++;
            }

            buffer.toLowerCase();

            if (buffer.equals("sin")) return Token.SIN;
            if (buffer.equals("cos")) return Token.COS;
            if (buffer.equals("sqrt")) return Token.SQRT;
            if (buffer.equals("inc")) return Token.INC;
            if (buffer.equals("dec")) return Token.DEC;
            if (buffer.equals("mmax")) return Token.MMAX;
            if (buffer.equals("mmin")) return Token.MMIN;

            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    if (data[i][j].getName().equals(buffer)) { //A2 = A5 + 6
                        if (!currentCell.MyDependens.contains(data[i][j]))
                            currentCell.MyDependens.add(data[i][j]);
                        if (!data[i][j].DependedCells.contains(currentCell))
                            data[i][j].DependedCells.add(currentCell);
                        if (checkToRecursion(currentCell, currentCell)) {
                            currentCell.MyDependens.remove(data[i][j]);
                            data[i][j].DependedCells.remove(currentCell);
                            throw new RuntimeException("Ви зациклили комірки! Введіть формулу ще раз!");
                        }
                        //System.out.println(data[i][j].getName() + " Lexer : Depended on me is " + data[i][j].getDependedCells().size());
                        //System.out.println("cur = " + currentCell.getName() + " I Depended from  " + data[i][j].getName());
                        return Token.CELL;
                    }
                }
            }
            throw new RuntimeException("Перевірте написання функцій або така клітинка не існує!");

        }
        if (Character.isDigit(c) || (c == '-' && (cur_token == null || bank.contains(cur_token.toString())))) {
            buffer += String.valueOf(c);
            i++;
            while (i < length && Character.isDigit(p_input.charAt(i))) {
                c = p_input.charAt(i);
                buffer += c;
                i++;
            }
            if (i < length - 1&& p_input.charAt(i) == '.') {
                buffer += p_input.charAt(i);
                i++;

                if (!Character.isDigit(p_input.charAt(i))) {
                    throw new RuntimeException("No digit after \'.\'");
                }
                while (i < length && Character.isDigit(p_input.charAt(i))) {
                    c = p_input.charAt(i);
                    buffer += c;
                    i++;
                }
            }
            return Token.NUMBER;
        }
        buffer = String.valueOf(p_input.charAt(i));
        switch (c) {
            case '+':
                i++;
                return Token.PLUS;
            case '-':
                i++;
                return Token.MINUS;
            case '*':
                i++;
                return Token.MUL;
            case '/':
                i++;
                return Token.DIV;
            case '%':
                i++;
                return Token.MOD;
            case '^':
                i++;
                return Token.POW;
            case '(':
                i++;
                return Token.LP;
            case ')':
                i++;
                return Token.RP;
            case ',':
                i++;
                return Token.COMA;
        }
        throw new RuntimeException("Перевірте написання функцій або така клітинка не існує!");
    }

    Lexer(String p_input, MyCell[][] data, MyCell currentCell) throws Exception {
        this.currentCell = currentCell;
        this.data = data;
        this.p_input = p_input.trim();
        length = this.p_input.length();
        cur_token = get_token();
        cur_token_text = buffer;
    }

    public void advance() throws Exception {
        if (cur_token != Token.END_OF) {
            cur_token = get_token();
            cur_token_text = buffer;
        }
    }

    private boolean checkToRecursion(MyCell Current, MyCell Initial) {
        if (Current.MyDependens.contains(Initial)) {
            return true;
        }
        for (var x : Current.MyDependens) {
            if (checkToRecursion(x, Initial))
                return true;
        }
        return false;
    }
}
