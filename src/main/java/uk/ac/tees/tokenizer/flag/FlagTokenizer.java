package uk.ac.tees.tokenizer.flag;

import uk.ac.tees.tokenizer.TinyBasicTokenizer;
import uk.ac.tees.tokenizer.Token;
import uk.ac.tees.tokenizer.TokenizationException;
import uk.ac.tees.tokenizer.UnexpectedCharacterException;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Tokenizes input by sequentially checking various conditions/flags, in a particular order, using a process of
 * elimination and ultimately throwing an exception if the character did not satisfy any of the conditions/flags.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class FlagTokenizer implements TinyBasicTokenizer {

    private int column, row;

    @Override
    public Queue<Token> tokenize(String input) throws TokenizationException {
        column = row = 1;

        Queue<Token> tokens = new LinkedList<>();
        Queue<Character> chars = input.chars().mapToObj(i -> (char) i).collect(Collectors.toCollection(LinkedList::new));

        while (!chars.isEmpty()) {
            char nextCharacter = chars.poll();

            if (nextCharacter == '\n') {
                column = 0;
                row++;
            } else if (Character.isWhitespace(nextCharacter)) {
                column++;
                continue;
            }

            Token nextToken = nextToken(nextCharacter, chars)
                    .orElseThrow(this::createUnexpectedCharacterException);

            column += nextToken.getValue().length();
            tokens.add(nextToken);
        }

        return tokens;
    }

    /**
     * Creates a new instance of {@link UnexpectedCharacterException} using the current column and row values.
     *
     * @return a new {@link UnexpectedCharacterException} using values {@link #row} and {@link #column}
     */
    private UnexpectedCharacterException createUnexpectedCharacterException() {
        return new UnexpectedCharacterException(row, column);
    }

    /**
     * Gets the next token from the character queue.
     *
     * @param firstChar the first character, polled, of the queue.
     * @param chars     the character queue to take tokensIterator from.
     * @return the next {@link Token} wrapped in an {@link Optional}
     */
    private Optional<Token> nextToken(char firstChar, Queue<Character> chars) {
        Token nextToken = nextOneCharToken(firstChar);

        if (Character.isLetter(firstChar)) {

            if (chars.isEmpty() || !Character.isLetter(chars.peek())) {
                nextToken = new Token(Token.Type.IDENTIFIER, Character.toString(firstChar), row, column);

            } else {

                nextToken = nextKeywordToken(firstChar, chars);
            }

        } else if (Character.isDigit(firstChar)) {
            nextToken = nextNumberToken(firstChar, chars);

        } else if (isRelationalOperator(firstChar)) {
            nextToken = nextRelOpToken(firstChar, chars);

        } else if (isQuotationMark(firstChar)) {
            StringBuilder tokenBuilder = nextCharSequence(firstChar, chars, Predicate.not(this::isQuotationMark))
                    .append(chars.poll());

            nextToken = new Token(Token.Type.STRING_EXPRESSION, tokenBuilder.toString(), row, column);
        }

        return Optional.ofNullable(nextToken);
    }

    /**
     * Gets sequence of characters from the character queue until the given loop predicate is no longer satisfied.
     *
     * @param firstChar     the first character, polled, of the queue.
     * @param chars         the character queue to take tokensIterator from.
     * @param loopPredicate the predicate that is to be satisfied for each character from the queue.
     * @return a {@link StringBuilder} containing the sequence of characters.
     */
    private StringBuilder nextCharSequence(char firstChar, Queue<Character> chars, Predicate<Character> loopPredicate) {
        StringBuilder tokenBuilder = new StringBuilder();

        tokenBuilder.append(firstChar);

        while (!chars.isEmpty() && loopPredicate.test(chars.peek())) {
            tokenBuilder.append(chars.poll());
        }

        return tokenBuilder;
    }

    /**
     * Gets the next keyword token from the character queue.
     *
     * @param firstChar the first character, polled, of the queue.
     * @param chars     the character queue to take tokensIterator from.
     * @return a {@link Token.Type#KEYWORD} token with the char sequence from the character queue.
     */
    private Token nextKeywordToken(char firstChar, Queue<Character> chars) {
        return new Token(Token.Type.KEYWORD, nextCharSequence(firstChar, chars, Character::isLetter).toString(), row, column);
    }

    /**
     * Gets the next number token from the character queue.
     *
     * @param firstChar the first character, polled, of the queue.
     * @param chars     the character queue to take tokensIterator from.
     * @return a {@link Token.Type#NUMBER} token with the char sequence from the character queue.
     */
    private Token nextNumberToken(char firstChar, Queue<Character> chars) {
        return new Token(Token.Type.NUMBER, nextCharSequence(firstChar, chars, Character::isDigit).toString(), row, column);
    }

    /**
     * Gets the next keyword token from the character queue.
     *
     * @param firstChar the first character, polled, of the queue.
     * @param chars     the character queue to take tokensIterator from.
     * @return a {@link Token.Type#REL_OP} token with the char sequence from the character queue.
     */
    private Token nextRelOpToken(char firstChar, Queue<Character> chars) {
        return new Token(Token.Type.REL_OP, nextCharSequence(firstChar, chars, this::isRelationalOperator).toString(), row, column);
    }

    /**
     * Gets the next one-character token from the character queue. The returned {@link Token} may be one of the
     * following types:
     *
     * <ul>
     * <li>
     * {@link Token.Type#COMMA}
     * </li>
     * <li>
     * {@link Token.Type#NEW_LINE}
     * </li>
     * <li>
     * {@link Token.Type#PLUS}
     * </li>
     * <li>
     * {@link Token.Type#MINUS}
     * </li>
     * <li>
     * {@link Token.Type#DIV}
     * </li>
     * <li>
     * {@link Token.Type#MULTIPLY}
     * </li>
     * <li>
     * {@link Token.Type#L_PARENTHESES}
     * </li>
     * <li>
     * {@link Token.Type#R_PARENTHESES}
     * </li>
     * </ul>
     *
     * @param firstChar the first character, polled, of the queue.
     * @return a {@link Token} that is one character in length.
     */
    private Token nextOneCharToken(char firstChar) {
        String asString = Character.toString(firstChar);

        switch (firstChar) {
            case ',':
                return new Token(Token.Type.COMMA, asString, row, column);
            case '\n':
                return new Token(Token.Type.NEW_LINE, asString, row, column);
            case '+':
                return new Token(Token.Type.PLUS, asString, row, column);
            case '-':
                return new Token(Token.Type.MINUS, asString, row, column);
            case '/':
                return new Token(Token.Type.DIV, asString, row, column);
            case '*':
                return new Token(Token.Type.MULTIPLY, asString, row, column);
            case '(':
                return new Token(Token.Type.L_PARENTHESES, asString, row, column);
            case ')':
                return new Token(Token.Type.R_PARENTHESES, asString, row, column);
        }

        return null;
    }

    /**
     * Denotes whether the given character is a relational operator character.
     *
     * @param c the character to test.
     * @return {@code true} if the character is a relational operator.
     */
    private boolean isRelationalOperator(char c) {
        return c == '<' || c == '>' || c == '=';
    }

    /**
     * Denotes whether the given character is a quotation mark.
     *
     * @param c the character to test.
     * @return {@code true} if the character is a quotation mark.
     */
    private boolean isQuotationMark(char c) {
        return c == '"';
    }

}