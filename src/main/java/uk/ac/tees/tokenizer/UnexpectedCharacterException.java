package uk.ac.tees.tokenizer;

/**
 * The {@link Exception} thrown upon detecting an unexpected character in a Tiny BASIC input char sequence.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class UnexpectedCharacterException extends TokenizationException {

    /**
     * The format string for the exception message.
     */
    private static final String MESSAGE_FORMAT = "Unexpected character on line %d, character %d)";

    /**
     * The line on which there exists an unexpected character.
     */
    private final int line;

    /**
     * The character index at which the unexpected character is placed.
     */
    private final int index;

    public UnexpectedCharacterException(int line, int index) {
        super(String.format(MESSAGE_FORMAT, line, index));

        this.line = line;
        this.index = index;
    }

    public int getLine() {
        return line;
    }

    public int getIndex() {
        return index;
    }

}