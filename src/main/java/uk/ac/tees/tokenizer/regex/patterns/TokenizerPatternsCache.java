package uk.ac.tees.tokenizer.regex.patterns;

import uk.ac.tees.tokenizer.Token;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Represents a temporary dictionary of {@link Token.Type}s and corresponding {@link Pattern}s.
 *
 * This class encapsulates the associations between Token types and regular expression patterns. Supported
 * {@link Token.Type}s are associated with {@link Pattern}s as key-value pairs.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class TokenizerPatternsCache {

    /**
     * {@link Token.Type}s mapped to their associated regex {@link Pattern}s.
     */
    private final Map<Token.Type, Pattern> map;

    TokenizerPatternsCache(Map<Token.Type, Pattern> map) {
        this.map = map;
    }

    /**
     * A {@link Collection} of supported {@link Token.Type}s provided.
     *
     * @return the supported token types.
     */
    public Set<Token.Type> supportedTypes() {
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