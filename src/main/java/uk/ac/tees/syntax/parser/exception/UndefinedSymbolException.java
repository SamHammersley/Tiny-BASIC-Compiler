package uk.ac.tees.syntax.parser.exception;

import uk.ac.tees.tokenizer.Token;

/**
 * Thrown when there is unrecognised input in the token sequence.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class UndefinedSymbolException extends ParseException {

    private static final String MESSAGE_TEMPLATE = "Undefined symbol on line %d, column %d: %s";

    public UndefinedSymbolException(Token token) {
        super(String.format(MESSAGE_TEMPLATE, token.getRow(), token.getColumn(), token.toString()));
    }
}