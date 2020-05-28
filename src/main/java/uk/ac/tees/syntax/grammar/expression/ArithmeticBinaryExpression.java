package uk.ac.tees.syntax.grammar.expression;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeNodeVisitor;

/**
 * An arithmetic binary expression, that has an {@link ArithmeticOperator} and two operands.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class ArithmeticBinaryExpression extends BinaryExpression<ArithmeticOperator> {

    public ArithmeticBinaryExpression(AbstractSyntaxTreeNode left, AbstractSyntaxTreeNode right, ArithmeticOperator operator) {
        super(left, right, operator);
    }

    @Override
    public void accept(AbstractSyntaxTreeNodeVisitor visitor) {
        // Need to accept both sides/child nodes of the expression first.
        left.accept(visitor);
        right.accept(visitor);

        // Then visit this node.
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Arithmetic(" + operator.toString() + ")";
    }

}