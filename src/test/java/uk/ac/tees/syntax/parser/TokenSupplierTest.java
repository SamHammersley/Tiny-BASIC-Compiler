package uk.ac.tees.syntax.parser;

import org.junit.jupiter.api.Test;
import uk.ac.tees.syntax.parser.exception.ParseException;
import uk.ac.tees.syntax.parser.exception.UnexpectedTokenTypeException;
import uk.ac.tees.syntax.parser.exception.UnexpectedTokenValueException;
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
        supplier.scan();

        assertDoesNotThrow(() -> supplier.predictType(Token.Type.NUMBER));
        assertThrows(UnexpectedTokenTypeException.class, () -> supplier.predictType(Token.Type.STRING_EXPRESSION));
    }

    @Test
    void testExpectValue() throws ParseException {
        TokenSupplier supplier = testSupplier();
        supplier.scan();

        assertDoesNotThrow(() -> supplier.predictValue("10"));
        assertThrows(UnexpectedTokenValueException.class, () -> supplier.predictValue("wrong_value"));
    }

    @Test
    void testNextTokenOfType() throws ParseException {
        TokenSupplier supplier = testSupplier();

        supplier.scan(Token.Type.NUMBER);
        assertEquals(Token.Type.NUMBER, supplier.getType());
    }

    @Test
    void testNextTokenOfValue() throws ParseException {
        TokenSupplier supplier = testSupplier();

        supplier.scan("10");

        int value = supplier.getValue(Integer::parseInt);
        assertEquals(10, value);
        assertEquals(Token.Type.NUMBER, supplier.getType());
    }

    @Test
    void testHasNext() throws ParseException {
        TokenSupplier supplier = testSupplier();

        assertTrue(supplier.hasNext());
        supplier.scan();
        assertFalse(supplier.hasNext());
    }

}