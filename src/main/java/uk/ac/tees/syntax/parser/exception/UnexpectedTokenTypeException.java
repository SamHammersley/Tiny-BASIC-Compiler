package uk.ac.tees.syntax.parser.exception;

import uk.ac.tees.tokenizer.Token;

import java.util.Arrays;

public class UnexpectedTokenTypeException extends ParseException {

    private static final String SINGLE_TYPE_MESSAGE_FORMAT = "Expected token type %s but got %s on line %d, column %d";

    private static final String MULTI_TYPE_MESSAGE_FORMAT = "Expected one of token types: %s; but got %s on line %d, column %d";

    public UnexpectedTokenTypeException(Token token, Token.Type... expectedTypes) {
        super(message(token, expectedTypes));
    }

    private static String message(Token token, Token.Type... expectedTypes) {
        String format = expectedTypes.length == 1 ? SINGLE_TYPE_MESSAGE_FORMAT : MULTI_TYPE_MESSAGE_FORMAT;
        String typeString = expectedTypes.length == 1 ? expectedTypes[0].toString() : Arrays.toString(expectedTypes);

        return String.format(format, typeString, token.getType(), token.getRow(), token.getColumn());
    }

}