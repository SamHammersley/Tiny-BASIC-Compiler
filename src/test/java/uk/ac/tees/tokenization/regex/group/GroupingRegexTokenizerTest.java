package uk.ac.tees.tokenization.regex.group;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.ac.tees.tokenization.Token;
import uk.ac.tees.tokenization.TokenizationException;
import uk.ac.tees.tokenization.UnexpectedCharacterException;
import uk.ac.tees.tokenization.regex.RegexTokenizer;
import uk.ac.tees.tokenization.regex.RegexTokenizerPatternsCache;
import uk.ac.tees.tokenization.regex.provider.FromFileProvider;

import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class GroupingRegexTokenizerTest {

    private static RegexTokenizerPatternsCache cache;

    @BeforeAll
    static void setup() {
        String regexFile = GroupingRegexTokenizerTest.class.getClassLoader().getResource("regex").getFile();

        cache = new FromFileProvider(regexFile).cache();
    }

    @Test
    void testUnexpectedCharacter() {
        RegexTokenizer tokenizer = new GroupingRegexTokenizer(cache);

        UnexpectedCharacterException e = assertThrows(UnexpectedCharacterException.class,
                () -> tokenizer.tokenize("10 LET N = 5\n20 PRINT N\n30 LET ]"));

        assertEquals(8, e.getIndex());
        assertEquals(3, e.getLine());

        assertEquals("Unexpected character on line 8, character 3)", e.getMessage());
    }

    @Test
    void testLetBinding() throws TokenizationException {
        RegexTokenizer tokenizer = new GroupingRegexTokenizer(cache);

        Queue<Token> tokens = tokenizer.tokenize("10 LET N = 5\n20 PRINT N");

        assertEquals(9, tokens.size());

        assertEquals(new Token(Token.Type.NUMBER, "10"), tokens.poll());
        assertEquals(new Token(Token.Type.KEYWORD, "LET"), tokens.poll());
        assertEquals(new Token(Token.Type.IDENTIFIER, "N"), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, "="), tokens.poll());
        assertEquals(new Token(Token.Type.NUMBER, "5"), tokens.poll());
        assertEquals(new Token(Token.Type.NEW_LINE, "\n"), tokens.poll());
        assertEquals(new Token(Token.Type.NUMBER, "20"), tokens.poll());
        assertEquals(new Token(Token.Type.KEYWORD, "PRINT"), tokens.poll());
        assertEquals(new Token(Token.Type.IDENTIFIER, "N"), tokens.poll());
    }

    @Test
    void testPrint() throws TokenizationException {
        RegexTokenizer tokenizer = new GroupingRegexTokenizer(cache);

        Queue<Token> tokens = tokenizer.tokenize("10 PRINT \"Hello, World!\"");

        assertEquals(3, tokens.size());

        assertEquals(new Token(Token.Type.NUMBER, "10"), tokens.poll());
        assertEquals(new Token(Token.Type.KEYWORD, "PRINT"), tokens.poll());
        assertEquals(new Token(Token.Type.STRING_EXPRESSION, "\"Hello, World!\""), tokens.poll());
    }

    @Test
    void testInput() throws TokenizationException {
        RegexTokenizer tokenizer = new GroupingRegexTokenizer(cache);

        Queue<Token> tokens = tokenizer.tokenize("10 INPUT X, Y, Z");

        assertEquals(7, tokens.size());

        assertEquals(new Token(Token.Type.NUMBER, "10"), tokens.poll());
        assertEquals(new Token(Token.Type.KEYWORD, "INPUT"), tokens.poll());
        assertEquals(new Token(Token.Type.IDENTIFIER, "X"), tokens.poll());
        assertEquals(new Token(Token.Type.COMMA, ","), tokens.poll());
        assertEquals(new Token(Token.Type.IDENTIFIER, "Y"), tokens.poll());
        assertEquals(new Token(Token.Type.COMMA, ","), tokens.poll());
        assertEquals(new Token(Token.Type.IDENTIFIER, "Z"), tokens.poll());
    }

    @Test
    void testArithmetic() throws TokenizationException {
        RegexTokenizer tokenizer = new GroupingRegexTokenizer(cache);

        Queue<Token> tokens = tokenizer.tokenize("3 * (6 + 2 - 3) / 5");

        assertEquals(11, tokens.size());

        assertEquals(new Token(Token.Type.NUMBER, "3"), tokens.poll());
        assertEquals(new Token(Token.Type.MULTIPLY, "*"), tokens.poll());
        assertEquals(new Token(Token.Type.L_PARENTHESES, "("), tokens.poll());
        assertEquals(new Token(Token.Type.NUMBER, "6"), tokens.poll());
        assertEquals(new Token(Token.Type.PLUS, "+"), tokens.poll());
        assertEquals(new Token(Token.Type.NUMBER, "2"), tokens.poll());
        assertEquals(new Token(Token.Type.MINUS, "-"), tokens.poll());
        assertEquals(new Token(Token.Type.NUMBER, "3"), tokens.poll());
        assertEquals(new Token(Token.Type.R_PARENTHESES, ")"), tokens.poll());
        assertEquals(new Token(Token.Type.DIV, "/"), tokens.poll());
        assertEquals(new Token(Token.Type.NUMBER, "5"), tokens.poll());
    }

    @Test
    void testRelationalOperators() throws TokenizationException {
        RegexTokenizer tokenizer = new GroupingRegexTokenizer(cache);

        Queue<Token> tokens = tokenizer.tokenize("< <= > >= <> >< =");

        assertEquals(7, tokens.size());

        assertEquals(new Token(Token.Type.REL_OP, "<"), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, "<="), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, ">"), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, ">="), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, "<>"), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, "><"), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, "="), tokens.poll());
    }

}