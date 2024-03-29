package uk.ac.tees.syntax.grammar.expression;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

/**
 * An expression with two operands and an operator. This class is a composite {@link AbstractSyntaxTreeNode} and can be
 * treated as such, where the two operands are themselves {@link AbstractSyntaxTreeNode}s. This allows the expression
 * to be treated as a node itself.
 *
 * @param <T> the binary operator type.
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public abstract class BinaryExpression<T extends BinaryOperator> implements Expression {

    /**
     * The left hand side of the expression, this is a node of the tree and may itself be an expression.
     */
    protected final Expression left;

    /**
     * The right hand side of the expression, this is a node of the tree and may itself be an expression.
     */
    protected final Expression right;

    /**
     * The operator for this binary expression.
     */
    protected final T operator;

    protected BinaryExpression(Expression left, Expression right, T operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public final <S, K extends AbstractSyntaxTreeNode> void accept(AbstractSyntaxTreeVisitor<S, K> visitor) {
        // Need to accept both sides/child nodes of the expression first.
        left.accept(visitor);
        right.accept(visitor);

        // Then visit this node.
        visitor.visitNode(this);
    }

    /**
     * Accessor method for the left side of the tree.
     *
     * @return {@link #left}
     */
    public Expression getLeft() {
        return left;
    }

    /**
     * Accessor method for the right side of the tree.
     *
     * @return {@link #right}
     */
    public Expression getRight() {
        return right;
    }

    public T getOperator() {
        return operator;
    }

}