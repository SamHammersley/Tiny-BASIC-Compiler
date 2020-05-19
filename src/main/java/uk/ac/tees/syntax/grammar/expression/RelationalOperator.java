package uk.ac.tees.syntax.grammar.expression;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Represents an relational operator for a boolean binary expression.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public enum RelationalOperator {

    LESS("<"),

    LESS_EQUAL("<="),

    EQUAL("="),

    NOT_EQUAL("!="),

    GREATER(">"),

    GREATER_EQUAL(">=");

    /**
     * The symbol associated with this operator.
     */
    private final String symbol;

    RelationalOperator(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Gets the {@link RelationalOperator} associated with the specified symbol.
     *
     * @param symbol the symbol associated with the desired operator.
     * @return {@link RelationalOperator}
     */
    public static RelationalOperator fromSymbol(String symbol) {
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