package project.Calculator;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import project.MyCell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyParser {
    MyCell[][] data;
    Lexer lexer;

    private double add_expr() throws Exception {
        double result = mul_expr();

        for (; ; ) {
            switch (lexer.getCur_token()) {
                case PLUS:
                    lexer.advance();
                    result += mul_expr();
                    break;
                case MINUS:
                    lexer.advance();
                    result -= mul_expr();
                    break;
                default:
                    return result;
            }
        }
    }

    private double mul_expr() throws Exception {
        double result = pow_expr();
        double x;

        for (; ; ) {
            switch (lexer.getCur_token()) {
                case MUL:
                    lexer.advance();
                    result *= pow_expr();
                    break;
                case DIV:
                    lexer.advance();
                    x = pow_expr();
                    if (x == 0) throw new RuntimeException("Не можна ділити на 0!");
                    result /= x;
                    break;
                case MOD:
                    lexer.advance();
                    x = pow_expr();
                    if (x == 0) throw new RuntimeException("Не можна ділити на 0!");
                    result = result % x;
                    break;
                default:
                    return result;
            }
        }
    }

    private double pow_expr() throws Exception {
        double result = primary();

        if (lexer.getCur_token() == Token.POW) {
            lexer.advance();
            double x = primary();
            check_domain(result, x);
            result = Math.pow(result, x);
        }
        return result;
    }

    private double check_domain(double x, double y) {
        if (x >= 0) return 0;

        double e = Math.abs(y);
        if (e <= 0 || e >= 1) return 0;

        throw new RuntimeException("Не можливо пінести від'ємне число до степеня!");
    }

    private double primary() throws Exception {
        String text = lexer.getCur_token_text();
        double arg;
        switch (lexer.getCur_token()) {
            case NUMBER:
                lexer.advance();
                return to_number(text);
            case LP:
                lexer.advance();
                arg = add_expr();
                if (lexer.getCur_token() != Token.RP)
                    throw new RuntimeException("Пропущена права дужка!");
                lexer.advance();
                return arg;
            case SIN:
                return Math.sin(get_argument());
            case COS:
                return Math.cos(get_argument());
            case SQRT:
                arg = get_argument();
                if (arg < 0)
                    throw new RuntimeException("Не можливо взяти корінь від від'ємного числа!");
                return Math.sqrt(arg);
            case INC:
                return (get_argument() + 1.0);
            case DEC:
                return (get_argument() - 1.0);
            case MMAX:
                return get_mmax();
            case MMIN:
                return get_mmin();
            case CELL:
                return getValueFromCell();
            default:
                throw new RuntimeException("Перевірте правильність написання операцій або функцій!");
        }
    }

    private double getValueFromCell() throws Exception {
        String buffer = lexer.getBuffer();
        double value = 0;
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[0].length;j++) {
                if(data[i][j].getName().equals(buffer)) {
                    value = Double.parseDouble((data[i][j].getValue().equals("") || data[i][j].getValue().equals("0.0")) ? "0" : data[i][j].getValue());
                }
            }
        }
        lexer.advance();
        return value;
    }

    private double get_mmax() throws Exception {
        lexer.advance();
        if (lexer.getCur_token() != Token.LP)
            throw new RuntimeException("Пропустили ( після назви функції!");
        lexer.advance();
        ArrayList<Double> helperArray = new ArrayList<>();
        double arg = add_expr();
        helperArray.add(arg);
        while (lexer.getCur_token() == Token.COMA) {
            lexer.advance();
            arg = add_expr();
            helperArray.add(arg);
        }
        if ((lexer.getCur_token() != Token.RP))
            throw new RuntimeException("Пропустили ) після аргументів функції!");
        lexer.advance();
        double maximum;
        return helperArray.stream()
                .mapToDouble(a -> a)
                .max().orElseThrow(NoSuchElementException::new);
    }

    private double get_mmin() throws Exception {
        lexer.advance();
        if (lexer.getCur_token() != Token.LP)
            throw new RuntimeException("Пропустили ( після назви функції!");
        lexer.advance();
        ArrayList<Double> helperArray = new ArrayList<>();
        double arg = add_expr();
        helperArray.add(arg);
        while (lexer.getCur_token() == Token.COMA) {
            lexer.advance();
            arg = add_expr();
            helperArray.add(arg);
        }
        if ((lexer.getCur_token() != Token.RP))
            throw new RuntimeException("Пропустили ) після аргументів функції!");
        lexer.advance();
        double maximum;
        return helperArray.stream()
                .mapToDouble(a -> a)
                .min().orElseThrow(NoSuchElementException::new);
    }


    private double get_argument() throws Exception {
        lexer.advance();    // Toss the function name. We already know it.
        if (lexer.getCur_token() != Token.LP)
            throw new RuntimeException("Пропустили ( після назви функції!");
        lexer.advance();
        double arg = add_expr();
        if (lexer.getCur_token() != Token.RP)
            throw new RuntimeException("Пропустили ) після аргументів функції!");
        lexer.advance();
        return arg;
    }

    public double Calculate(String s, MyCell[][] data, MyCell currentCell) throws Exception {
        this.data = data;
        lexer = new Lexer(s, data, currentCell);
        return add_expr();
    }

    public MyParser() {
    }

    private double to_number(String s) {
        return Double.parseDouble(s);
    }

    private String to_string(Double s) {
        return s.toString();
    }

}


