package uk.ac.tees.tokenizer;

import java.util.Objects;

/**
 * Represents a token of some input source code.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class Token {

    /**
     * The type of this token.
     */
    private final Type type;

    /**
     * The value held in this token, certain types do not require a value. Null represents empty value.
     */
    private final String value;

    /**
     * Encapsulates row and column positions of this token, bit-packed. First 16 bits represents the row, final 16 bits
     * represents the column.
     * <p>
     * There are potentially many tokens in a piece of source code text therefore, in order to minimise the space each
     * token occupies in memory, row and column data is packed with one integer. This allocates 16 bits for each piece
     * of data.
     */
    private final int position;

    /**
     * Constructs a new {@link Token} with a given {@link #type} and value;
     *
     * @param type  the type of token represented.
     * @param value the value held within this token.
     */
    public Token(Type type, String value, int row, int column) {
        this.type = type;
        this.value = value;
        this.position = row << 16 | (column & 0xFFFF);
    }

    /**
     * Accessor function for type field.
     *
     * @return the {@link #type} of this function.
     */
    public Type getType() {
        return type;
    }

    /**
     * Accessor function for value field.
     *
     * @return the string value for this token.
     */
    public String getValue() {
        return value;
    }

    /**
     * Accessor function for the row (line) position of this token.
     *
     * @return the row/line this token is at in the source code.
     */
    public int getRow() {
        return position >> 16;
    }

    /**
     * Accessor function for the column position (starting position) of this token.
     *
     * @return the column this token starts at in the source code.
     */
    public int getColumn() {
        return position & 0xFFFF;
    }

    @Override
    public String toString() {
        return type + ", " + value;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Token)) {
            return false;
        }

        Token other = (Token) object;

        return type.equals(other.type) && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    /**
     * Token type
     */
    public enum Type {

        PLUS,

        MINUS,

        MULTIPLY,

        DIV,

        KEYWORD,

        L_PARENTHESES,

        R_PARENTHESES,

        NUMBER,

        NEW_LINE,

        STRING_EXPRESSION,

        COMMA,

        IDENTIFIER,

        REL_OP

    }

}