package uk.ac.tees.tokenization.regex.sequential;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class TokenizerCursorTest {

    @Test
    void testNextLine() {
        TokenizerCursor cursor = new TokenizerCursor("");

        cursor.nextLine();

        assertEquals(cursor.row(), 2);
    }

    @Test
    void testAdvance() {
        String input = "10 LET X = 10";
        TokenizerCursor cursor = new TokenizerCursor(input);

        cursor.advance("10");

        assertEquals(3, cursor.column());
        assertEquals(1, cursor.row());
    }

    @Test
    void testAdvanceThrowsException() {
        String input = "10 LET X = 10";
        TokenizerCursor cursor = new TokenizerCursor(input);

        assertThrows(IllegalStateException.class, () -> cursor.advance("Y"));
    }

    @Test
    void testRemaining() {
        String input = "10 LET X = 10";
        TokenizerCursor cursor = new TokenizerCursor(input);

        cursor.advance("10 LET X = 10");

        assertEquals(14, cursor.column());
        assertEquals(1, cursor.row());
        assertFalse(cursor.hasRemaining());
    }

}