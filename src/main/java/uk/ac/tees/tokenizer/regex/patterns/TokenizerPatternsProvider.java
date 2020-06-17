package uk.ac.tees.tokenizer.regex.patterns;

import uk.ac.tees.tokenizer.Token;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Abstracts away the details of the source of regular expression patterns. There must be a {@link Pattern} for
 * each of the {@link Token.Type}s, this is verified when creating a new instance of {@link TokenizerPatternsCache}
 * via the {@link #newCache()} method.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public abstract class TokenizerPatternsProvider {

    /**
     * Creates a {@link Map} of {@link Token.Type}s mapped to their corresponding {@link Pattern}.
     *
     * @return a {@link Map} of the available patterns for each token type.
     */
    protected abstract Map<Token.Type, Pattern> getPatterns();

    /**
     * Creates a new {@link TokenizerPatternsCache}. Invokes {@link #getPatterns()} and then verifies that
     * there is a {@link Pattern} for each {@link Token.Type}, throwing a {@link MissingTokenPatternException}
     * if not.
     *
     * @return an instance of {@link TokenizerPatternsCache} containing a {@link Pattern} for each token type.
     */
    public TokenizerPatternsCache newCache() {
        Map<Token.Type, Pattern> patterns = getPatterns();

        for (Token.Type type : Token.Type.values()) {
            if (!patterns.containsKey(type)) {
                throw new MissingTokenPatternException(type);
            }
        }

        return new TokenizerPatternsCache(patterns);
    }

}