package uk.ac.tees.syntax.grammar.expression;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Represents an arithmetic operator for an arithmetic binary expression.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public enum ArithmeticOperator {

    ADD("+"),

    SUB("-"),

    MUL("*"),

    DIV("/");

    /**
     * The symbol associated with this operator.
     */
    private final String symbol;

    ArithmeticOperator(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Gets the {@link ArithmeticOperator} associated with the specified symbol.
     *
     * @param symbol the symbol associated with the desired operator.
     * @return {@link ArithmeticOperator}
     */
    public static ArithmeticOperator fromSymbol(String symbol) {
        return Arrays
                .stream(values())
                .filter(o -> o.symbol.equals(symbol))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public String toString() {
        return name() + "(" + symbol + ")";
    }
}