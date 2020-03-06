package uk.ac.tees.tokenization.regex.group;

import uk.ac.tees.tokenization.Token;

import java.util.regex.MatchResult;

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
     * The token matched.
     */
    private final Token token;

    TokenMatchResult(int startIndex, int endIndex, Token token) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.token = token;
    }

    /**
     * Accessor method for the start index for this match.
     *
     * @return {@link #startIndex}.
     */
    int start() {
        return startIndex;
    }

    /**
     * Accessor method for the end index for this match.
     *
     * @return {@link #endIndex}.
     */
    int end() {
        return endIndex;
    }

    /**
     * Accessor method for the matched token.
     *
     * @return {@link #token}.
     */
    Token getToken() {
        return token;
    }

    /**
     * Creates a new instance of @{link TokenMatchResult} from the given {@link Token.Type} and {@link MatchResult}.
     *
     * @param type the type of the token matched.
     * @param r the {@link MatchResult}.
     * @return a new {@link TokenMatchResult} containing the start index, end index and the matched token.
     */
    public static TokenMatchResult fromMatchResult(Token.Type type, MatchResult r) {
        return new TokenMatchResult(r.start(), r.end(), new Token(type, r.group()));
    }

    @Override
    public String toString() {
        return "(" + startIndex + ":" + endIndex + "): " + token.toString();
    }

    @Override
    public int compareTo(TokenMatchResult o) {
        return startIndex - o.startIndex;
    }
}