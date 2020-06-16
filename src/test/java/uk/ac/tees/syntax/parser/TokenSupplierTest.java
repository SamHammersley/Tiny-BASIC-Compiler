package uk.ac.tees.syntax.parser;

import org.junit.jupiter.api.Test;
import uk.ac.tees.syntax.parser.exception.ParseException;
import uk.ac.tees.syntax.parser.exception.UnexpectedTokenException;
import uk.ac.tees.tokenizer.Token;

import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

final class TokenSupplierTest {

    private TokenSupplier testSupplier() {
        Queue<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.Type.NUMBER, "10", 1, 1));

        return new TokenSupplier(tokens);
    }

    @Test
    void testExpectType() throws ParseException {
        TokenSupplier supplier = testSupplier();
        supplier.nextToken();

        assertDoesNotThrow(() -> supplier.expectType(Token.Type.NUMBER));
        assertThrows(UnexpectedTokenException.class, ()-> supplier.expectType(Token.Type.STRING_EXPRESSION));
    }

    @Test
    void testExpectValue() throws ParseException {
        TokenSupplier supplier = testSupplier();
        supplier.nextToken();

        assertDoesNotThrow(() -> supplier.expectValue(s -> Integer.parseInt(s) == 10));
        assertThrows(UnexpectedTokenException.class, ()-> supplier.expectType(Token.Type.STRING_EXPRESSION));
    }

    @Test
    void testNextTokenOfType() throws ParseException {
        TokenSupplier supplier = testSupplier();

        supplier.nextToken(Token.Type.NUMBER);
        assertEquals(Token.Type.NUMBER, supplier.getType());
    }

    @Test
    void testNextTokenOfValue() throws ParseException {
        TokenSupplier supplier = testSupplier();

        supplier.nextToken(s -> Integer.parseInt(s) == 10);

        int value = supplier.getValue(Integer::parseInt);
        assertEquals(10, value);
        assertEquals(Token.Type.NUMBER, supplier.getType());
    }

}