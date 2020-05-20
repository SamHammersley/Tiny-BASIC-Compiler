package uk.ac.tees.tokenizer.regex.sequential;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.ac.tees.tokenizer.Token;
import uk.ac.tees.tokenizer.TokenizationException;
import uk.ac.tees.tokenizer.UnexpectedCharacterException;
import uk.ac.tees.tokenizer.regex.RegexTokenizer;
import uk.ac.tees.tokenizer.regex.RegexTokenizerPatternsCache;
import uk.ac.tees.tokenizer.regex.provider.FromFileProvider;

import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class SequentialRegexTokenizerTest {

    private static RegexTokenizerPatternsCache cache;

    @BeforeAll
    static void setup() {
        String regexFile = SequentialRegexTokenizerTest.class.getClassLoader().getResource("regex").getFile();

        cache = new FromFileProvider(regexFile).cache();
    }

    @Test
    void testUnexpectedCharacter() {
        RegexTokenizer tokenizer = new SequentialRegexTokenizer(cache);

        UnexpectedCharacterException e = assertThrows(UnexpectedCharacterException.class,
                () -> tokenizer.tokenize("10 LET N = 5\n20 PRINT N\n30 LET ]"));

        assertEquals(8, e.getIndex());
        assertEquals(3, e.getLine());

        assertEquals("Unexpected character on line 8, character 3)", e.getMessage());
    }

    @Test
    void testLetBinding() throws TokenizationException {
        RegexTokenizer tokenizer = new SequentialRegexTokenizer(cache);

        Queue<Token> tokens = tokenizer.tokenize("10 LET N = 5\n20 PRINT N");

        assertEquals(9, tokens.size());

        assertEquals(new Token(Token.Type.NUMBER, "10", 1, 1), tokens.poll());
        assertEquals(new Token(Token.Type.KEYWORD, "LET", 1, 4), tokens.poll());
        assertEquals(new Token(Token.Type.IDENTIFIER, "N", 1, 8), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, "=", 1, 10), tokens.poll());
        assertEquals(new Token(Token.Type.NUMBER, "5", 1, 12), tokens.poll());
        assertEquals(new Token(Token.Type.NEW_LINE, "\n", 1, 11), tokens.poll());
        assertEquals(new Token(Token.Type.NUMBER, "20", 2, 1), tokens.poll());
        assertEquals(new Token(Token.Type.KEYWORD, "PRINT", 2, 4), tokens.poll());
        assertEquals(new Token(Token.Type.IDENTIFIER, "N", 2, 10), tokens.poll());
    }

    @Test
    void testPrint() throws TokenizationException {
        RegexTokenizer tokenizer = new SequentialRegexTokenizer(cache);

        Queue<Token> tokens = tokenizer.tokenize("10 PRINT \"Hello, World!\"");

        assertEquals(3, tokens.size());

        assertEquals(new Token(Token.Type.NUMBER, "10", 1, 1), tokens.poll());
        assertEquals(new Token(Token.Type.KEYWORD, "PRINT", 1, 4), tokens.poll());
        assertEquals(new Token(Token.Type.STRING_EXPRESSION, "\"Hello, World!\"", 1, 10), tokens.poll());
    }

    @Test
    void testInput() throws TokenizationException {
        RegexTokenizer tokenizer = new SequentialRegexTokenizer(cache);

        Queue<Token> tokens = tokenizer.tokenize("10 INPUT X, Y, Z");

        assertEquals(7, tokens.size());

        assertEquals(new Token(Token.Type.NUMBER, "10", 1, 1), tokens.poll());
        assertEquals(new Token(Token.Type.KEYWORD, "INPUT", 1, 4), tokens.poll());
        assertEquals(new Token(Token.Type.IDENTIFIER, "X", 1, 10), tokens.poll());
        assertEquals(new Token(Token.Type.COMMA, ",", 1, 11), tokens.poll());
        assertEquals(new Token(Token.Type.IDENTIFIER, "Y", 1, 13), tokens.poll());
        assertEquals(new Token(Token.Type.COMMA, ",", 1, 14), tokens.poll());
        assertEquals(new Token(Token.Type.IDENTIFIER, "Z", 1, 16), tokens.poll());
    }

    @Test
    void testArithmetic() throws TokenizationException {
        RegexTokenizer tokenizer = new SequentialRegexTokenizer(cache);

        Queue<Token> tokens = tokenizer.tokenize("3 * (6 + 2 - 3) / 5");

        assertEquals(11, tokens.size());

        assertEquals(new Token(Token.Type.NUMBER, "3", 1, 1), tokens.poll());
        assertEquals(new Token(Token.Type.MULTIPLY, "*", 1, 3), tokens.poll());
        assertEquals(new Token(Token.Type.L_PARENTHESES, "(", 1, 5), tokens.poll());
        assertEquals(new Token(Token.Type.NUMBER, "6", 1, 6), tokens.poll());
        assertEquals(new Token(Token.Type.PLUS, "+", 1, 8), tokens.poll());
        assertEquals(new Token(Token.Type.NUMBER, "2", 1, 10), tokens.poll());
        assertEquals(new Token(Token.Type.MINUS, "-", 1, 12), tokens.poll());
        assertEquals(new Token(Token.Type.NUMBER, "3", 1, 14), tokens.poll());
        assertEquals(new Token(Token.Type.R_PARENTHESES, ")", 1, 15), tokens.poll());
        assertEquals(new Token(Token.Type.DIV, "/", 1, 17), tokens.poll());
        assertEquals(new Token(Token.Type.NUMBER, "5", 1, 19), tokens.poll());
    }

    @Test
    void testRelationalOperators() throws TokenizationException {
        RegexTokenizer tokenizer = new SequentialRegexTokenizer(cache);

        Queue<Token> tokens = tokenizer.tokenize("< <= > >= <> >< =");

        assertEquals(7, tokens.size());

        assertEquals(new Token(Token.Type.REL_OP, "<", 1, 1), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, "<=", 1, 3), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, ">", 1, 6), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, ">=", 1, 8), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, "<>", 1, 11), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, "><", 1, 14), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, "=", 1, 17), tokens.poll());
    }

}