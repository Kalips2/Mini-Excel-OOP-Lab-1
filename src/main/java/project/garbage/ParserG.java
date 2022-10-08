package project.garbage;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParserG {

    LexerG lexer;
    Map<String, Double> symbol_table = new HashMap<>(Map.of(
            "pi", 4.0*Math.atan(1.0),
            "e", Math.exp(1.0) + 0.0));

    private double assign_expr() throws Exception {
        TokenG token = lexer.getCur_token();
        String token_text = lexer.getCur_token_text();
        double result = add_expr();
        //Process assign_expr_suffix
        if(lexer.getCur_token() == TokenG.ASSIGN) {
            //The target of our assignment must be an id
            if( token != TokenG.ID)
                throw new RuntimeException("The target of our assignment must be an id");
            if (token_text.equals("pi") || token_text.equals("e"))
                throw new RuntimeException("attempt to modify the constant " + token_text);
            lexer.advance();
            return add_expr();
        }

        return result;
    }
    private double add_expr() throws Exception {
        double result = mul_expr();
        //Process add_expr_tail
        for(;;) {
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
        for (;;) {
            switch (lexer.getCur_token()) {
                case MUL:
                    lexer.advance();
                    result *= pow_expr();
                    break;
                case DIV:
                    lexer.advance();
                    x = pow_expr();
                    if(x == 0) throw new RuntimeException("attempt to divide by zero");
                    result /= x;
                    break;
                case MOD:
                    lexer.advance();
                    x = pow_expr();
                    if(x == 0) throw new RuntimeException("attempt to divide by zero");
                    result = result % x;
                    break;
                default:
                    return result;
            }
        }
    }
    private double pow_expr() throws Exception {
        double result = unary_expr();
        // Process pow_expr_suffix.
        if (lexer.getCur_token() == TokenG.POW) {
            lexer.advance();
            double x = unary_expr();
            check_domain(result, x);
            result = Math.pow(result, x);
        }
        return result;
    }

    private double check_domain(double x, double y) {
        if( x>= 0 ) return 0 ;

        double e = Math.abs(y);
        if(e <= 0 || e >= 1) return 0;

        throw new RuntimeException("attempt to take root of a negative number");
    }

    private double unary_expr() throws Exception {
        //System.out.println("IN UNARNY");
        switch(lexer.getCur_token()) {
            case PLUS:
                lexer.advance();
                return +primary();
            case MINUS:
                lexer.advance();
                return -primary();
            default:
                return primary();
        }
    }
    private double primary() throws Exception {
        String text = lexer.getCur_token_text();

        double arg;

        switch (lexer.getCur_token()) {
            case ID:
                lexer.advance();
                return symbol_table.get(text);
            case NUMBER:
                lexer.advance();
                return to_number(text);
            case LP:
                lexer.advance();
                arg = add_expr();
                if (lexer.getCur_token() != TokenG.RP)
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
            case INT:
                arg = get_argument();
                if (arg < 0)
                    return Math.ceil(arg);
                else
                    return Math.floor(arg);
            default:
                throw new RuntimeException("invalid primary expression");
        }
    }

    // Change the return type to double when we add semantics.
    private double get_argument() throws Exception {
        lexer.advance();	// Toss the function name. We already know it.
        if (lexer.getCur_token() != TokenG.LP)
            throw new RuntimeException("missing ( after function name");
        lexer.advance();
        double arg = add_expr();
        if (lexer.getCur_token() != TokenG.RP)
            throw new RuntimeException("missing ) after function argument");
        lexer.advance();
        return arg;
    }

    public double Calculate(String s) throws Exception {
        lexer = new LexerG(s);
        return assign_expr();
    }
    ParserG() {
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
        ParserG parser = new ParserG();
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


