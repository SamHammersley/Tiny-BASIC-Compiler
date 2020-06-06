package uk.ac.tees.syntax.grammar.expression.relational;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.grammar.expression.BinaryExpression;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

/**
 * A boolean binary expression, that has a {@link RelationalOperator} and two operands.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class RelationalBinaryExpression  extends BinaryExpression<RelationalOperator> {

    public RelationalBinaryExpression(AbstractSyntaxTreeNode left, AbstractSyntaxTreeNode right, RelationalOperator operator) {
        super(left, right, operator);
    }

    @Override
    public void accept(AbstractSyntaxTreeVisitor visitor) {
        // Need to accept both sides/child nodes of the expression first.
        left.accept(visitor);
        right.accept(visitor);

        // Then visit this node.
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Relational(" + operator.toString() + ")";
    }

}