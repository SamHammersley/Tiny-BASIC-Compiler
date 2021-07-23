package uk.ac.tees.syntax.grammar.expression.arithmetic;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.grammar.expression.BinaryExpression;
import uk.ac.tees.syntax.grammar.expression.Expression;

import java.util.Objects;

/**
 * An arithmetic binary expression, that has an {@link ArithmeticOperator} and two operands.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class ArithmeticBinaryExpression extends BinaryExpression<ArithmeticOperator> {

    public ArithmeticBinaryExpression(Expression left, Expression right, ArithmeticOperator operator) {
        super(left, right, operator);
    }

    @Override
    public String toString() {
        return "Arithmetic(" + operator.toString() + ")";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ArithmeticBinaryExpression)) {
            return false;
        }

        ArithmeticBinaryExpression other = (ArithmeticBinaryExpression) object;
        return left.equals(other.left) && right.equals(other.right) && operator.equals(other.operator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, operator);
    }
}