package uk.ac.tees.syntax.parser.exception;

import uk.ac.tees.tokenizer.Token;

/**
 * Thrown when a {@link Token} of a different type was expected.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class UnexpectedTokenException extends ParseException {

    private static final String MESSAGE_TEMPLATE = "Unexpected token on line %d, column %d: %s";

    public UnexpectedTokenException(Token token) {
        super(String.format(MESSAGE_TEMPLATE, token.getRow(), token.getColumn(), token.toString()));
    }

}