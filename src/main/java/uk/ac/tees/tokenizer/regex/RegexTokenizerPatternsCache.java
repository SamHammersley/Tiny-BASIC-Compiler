package uk.ac.tees.tokenizer.regex;

import uk.ac.tees.tokenizer.Token;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class encapsulates the associations between Token types and regular expression patterns. Supported
 * {@link Token.Type}s are stored with their associated {@link Pattern}s as key-value pairs.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class RegexTokenizerPatternsCache {

    /**
     * {@link Token.Type}s mapped to their associated regex {@link Pattern}s.
     */
    private final Map<Token.Type, Pattern> map;

    public RegexTokenizerPatternsCache(Map<Token.Type, Pattern> map) {
        this.map = map;
    }

    /**
     * A {@link Collection} of supported {@link Token.Type}s provided.
     *
     * @return the supported token types.
     */
    public Collection<Token.Type> supportedTypes() {
        return map.keySet();
    }

    /**
     * Gets the associated {@link Pattern} for the given {@link Token.Type}.
     *
     * @param type the {@link Token.Type} to get the pattern for.
     * @return the associated {@link Pattern} to the given {@link Token.Type}.
     */
    public Pattern getPattern(Token.Type type) {
        return map.get(type);
    }

}