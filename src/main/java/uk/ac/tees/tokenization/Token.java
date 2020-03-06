package uk.ac.tees.tokenization;

import java.util.Optional;

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
     * Constructs a new {@link Token} with an empty value.
     *
     * @param type the type of token represented.
     */
    public Token(Type type) {
        this(type, null);
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
     * @return an {@link Optional}, since nullable, for the value field.
     */
    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }

    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder(type.name());

        getValue().ifPresent(v -> bldr.append(", ").append(v));

        return bldr.toString();
    }

    /**
     * Token type
     */
    public enum Type {

        PLUS,

        MINUS,

        MULTIPLY,

        DIV,

        REL_OP,

        KEYWORD,

        L_PARENTHESES,

        R_PARENTHESES,

        NUMBER,

        NEW_LINE,

        STRING_EXPRESSION

    }

}