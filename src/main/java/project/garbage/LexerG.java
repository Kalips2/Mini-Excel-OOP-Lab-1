package project.garbage;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LexerG {
    String p_input;
    boolean canDeleteP; //owns_input

    TokenG cur_token; //token() = getToken()
    String cur_token_text;

    String buffer; //
    public int i = 0;
    public int len;

    private TokenG get_token() throws RuntimeException {
        System.out.println();
        buffer = "";
        System.out.println("i = " + i);
        if (i >= len - 1 || len == 0) return TokenG.END_OF;

        char c = p_input.charAt(i);

        while (i < len && Character.isSpaceChar(c)) c = p_input.charAt(++i);

        if (Character.isLetter(c)) {
            buffer = String.valueOf(c);
            i++;
            while (i < len && Character.isLetterOrDigit(p_input.charAt(i))) {
                c = p_input.charAt(i);
                buffer += c;
                i++;
            }

            buffer.toLowerCase();

            if (buffer == "sin") return TokenG.SIN;
            if (buffer == "cos") return TokenG.COS;
            if (buffer == "sqrt") return TokenG.SQRT;

            return TokenG.ID;

        }
        if (Character.isDigit(c)) {
            buffer += String.valueOf(c);
            i++;

            while (i < len && Character.isDigit(p_input.charAt(i))) {
                c = p_input.charAt(i);
                buffer += c;
                i++;
            }
            return TokenG.NUMBER;
        }
        buffer = String.valueOf(p_input.charAt(i));
        switch (c) {
            case '=':
                i++;
                return TokenG.ASSIGN;
            case '+':
                i++;
                return TokenG.PLUS;
            case '-':
                i++;
                return TokenG.MINUS;
            case '*':
                i++;
                return TokenG.MUL;
            case '/':
                i++;
                return TokenG.DIV;
            case '%':
                i++;
                return TokenG.MOD;
            case '^':
                i++;
                return TokenG.POW;
            case '(':
                i++;
                return TokenG.LP;
            case ')':
                i++;
                return TokenG.RP;
        }
        throw new RuntimeException("Lexical error with token");
    }

    LexerG(String p_input) throws Exception {
        this.p_input = p_input;
        len = p_input.length();
        System.out.println("LENGTH = " + len);
        canDeleteP = false;
        cur_token = get_token();
        cur_token_text = buffer;
    }

    public void advance() throws Exception {
        if (cur_token != TokenG.END_OF) {
            cur_token = get_token();
            cur_token_text = buffer;
            System.out.println("new token on i = " + i + " is " + cur_token.toString() + "    they text is " + getBuffer());
        }
    }
}
