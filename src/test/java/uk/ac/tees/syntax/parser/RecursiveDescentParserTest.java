package uk.ac.tees.syntax.parser;

import org.junit.jupiter.api.Test;
import uk.ac.tees.syntax.grammar.Line;
import uk.ac.tees.syntax.grammar.Program;
import uk.ac.tees.syntax.grammar.UnassignedIdentifier;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticOperator;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalOperator;
import uk.ac.tees.syntax.grammar.factor.IdentifierFactor;
import uk.ac.tees.syntax.grammar.factor.NumberFactor;
import uk.ac.tees.syntax.grammar.statement.*;
import uk.ac.tees.syntax.parser.exception.ParseException;
import uk.ac.tees.syntax.parser.exception.UnrecognisedCommand;
import uk.ac.tees.tokenizer.Token;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.ac.tees.tokenizer.Token.Type.*;

class RecursiveDescentParserTest {

    private Queue<Token> testProgramTokens() {
        Queue<Token> tokens = new LinkedList<>();

        tokens.add(new Token(NUMBER, "10", 1, 1));
        tokens.add(new Token(KEYWORD, "LET", 1, 4));
        tokens.add(new Token(IDENTIFIER, "B", 1, 8));
        tokens.add(new Token(REL_OP, "=", 1, 10));
        tokens.add(new Token(NUMBER, "6", 1, 12));
        tokens.add(new Token(NEW_LINE, "\n", 1, 13));

        // B * B + (B - 1) * 10
        tokens.add(new Token(NUMBER, "20", 2, 1));
        tokens.add(new Token(KEYWORD, "PRINT", 2, 4));
        tokens.add(new Token(IDENTIFIER, "B", 2, 10));
        tokens.add(new Token(MULTIPLY, "*", 2, 12));
        tokens.add(new Token(IDENTIFIER, "B", 2, 14));
        tokens.add(new Token(PLUS, "+", 2, 16));
        tokens.add(new Token(L_PARENTHESES, "(", 2, 18));
        tokens.add(new Token(IDENTIFIER, "B", 2, 19));
        tokens.add(new Token(MINUS, "-", 2, 21));
        tokens.add(new Token(NUMBER, "1", 2, 22));
        tokens.add(new Token(R_PARENTHESES, ")", 2, 24));
        tokens.add(new Token(MULTIPLY, "*", 2, 26));
        tokens.add(new Token(NUMBER, "10", 2, 28));
        tokens.add(new Token(NEW_LINE, "\n", 1, 29));

        tokens.add(new Token(NUMBER, "30", 3, 1));
        tokens.add(new Token(KEYWORD, "IF", 3, 4));
        tokens.add(new Token(IDENTIFIER, "B", 3, 7));
        tokens.add(new Token(REL_OP, ">", 3, 9));
        tokens.add(new Token(IDENTIFIER, "B", 3, 11));
        tokens.add(new Token(KEYWORD, "THEN", 3, 13));
        tokens.add(new Token(KEYWORD, "END", 3, 18));
        tokens.add(new Token(NEW_LINE, "\n", 3, 19));

        tokens.add(new Token(NUMBER, "40", 4, 1));
        tokens.add(new Token(KEYWORD, "END", 4, 4));

        return tokens;
    }

    private Program manualAbstractSyntaxTree() {
        // B * B (6 * 6)
        ArithmeticBinaryExpression e1Left = new ArithmeticBinaryExpression(
                new IdentifierFactor('B'), new IdentifierFactor('B'), ArithmeticOperator.MUL);

        // B - 1 (6 - 1)
        ArithmeticBinaryExpression e2Left = new ArithmeticBinaryExpression(
                new IdentifierFactor('B'), new NumberFactor(1), ArithmeticOperator.SUB);

        // e2Left * 10 (5 * 10)
        ArithmeticBinaryExpression e1Right = new ArithmeticBinaryExpression(e2Left, new NumberFactor(10), ArithmeticOperator.MUL);

        // e1Left + e1Right (36 + 50)
        ArithmeticBinaryExpression printExpression = new ArithmeticBinaryExpression(e1Left, e1Right, ArithmeticOperator.ADD);

        RelationalBinaryExpression relExp = new RelationalBinaryExpression(new IdentifierFactor('B'),
                new IdentifierFactor('B'), RelationalOperator.GREATER);

        Line line1 = new Line(10, new LetStatement(new UnassignedIdentifier('B'), new NumberFactor(6)));
        Line line2 = new Line(20, new PrintStatement(printExpression));
        Line line3 = new Line(30, new IfStatement(relExp, new EndStatement()));
        Line line4 = new Line(40, new EndStatement());

        List<Line> lines = List.of(line1, line2, line3, line4);

        return new Program("test", lines);
    }

