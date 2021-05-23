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
     * The line on which this token resides.
     */
    private final int row;

    /**
     * The column at which this token starts.
     */
    private final int column;

    /**
     * Constructs a new {@link Token} with a given {@link #type} and value;
     *
     * @param type   the type of token represented.
     * @param value  the value held within this token.
     * @param row    the row at which this token starts.
     * @param column the column this token is on.
     */
    public Token(Type type, String value, int row, int column) {
        this.type = type;
        this.value = value;
        this.row = row;
        this.column = column;
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
        return row;
    }

    /**
     * Accessor function for the column position (starting position) of this token.
     *
     * @return the column this token starts at in the source code.
     */
    public int getColumn() {
        return column;
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