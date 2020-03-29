package uk.ac.tees.tokenization.regex.sequential;

/**
 * Represents a position of a tokenizer, in an input string. The remaining input, substring from the current index,
 * is tracked.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
final class TokenizerCursor {

    /**
     * The current row (line).
     */
    private int row = 1;

    /**
     * The current column.
     */
    private int column = 1;

    /**
     * The remaining portion of the input string, to be tokenized.
     */
    private String remainingInput;

    /**
     * Constructs a new {@link TokenizerCursor} from the given input.
     *
     * @param input the initial input.
     */
    TokenizerCursor(String input) {
        this.remainingInput = input;
    }

    /**
     * Progresses the cursor to the next line. {@link #row} is incremented and {@link #column} is reset to 1.
     */
    void nextLine() {
        row++;
        column = 1;
    }

    /**
     * Gets the current row being pointed to.
     *
     * @return {@link #row}.
     */
    int row() {
        return row;
    }

    /**
     * Gets the current column being pointed to.
     *
     * @return {@link #column}.
     */
    int column() {
        return column;
    }

    /**
     * Moves the cursor the appropriate amount for the given string value.
     *
     * @param value the string value, to move the cursor for.
     */
    void advance(String value) {
        if (!remainingInput.contains(value)) {
            throw new IllegalStateException("The remaining string does not contain " + value);
        }

        remainingInput = remainingInput.substring(value.length());

        column += value.length();
    }

    /**
     * Gets the remaining portion of the input string.
     *
     * @return {@link #remainingInput}.
     */
    String remaining() {
        return remainingInput;
    }

    /**
     * Checks if {@link #remainingInput} is <i>not</i> blank, if it is there is no remaining input.
     *
     * @return {@code true} if there is remaining input, substring of input from the current position.
     */
    boolean hasRemaining() {
        return !remainingInput.isBlank();
    }

}