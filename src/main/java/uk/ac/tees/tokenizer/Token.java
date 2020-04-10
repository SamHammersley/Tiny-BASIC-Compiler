package uk.ac.tees.tokenizer;

/**
 * Represents a token of some input source code.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public class Token {

    /**
     * The type of this token.
     */
    private final Type type;

    /**
     * The value held in this token, certain types do not require a value. Null represents empty value.
     */
    private final String value;

    /**
     * Constructs a new {@link Token} with a given {@link #type} and value;
     *
     * @param type the type of token represented.
     * @param value the value held within this token.
     */
    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
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