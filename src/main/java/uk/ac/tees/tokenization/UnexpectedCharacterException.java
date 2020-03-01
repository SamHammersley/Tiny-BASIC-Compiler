package uk.ac.tees.tokenization;

/**
 * The {@link Exception} thrown upon detecting an unexpected character in a Tiny BASIC input char sequence.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class UnexpectedCharacterException extends Exception {

    /**
     * The format string for the exception message.
     */
    private static final String MESSAGE_FORMAT = "Unexpected character on line %d, character %d)";

    public UnexpectedCharacterException(int line, int index) {
        super(String.format(MESSAGE_FORMAT, line, index));
    }

}