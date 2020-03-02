package uk.ac.tees.tokenization.regex;

import uk.ac.tees.tokenization.Token;
import uk.ac.tees.tokenization.TinyBasicTokenizer;
import uk.ac.tees.tokenization.UnexpectedCharacterException;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;

/**
 * A Tiny BASIC tokenizer based on Regular expressions. There are regular expressions for each valid token.
 */
public abstract class RegexTokenizer implements TinyBasicTokenizer {

    /**
     * The patterns by which tokens are matched.
     */
    protected final Map<Pattern, Token.Type> patterns;

    /**
     * Construct a new {@link RegexTokenizer}.
     *
     * @param patterns the regex rules.
     */
    protected RegexTokenizer(Map<Pattern, Token.Type> patterns) {
        this.patterns = patterns;
    }

    /**
     * Produces a {@link Queue} of {@link Token}s via tokenization of the specified input string.
     *
     * @param input the source code to tokenize.
     * @return a {@link Queue} of {@link Token}s.
     * @throws UnexpectedCharacterException if an unexpected character was found.
     */
    public abstract Queue<Token> tokenize(String input) throws UnexpectedCharacterException;

}