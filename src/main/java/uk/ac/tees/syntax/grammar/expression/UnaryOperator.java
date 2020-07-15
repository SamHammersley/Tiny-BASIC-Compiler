package uk.ac.tees.syntax.grammar.expression;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Represents an operator that operates with one argument.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public enum UnaryOperator {

    ADD("+"),

    SUB("-");

    /**
     * The string symbol associated with this operator.
     */
    private final String symbol;

    UnaryOperator(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Gets the {@link UnaryOperator} associated with the specified symbol.
     *
     * @param symbol the symbol associated with the desired operator.
     * @return {@link UnaryOperator}
     */
    public static UnaryOperator fromSymbol(String symbol) {
        return Arrays
                .stream(values())
                .filter(o -> o.symbol.equals(symbol))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public String toString() {
        return symbol;
    }

}