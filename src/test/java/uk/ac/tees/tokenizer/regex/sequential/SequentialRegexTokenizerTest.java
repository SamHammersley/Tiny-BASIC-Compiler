package uk.ac.tees.tokenizer.regex.sequential;

import org.junit.jupiter.api.Test;
import uk.ac.tees.tokenizer.Token;
import uk.ac.tees.tokenizer.TokenizationException;
import uk.ac.tees.tokenizer.UnexpectedCharacterException;
import uk.ac.tees.tokenizer.regex.RegexTokenizer;
import uk.ac.tees.tokenizer.regex.patterns.TokenizerPatternsCache;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static uk.ac.tees.tokenizer.Token.Type.*;

final class SequentialRegexTokenizerTest {

    private TokenizerPatternsCache supporting(Token.Type... types) {
        TokenizerPatternsCache mock = mock(TokenizerPatternsCache.class);

        List<Token.Type> l = Arrays.asList(types);
        return when(mock.supportedTypes()).thenReturn(new LinkedHashSet<>(l)).getMock();
    }

    @Test
    void testUnexpectedCharacter() {
        TokenizerPatternsCache mock = supporting(NUMBER, KEYWORD, IDENTIFIER, REL_OP, NEW_LINE);

        when(mock.getPattern(NUMBER)).thenReturn(Pattern.compile("\\d+"));
        when(mock.getPattern(KEYWORD)).thenReturn(Pattern.compile("[a-zA-Z]{2,}"));
        when(mock.getPattern(IDENTIFIER)).thenReturn(Pattern.compile("\\b[a-zA-Z]\\b"));
        when(mock.getPattern(REL_OP)).thenReturn(Pattern.compile("(<[=>]?|>[=<]?|=)"));
        when(mock.getPattern(NEW_LINE)).thenReturn(Pattern.compile("\\n"));

        RegexTokenizer tokenizer = new SequentialRegexTokenizer(mock);

        UnexpectedCharacterException e = assertThrows(UnexpectedCharacterException.class,
                () -> tokenizer.tokenize("10 LET N = 5\n20 PRINT N\n30 LET ]"));

        assertEquals(8, e.getIndex());
        assertEquals(3, e.getLine());

        assertEquals("Unexpected character on line 3, character 8)", e.getMessage());

        verify(mock, times(13)).supportedTypes();
        // called every token
        verify(mock, times(13)).getPattern(NUMBER);
        // called every token that is not a number
        verify(mock, times(9)).getPattern(KEYWORD);
        // called every token that is neither a number or keyword
        verify(mock, times(6)).getPattern(IDENTIFIER);
        // called every token that is not a number, keyword or identifier
        verify(mock, times(4)).getPattern(REL_OP);
        // called every token that is not a number, keyword, identifier or a new line
        verify(mock, times(3)).getPattern(NEW_LINE);
    }

    @Test
    void testLetBinding() throws TokenizationException {
        TokenizerPatternsCache mock = supporting(NUMBER, KEYWORD, IDENTIFIER, REL_OP, NEW_LINE);

        when(mock.getPattern(NUMBER)).thenReturn(Pattern.compile("\\d+"));
        when(mock.getPattern(KEYWORD)).thenReturn(Pattern.compile("[a-zA-Z]{2,}"));
        when(mock.getPattern(IDENTIFIER)).thenReturn(Pattern.compile("\\b[a-zA-Z]\\b"));
        when(mock.getPattern(REL_OP)).thenReturn(Pattern.compile("(<[=>]?|>[=<]?|=)"));
        when(mock.getPattern(NEW_LINE)).thenReturn(Pattern.compile("\\n"));

        RegexTokenizer tokenizer = new SequentialRegexTokenizer(mock);

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

        verify(mock, times(9)).supportedTypes();
        verify(mock, times(9)).getPattern(NUMBER);
        verify(mock, times(6)).getPattern(KEYWORD);
        verify(mock, times(4)).getPattern(IDENTIFIER);
        verify(mock, times(2)).getPattern(REL_OP);
        verify(mock, times(1)).getPattern(NEW_LINE);
    }

    @Test
    void testPrint() throws TokenizationException {
        TokenizerPatternsCache mock = supporting(STRING_EXPRESSION, NUMBER, KEYWORD);

        when(mock.getPattern(STRING_EXPRESSION)).thenReturn(Pattern.compile("\"[^\"]*\""));
        when(mock.getPattern(NUMBER)).thenReturn(Pattern.compile("\\d+"));
        when(mock.getPattern(KEYWORD)).thenReturn(Pattern.compile("[a-zA-Z]{2,}"));

        RegexTokenizer tokenizer = new SequentialRegexTokenizer(mock);

        Queue<Token> tokens = tokenizer.tokenize("10 PRINT \"Hello, World!\"");

        assertEquals(3, tokens.size());

        assertEquals(new Token(Token.Type.NUMBER, "10", 1, 1), tokens.poll());
        assertEquals(new Token(Token.Type.KEYWORD, "PRINT", 1, 4), tokens.poll());
        assertEquals(new Token(Token.Type.STRING_EXPRESSION, "\"Hello, World!\"", 1, 10), tokens.poll());

        verify(mock, times(3)).supportedTypes();
        verify(mock, times(3)).getPattern(STRING_EXPRESSION);
        verify(mock, times(2)).getPattern(NUMBER);
        verify(mock, times(1)).getPattern(KEYWORD);
    }

