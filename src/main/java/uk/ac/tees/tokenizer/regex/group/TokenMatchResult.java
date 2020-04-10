package uk.ac.tees.tokenizer.regex.group;

import uk.ac.tees.tokenizer.Token;

/**
 * Represents a token matched with regex pattern in source code.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class TokenMatchResult implements Comparable<TokenMatchResult> {

    /**
     * The character index, in the input source string, at which {@link #token} starts.
     */
    private final int startIndex;

    /**
     * The character index, in the input source string, at which {@link #token} ends.
     */
    private final int endIndex;

    /**
     * The matched token.
     */
    private final Token token;

    TokenMatchResult(int startIndex, int endIndex, Token token) {
        this.startIndex = startIndex;
        this.token = token;
        this.endIndex = endIndex;
    }

    /**
     * Accessor method for the matched token.
     *
     * @return {@link #token}.
     */
    Token getToken() {
        return token;
    }

    @Override
    public String toString() {
        return token.toString() + " [" + startIndex + "," + endIndex + "]";
    }

    @Override
    public int compareTo(TokenMatchResult o) {
        return startIndex - o.startIndex;
    }
}