    @Test
    void testParseProgram() throws ParseException {
        // mock this
        TokenSupplier supplier = new TokenSupplier(testProgramTokens());
        RecursiveDescentParser parser = new RecursiveDescentParser(supplier);

        Program expected = manualAbstractSyntaxTree();
        Program actual = parser.parse("test");

        assertEquals(expected, actual);
    }

    @Test
    void testThrowsUnrecognisedCommand() {
        Queue<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.Type.NUMBER, "10", 1, 1));
        tokens.add(new Token(Token.Type.KEYWORD, "UNRECOGNISED", 1, 4));
        tokens.add(new Token(Token.Type.KEYWORD, "COMMAND", 1, 17));

        TokenSupplier supplier = new TokenSupplier(tokens);
        RecursiveDescentParser parser = new RecursiveDescentParser(supplier);

        assertThrows(UnrecognisedCommand.class, () -> parser.parse("throws unrecognised command"));
    }

    @Test
    void testParseLine() throws ParseException {
        TokenSupplier mockSupplier = mock(TokenSupplier.class);

        when(mockSupplier.getType()).thenReturn(NUMBER);
        when(mockSupplier.getValue(any())).thenCallRealMethod().thenCallRealMethod().thenCallRealMethod().thenReturn(10);

        when(mockSupplier.getCurrentToken())
                .thenReturn(new Token(Token.Type.NUMBER, "10", 1, 1))
                .thenReturn(new Token(Token.Type.KEYWORD, "LET", 1, 4))
                .thenReturn(new Token(IDENTIFIER, "X", 1, 8))
                .thenReturn(new Token(REL_OP, "=", 1, 10))
                .thenReturn(new Token(Token.Type.NUMBER, "10", 1, 1));

        RecursiveDescentParser parser = new RecursiveDescentParser(mockSupplier);
        Line actual = parser.parseLine();

        Line expected = new Line(10, new LetStatement(new UnassignedIdentifier('X'), new NumberFactor(10)));

        assertEquals(expected, actual);

        verify(mockSupplier).getType();
        verify(mockSupplier, times(2)).scan(any(Token.Type.class));
        verify(mockSupplier).predictType(IDENTIFIER);
        verify(mockSupplier, times(4)).getValue(any());
    }

    @Test
    void testParseStatement() throws ParseException {
        TokenSupplier mockSupplier = mock(TokenSupplier.class);
        when(mockSupplier.getValue(any())).thenReturn("print").thenReturn(10).thenReturn(ArithmeticOperator.MUL).thenReturn(10);

        when(mockSupplier.getType()).thenReturn(NUMBER).thenReturn(NUMBER).thenReturn(IDENTIFIER); // parsing factors
        when(mockSupplier.getValue()).thenReturn("X"); // parsing identifier factor

        when(mockSupplier.currentTypeIs(same(MULTIPLY), same(DIV))).thenReturn(true).thenReturn(false);
        when(mockSupplier.currentTypeIs(same(COMMA))).thenReturn(true).thenReturn(true).thenReturn(false);

        RecursiveDescentParser parser = new RecursiveDescentParser(mockSupplier);
        Statement actual = parser.parseStatement();

        CompoundPrintStatement expected = new CompoundPrintStatement();
        expected.addExpression(new ArithmeticBinaryExpression(
                new NumberFactor(10),
                new NumberFactor(10),
                ArithmeticOperator.MUL));
        expected.addExpression(new IdentifierFactor('X'));

        assertEquals(expected, actual);

        verify(mockSupplier, times(3)).getType();
        verify(mockSupplier, times(3)).predictType(any());

        verify(mockSupplier, times(1)).scan(any(Token.Type.class));
        verify(mockSupplier, times(5)).scan();
    }

}