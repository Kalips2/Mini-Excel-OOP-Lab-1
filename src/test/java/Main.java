import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import project.Calculator.MyParser;

import static org.junit.Assert.assertEquals;

public class Main {

    MyParser parser;

    @Before
    public void createNewParser() {
        parser = new MyParser();
    }

    @Test
    public void IntegerCurrectCalculateExpressionWithoutCells() throws Exception {
        String expression = "6 * (7 - dec(6)) -inc( 8 * 7 - 8 / 2 + 2^10)";
        assertEquals(parser.Calculate(expression, null, null), -1065, 0);
    }

    @Test
    public void DoubleCurrectCalculateExpressionWithoutCells() throws Exception {
        String expression = "mmax(3.2^2 / (0.5 - mmin(6.1, 4.6, 0.2)), inc(inc(inc( 8.9 * 1.2 - 63.2 / 2 / 2 + 5.6))), 34.2)";
        assertEquals(parser.Calculate(expression, null, null), 34.2, 0);
    }

    @Test
    public void IntegerAndDoubleCurrectCalculateExpressionWithoutCells() throws Exception {
        String expression = "mmin(6 * 9 + 4 * 0 + 7 * 2.5 - 4.2, 1.5 ^   9, dec(dec(4.2 -2)))";
        assertEquals(parser.Calculate(expression, null, null), 0.2, 0.0000000001);
    }

    @Test
    public void ThrowErrorWhenTryingDivideByZeroFirstCurrectCalculateExpressionWithoutCells() throws Exception {
        String expression = "mmin(5, 6.8, 6.8 / 0)";
        try {
            parser.Calculate(expression, null, null);
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Не можна ділити на 0!");
        }
    }

    @Test
    public void  ThrowErrorWhenTryingDivideByZeroSecondCurrectCalculateExpressionWithoutCells() throws Exception {
        String expression = "dec(8 * inc(9 % 6)) ";
        try {
            parser.Calculate(expression, null, null);
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Не можна ділити на 0!");
        }
    }

    @Test
    public void ForgiveBracketBeforeFunctionCurrectCalculateExpressionWithoutCells() throws Exception {
        String expression = "dec(8 * inc 9 % 6)) ";
        try {
            parser.Calculate(expression, null, null);
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Пропустили ( після назви функції!");
        }
    }

    @Test
    public void  ForgiveBracketAfterFunctionCurrectCalculateExpressionWithoutCells() throws Exception {
        String expression = "dec(8 * inc (9 % 6) ";
        try {
            parser.Calculate(expression, null, null);
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Пропустили ) після аргументів функції!");
        }
    }

    @After
    public void deleteThisParser() {
        parser = null;
    }
}
