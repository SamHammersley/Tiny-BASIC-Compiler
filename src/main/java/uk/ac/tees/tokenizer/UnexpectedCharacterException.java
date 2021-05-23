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
     * The row on which there exists an unexpected character.
     */
    private final int row;

    /**
     * The character column at which the unexpected character is placed.
     */
    private final int column;

    public UnexpectedCharacterException(int row, int column) {
        super(String.format(MESSAGE_FORMAT, row, column));

        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

}