    @Test
    void testInput() throws TokenizationException {
        TokenizerPatternsCache mock = supporting(NUMBER, KEYWORD, IDENTIFIER, COMMA);

        when(mock.getPattern(NUMBER)).thenReturn(Pattern.compile("\\d+"));
        when(mock.getPattern(KEYWORD)).thenReturn(Pattern.compile("[a-zA-Z]{2,}"));
        when(mock.getPattern(IDENTIFIER)).thenReturn(Pattern.compile("\\b[a-zA-Z]\\b"));
        when(mock.getPattern(COMMA)).thenReturn(Pattern.compile(","));

        RegexTokenizer tokenizer = new SequentialRegexTokenizer(mock);

        Queue<Token> tokens = tokenizer.tokenize("10 INPUT X, Y, Z");

        assertEquals(7, tokens.size());

        assertEquals(new Token(Token.Type.NUMBER, "10", 1, 1), tokens.poll());
        assertEquals(new Token(Token.Type.KEYWORD, "INPUT", 1, 4), tokens.poll());
        assertEquals(new Token(Token.Type.IDENTIFIER, "X", 1, 10), tokens.poll());
        assertEquals(new Token(Token.Type.COMMA, ",", 1, 11), tokens.poll());
        assertEquals(new Token(Token.Type.IDENTIFIER, "Y", 1, 13), tokens.poll());
        assertEquals(new Token(Token.Type.COMMA, ",", 1, 14), tokens.poll());
        assertEquals(new Token(Token.Type.IDENTIFIER, "Z", 1, 16), tokens.poll());

        verify(mock, times(7)).supportedTypes();
        verify(mock, times(7)).getPattern(NUMBER);
        verify(mock, times(6)).getPattern(KEYWORD);
        verify(mock, times(5)).getPattern(IDENTIFIER);
        verify(mock, times(2)).getPattern(COMMA);
    }

    @Test
    void testArithmetic() throws TokenizationException {
        TokenizerPatternsCache mock = supporting(NUMBER, MULTIPLY, DIV, L_PARENTHESES, R_PARENTHESES, PLUS, MINUS);

        when(mock.getPattern(NUMBER)).thenReturn(Pattern.compile("\\d+"));
        when(mock.getPattern(MULTIPLY)).thenReturn(Pattern.compile("\\*"));
        when(mock.getPattern(DIV)).thenReturn(Pattern.compile("/"));
        when(mock.getPattern(L_PARENTHESES)).thenReturn(Pattern.compile("\\("));
        when(mock.getPattern(R_PARENTHESES)).thenReturn(Pattern.compile("\\)"));
        when(mock.getPattern(PLUS)).thenReturn(Pattern.compile("\\+"));
        when(mock.getPattern(MINUS)).thenReturn(Pattern.compile("-"));

        RegexTokenizer tokenizer = new SequentialRegexTokenizer(mock);

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

        verify(mock, times(11)).supportedTypes();
        verify(mock, times(11)).getPattern(NUMBER);
        verify(mock, times(6)).getPattern(MULTIPLY);
        verify(mock, times(5)).getPattern(DIV);
        verify(mock, times(4)).getPattern(L_PARENTHESES);
        verify(mock, times(3)).getPattern(R_PARENTHESES);
        verify(mock, times(2)).getPattern(PLUS);
        verify(mock, times(1)).getPattern(MINUS);
    }

    @Test
    void testRelationalOperators() throws TokenizationException {
        TokenizerPatternsCache mock = supporting(REL_OP);

        when(mock.getPattern(REL_OP)).thenReturn(Pattern.compile("(<[=>]?|>[=<]?|=)"));

        RegexTokenizer tokenizer = new SequentialRegexTokenizer(mock);

        Queue<Token> tokens = tokenizer.tokenize("< <= > >= <> >< =");

        assertEquals(7, tokens.size());

        assertEquals(new Token(Token.Type.REL_OP, "<", 1, 1), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, "<=", 1, 3), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, ">", 1, 6), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, ">=", 1, 8), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, "<>", 1, 11), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, "><", 1, 14), tokens.poll());
        assertEquals(new Token(Token.Type.REL_OP, "=", 1, 17), tokens.poll());

        verify(mock, times(7)).supportedTypes();
        verify(mock, times(7)).getPattern(REL_OP);
    }

}