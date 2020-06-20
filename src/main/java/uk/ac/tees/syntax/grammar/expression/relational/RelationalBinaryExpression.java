package uk.ac.tees.syntax.grammar.expression.relational;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.grammar.expression.BinaryExpression;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

import java.util.Objects;

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
    public <T, K extends AbstractSyntaxTreeNode> void accept(AbstractSyntaxTreeVisitor<T, K> visitor) {
        // Need to accept both sides/child nodes of the expression first.
        left.accept(visitor);
        right.accept(visitor);

        // Then visit this node.
        visitor.visitNode(this);
    }

    @Override
    public String toString() {
        return "Relational(" + operator.toString() + ")";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof RelationalBinaryExpression)) {
            return false;
        }

        RelationalBinaryExpression other = (RelationalBinaryExpression) object;
        return left.equals(other.left) && right.equals(other.right) && operator.equals(other.operator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, operator);
    }
}