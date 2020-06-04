package uk.ac.tees.syntax.parser.exception;

import uk.ac.tees.tokenizer.Token;

/**
 * Thrown when there is unrecognised command in the token sequence. This error message is documented in the Tiny BASIC
 * manual.
 *
 * @see <a href="http://tinybasic.cyningstan.org.uk/page/12/tiny-basic-manual">Tiny BASIC Manual</a>
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class UnrecognisedCommand extends ParseException {

    private static final String MESSAGE_TEMPLATE = "Unrecognised command on line %d, column %d: %s";

    public UnrecognisedCommand(Token token) {
        super(String.format(MESSAGE_TEMPLATE, token.getRow(), token.getColumn(), token.toString()));
    }
}