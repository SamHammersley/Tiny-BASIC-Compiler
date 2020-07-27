package uk.ac.tees.syntax.parser;

import uk.ac.tees.syntax.parser.exception.ParseException;
import uk.ac.tees.syntax.parser.exception.UnexpectedTokenTypeException;
import uk.ac.tees.syntax.parser.exception.UnexpectedTokenValueException;
import uk.ac.tees.tokenizer.Token;

import java.util.Arrays;
import java.util.Queue;
import java.util.function.Function;

/**
 * Represents a sequence of tokens, supplying tokens as requested.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class TokenSupplier {

    /**
     * The {@link Queue} of tokens.
     */
    private final Queue<Token> tokens;

    /**
     * The current token, the head of the queue.
     */
    private Token currentToken;

    public TokenSupplier(Queue<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Accessor for the current token.
     *
     * @return {@link #currentToken}
     */
    Token getCurrentToken() {
        return currentToken;
    }

    /**
     * Denotes whether or not there are tokens left in the queue.
     *
     * @return {@code true} if the token queue isn't empty.
     */
    boolean hasNext() {
        return !tokens.isEmpty();
    }

    /**
     * Gets the value of the current token as a {@link String}.
     *
     * @return string value of the current token.
     */
    String getValue() {
        return getCurrentToken().getValue();
    }

    /**
     * Gets the value as an object of type T by invoking the given mapping function on the {@link #currentToken}.
     *
     * @param transformer function that takes current token's value as a string and returns object of type T.
     * @param <T>         the type of object to be returned after applying the transformer function.
     * @return an object of type T.
     */
    <T> T getValue(Function<String, T> transformer) {
        return transformer.apply(getCurrentToken().getValue());
    }

    /**
     * Gets the type of the current token.
     *
     * @return the type of the current token.
     */
    Token.Type getType() {
        return getCurrentToken().getType();
    }

    /**
     * Assert that the current token should be one of the given types.
     *
     * @param types the collection of acceptable types.
     * @throws UnexpectedTokenTypeException if {@link #currentToken} is not one of the given types.
     */
    void predictType(Token.Type... types) throws UnexpectedTokenTypeException {
        if (!Arrays.asList(types).contains(currentToken.getType())) {
            throw new UnexpectedTokenTypeException(currentToken, types);
        }
    }

    /**
     * Assert that the value of the current token should satisfy the given token.
     *
     * @param value the value that is expected to match the {@link #currentToken}'s value.
     * @throws UnexpectedTokenValueException if the current token's value does not match the given expected value.
     */
    void predictValue(String value) throws UnexpectedTokenValueException {
        if (!getValue().equals(value)) {
            throw new UnexpectedTokenValueException(currentToken, value);
        }
    }

    /**
     * Requires that there are more tokens in the queue and throws an exception otherwise.
     *
     * @throws ParseException if there are no more tokens.
     */
    private void requireNotEnd() throws ParseException {
        if (tokens.isEmpty()) {
            throw new ParseException("Unexpected end of tokens");
        }
    }

    /**
     * Gets the next token.
     *
     * @throws ParseException if there are no more tokens.
     */
    void scan() throws ParseException {
        requireNotEnd();

        currentToken = tokens.poll();
    }

    /**
     * Gets the next token providing that the given value matches the next tokens value.
     *
     * @param value the expected value.
     * @throws ParseException if there are no more tokens or when the given value does not match the next token's value.
     */
    void scan(String value) throws ParseException {
        scan();

        predictValue(value);
    }

    /**
     * Gets the next token providing that the next token's type is one of the given types.
     *
     * @param types the list of types that are permitted.
     * @throws ParseException if there are no more tokens or when the given type does not match the next token's type.
     */
    void scan(Token.Type... types) throws ParseException {
        scan();

        predictType(types);
    }

    boolean currentTypeIs(Token.Type... expectedTypes) {
        return Arrays.asList(expectedTypes).contains(currentToken.getType());
    }

}