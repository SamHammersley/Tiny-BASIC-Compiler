package uk.ac.tees.tokenizer.regex.patterns;

import uk.ac.tees.tokenizer.Token;

import java.util.regex.Pattern;

/**
 * Exception that's thrown when there is no {@link Pattern} for one of the {@link Token.Type}s in
 * a {@link TokenizerPatternsCache}.
 */
final class MissingTokenPatternException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "Missing pattern for token type %s";

    /**
     * Constructs a new {@link MissingTokenPatternException} for the given missing token type.
     *
     * @param missingType the {@link Token.Type} that is missing a pattern.
     */
    MissingTokenPatternException(Token.Type missingType) {
        super(String.format(MESSAGE_FORMAT, missingType.toString()));
    }

}