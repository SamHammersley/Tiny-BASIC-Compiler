package uk.ac.tees.syntax.parser;

import uk.ac.tees.syntax.parser.exception.ParseException;
import uk.ac.tees.syntax.parser.exception.UnexpectedTokenException;
import uk.ac.tees.tokenizer.Token;

import java.util.Arrays;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Predicate;

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
     * Assert that the current token should satisfy the given predicate, if not an exception is thrown.
     *
     * @param predicate {@link #currentToken} should satisfy this predicate.
     * @throws UnexpectedTokenException when the given predicate is not satisfied by the current token.
     */
    private void expect(Predicate<Token> predicate) throws UnexpectedTokenException {
        if (!predicate.test(currentToken)) {
            throw new UnexpectedTokenException(currentToken);
        }
    }

    /**
     * Assert that the current token should be one of the given types.
     *
     * @param types the collection of acceptable types.
     * @throws UnexpectedTokenException if {@link #currentToken} is not one of the given types.
     */
    void expectType(Token.Type... types) throws UnexpectedTokenException {
        expect(t -> Arrays.asList(types).contains(t.getType()));
    }

    /**
     * Assert that the value of the current token should satisfy the given token.
     *
     * @param valuePredicate the predicate that should be satisfied for the current token.
     * @throws UnexpectedTokenException if the current token does not satisfy the given predicate.
     */
    void expectValue(Predicate<String> valuePredicate) throws UnexpectedTokenException {
        expect(t -> valuePredicate.test(t.getValue()));
    }

    /**
     * Polls the token queue, providing it's not empty, and asserts that the current token, as a result of polling
     * {@link #tokens}, satisfies the given predicate.
     *
     * @param predicate {@link #currentToken} should satisfy this predicate.
     * @throws ParseException if the token does not satisfy the predicate.
     */
    private void nextTokenConditional(Predicate<Token> predicate) throws ParseException {
        if (tokens.isEmpty()) {
            throw new ParseException("Unexpected end of tokens");
        }

        currentToken = tokens.poll();

        if (predicate != null) {
            expect(predicate);
        }
    }

    /**
     * Gets the next token regardless of conditions.
     *
     * @throws ParseException if there are no more tokens.
     */
    void nextToken() throws ParseException {
        nextTokenConditional(t -> true);
    }

    void nextToken(Predicate<String> valuePredicate) throws ParseException {
        nextTokenConditional(t -> valuePredicate.test(t.getValue()));
    }

    void nextToken(Token.Type... types) throws ParseException {
        nextTokenConditional(t -> Arrays.asList(types).contains(t.getType()));
    }

    boolean currentTypeIs(Token.Type... expectedTypes) {
        return Arrays.asList(expectedTypes).contains(currentToken.getType());
    }

}