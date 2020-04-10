package uk.ac.tees.tokenizer.regex;

import uk.ac.tees.tokenizer.Token;
import uk.ac.tees.tokenizer.TinyBasicTokenizer;
import uk.ac.tees.tokenizer.TokenizationException;

import java.util.Queue;
import java.util.regex.Pattern;

/**
 * A Tiny BASIC tokenizer based on Regular expressions. There are regular expressions for each valid token.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public abstract class RegexTokenizer implements TinyBasicTokenizer {

    /**
     * Provides regular expression {@link Pattern}s and their corresponding {@link Token.Type}s.
     */
    protected final RegexTokenizerPatternsCache patterns;

    /**
     * Construct a new {@link RegexTokenizer}.
     *
     * @param patterns provides the regex patterns and token types.
     */
    protected RegexTokenizer(RegexTokenizerPatternsCache patterns) {
        this.patterns = patterns;
    }

    /**
     * Produces a {@link Queue} of {@link Token}s via tokenizer of the specified input string.
     *
     * @param input the source code to tokenize.
     * @return a {@link Queue} of {@link Token}s.
     * @throws TokenizationException if any exceptional behaviour exhibited.
     */
    public abstract Queue<Token> tokenize(String input) throws TokenizationException;

}