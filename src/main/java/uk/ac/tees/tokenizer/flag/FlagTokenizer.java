package uk.ac.tees.tokenizer.flag;

import uk.ac.tees.tokenizer.*;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Predicate;

/**
 * Tokenizes input by sequentially checking various conditions/flags, in a particular order, using a process of
 * elimination and ultimately throwing an exception if the character did not satisfy any of the conditions/flags.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class FlagTokenizer implements TinyBasicTokenizer {

    @Override
    public Queue<Token> tokenize(String input) throws UnexpectedCharacterException {
        TokenizerCursor cursor = new TokenizerCursor(input);
        Queue<Token> tokens = new LinkedList<>();

        while (cursor.hasRemaining()) {
            char nextCharacter = cursor.remaining().charAt(0);

            if (Character.toString(nextCharacter).matches("\\h")) {
                cursor.advance(nextCharacter);

            } else {
                Token token = nextToken(cursor);
                cursor.advance(token.getValue());

                tokens.add(token);
            }
        }

        return tokens;
    }

    /**
     * Gets the next token from the character queue.
     *
     * @param cursor the cursor containing the remaining input and tracking the column and row of the read character.
     * @return the next {@link Token} from the input encapsulated in the cursor.
     * @throws UnexpectedCharacterException when the input doesn't match any defined token type.
     */
    private Token nextToken(TokenizerCursor cursor) throws UnexpectedCharacterException {
        String remainingInput = cursor.remaining();
        char character = remainingInput.charAt(0);

        Token nextOneCharToken = nextOneCharToken(cursor);

        if (nextOneCharToken != null) {
            return nextOneCharToken;

        } else if (Character.isLetter(character)) {

            if (remainingInput.length() < 2 || !Character.isLetter(remainingInput.charAt(1))) {
                return new Token(Token.Type.IDENTIFIER, Character.toString(character), cursor.row(), cursor.column());

            } else {
                return new Token(Token.Type.KEYWORD, nextCharSequence(cursor, Character::isLetter), cursor.row(), cursor.column());
            }

        } else if (Character.isDigit(character)) {
            return new Token(Token.Type.NUMBER, nextCharSequence(cursor, Character::isDigit), cursor.row(), cursor.column());

        } else if (isRelationalOperator(character)) {
            return new Token(Token.Type.REL_OP, nextCharSequence(cursor, this::isRelationalOperator), cursor.row(), cursor.column());

        } else if (isQuotationMark(character)) {
            String tokenBuilder = nextCharSequence(cursor, Predicate.not(this::isQuotationMark)) + "\"";

            return new Token(Token.Type.STRING_EXPRESSION, tokenBuilder, cursor.row(), cursor.column());
        }

        throw new UnexpectedCharacterException(cursor.row(), cursor.column());
    }

    /**
     * Gets sequence of characters from the character queue until the given loop predicate is no longer satisfied.
     *
     * @param cursor the cursor containing the remaining input and tracking the column and row of the read character.
     * @param loopPredicate the predicate that is to be satisfied for each character from the queue.
     * @return a {@link StringBuilder} containing the sequence of characters.
     */
    private String nextCharSequence(TokenizerCursor cursor, Predicate<Character> loopPredicate) {
        StringBuilder tokenBuilder = new StringBuilder();
        tokenBuilder.append(cursor.remaining().charAt(0));

        int index = 1;

        while (index < cursor.remaining().length() && loopPredicate.test(cursor.remaining().charAt(index))) {
            tokenBuilder.append(cursor.remaining().charAt(index++));
        }

        return tokenBuilder.toString();
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
     * @param cursor the cursor containing the remaining input and tracking the column and row of the read character.
     * @return a {@link Token} that is one character in length.
     */
    private Token nextOneCharToken(TokenizerCursor cursor) {
        String asString = Character.toString(cursor.remaining().charAt(0));

        int row = cursor.row(), column = cursor.column();

        switch (asString) {
            case "\n":
                cursor.nextLine();
                return new Token(Token.Type.NEW_LINE, asString, row, column);
            case ",":
                return new Token(Token.Type.COMMA, asString, row, column);
            case "+":
                return new Token(Token.Type.PLUS, asString, row, column);
            case "-":
                return new Token(Token.Type.MINUS, asString, row, column);
            case "/":
                return new Token(Token.Type.DIV, asString, row, column);
            case "*":
                return new Token(Token.Type.MULTIPLY, asString, row, column);
            case "(":
                return new Token(Token.Type.L_PARENTHESES, asString, row, column);
            case ")":
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