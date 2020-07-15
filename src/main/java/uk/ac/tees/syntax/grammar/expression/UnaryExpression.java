package uk.ac.tees.syntax.grammar.expression;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

/**
 * An expression that is prefixed with a {@link UnaryOperator}.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class UnaryExpression implements AbstractSyntaxTreeNode {

    /**
     * The operator prefixing {@link #expression}.
     */
    private final UnaryOperator operator;

    /**
     * The expression.
     */
    private final AbstractSyntaxTreeNode expression;

    public UnaryExpression(UnaryOperator operator, AbstractSyntaxTreeNode expression) {
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public <T, K extends AbstractSyntaxTreeNode> void accept(AbstractSyntaxTreeVisitor<T, K> visitor) {
        expression.accept(visitor);

        visitor.visitNode(this);
    }

    @Override
    public String toString() {
        return operator.toString();
    }

    public UnaryOperator getOperator() {
        return operator;
    }

    public AbstractSyntaxTreeNode getExpression() {
        return expression;
    }
}