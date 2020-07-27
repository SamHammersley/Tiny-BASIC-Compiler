package uk.ac.tees.syntax.parser.exception;

import uk.ac.tees.tokenizer.Token;

public class UnexpectedTokenValueException extends ParseException {

    private static final String MESSAGE_TEMPLATE = "Expected token value: %s but got %s on line %d, column %d";

    public UnexpectedTokenValueException(Token token, String expectedValue) {
        super(String.format(MESSAGE_TEMPLATE, expectedValue, token.getValue(), token.getRow(), token.getColumn()));
    }
}