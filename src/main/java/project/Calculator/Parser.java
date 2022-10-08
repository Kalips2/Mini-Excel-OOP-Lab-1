package project.Calculator;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Parser {

    Lexer lexer;

    private double add_expr() throws Exception {
        double result = mul_expr();
        //Process add_expr_tail
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

        // Process mul_expr_tail.
        for (; ; ) {
            switch (lexer.getCur_token()) {
                case MUL:
                    lexer.advance();
                    result *= pow_expr();
                    break;
                case DIV:
                    lexer.advance();
                    x = pow_expr();
                    if (x == 0) throw new RuntimeException("attempt to divide by zero");
                    result /= x;
                    break;
                case MOD:
                    lexer.advance();
                    x = pow_expr();
                    if (x == 0) throw new RuntimeException("attempt to divide by zero");
                    result = result % x;
                    break;
                default:
                    return result;
            }
        }
    }

    private double pow_expr() throws Exception {
        double result = primary();
        // Process pow_expr_suffix.
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

        throw new RuntimeException("attempt to take root of a negative number");
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
                    throw new RuntimeException("missing ) after subexpression");
                lexer.advance();
                return arg;
            case SIN:
                return Math.sin(get_argument());
            case COS:
                return Math.cos(get_argument());
            case SQRT:
                arg = get_argument();
                if (arg < 0)
                    throw new RuntimeException("attempt to take square root of negative number");
                return Math.sqrt(arg);
            case INC:
                return (get_argument() + 1);
            case DEC:
                return (get_argument() - 1);
            case MMAX:
                return get_mmax();
            case MMIN:
                return get_mmin();
            default:
                throw new RuntimeException("invalid primary expression");
        }
    }
    private double get_mmax() throws Exception {
        lexer.advance();
        if (lexer.getCur_token() != Token.LP)
            throw new RuntimeException("missing ( after function name");
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
            throw new RuntimeException("missing ) after function argument");
        lexer.advance();
        double maximum;
        return helperArray.stream()
                .mapToDouble(a -> a)
                .max().orElseThrow(NoSuchElementException::new);
    }

    private double get_mmin() throws Exception {
        lexer.advance();
        if (lexer.getCur_token() != Token.LP)
            throw new RuntimeException("missing ( after function name");
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
            throw new RuntimeException("missing ) after function argument");
        lexer.advance();
        double maximum;
        return helperArray.stream()
                .mapToDouble(a -> a)
                .min().orElseThrow(NoSuchElementException::new);
    }


    private double get_argument() throws Exception {
        lexer.advance();    // Toss the function name. We already know it.
        if (lexer.getCur_token() != Token.LP)
            throw new RuntimeException("missing ( after function name");
        lexer.advance();
        double arg = add_expr();
        if (lexer.getCur_token() != Token.RP)
            throw new RuntimeException("missing ) after function argument");
        lexer.advance();
        return arg;
    }

    public double Calculate(String s) throws Exception {
        lexer = new Lexer(s);
        return add_expr();
    }

    Parser() {
    }

    private double to_number(String s) {
        System.out.println("IN TO NUMBER: " + (Double.parseDouble(s)));
        return Double.parseDouble(s);
    }

    private String to_string(Double s) {
        return s.toString();
    }

    public static void main(String[] args) throws IOException {
        String s;
        Parser parser = new Parser();
        System.out.print("Enter a expression: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        s = reader.readLine();
        try {
            System.out.println(parser.Calculate(s));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}


