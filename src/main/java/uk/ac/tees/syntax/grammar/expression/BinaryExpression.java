package uk.ac.tees.syntax.grammar.expression;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;

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
    protected final AbstractSyntaxTreeNode left;

    /**
     * The right hand side of the expression, this is a node of the tree and may itself be an expression.
     */
    protected final AbstractSyntaxTreeNode right;

    /**
     * The operator for this binary expression.
     */
    protected final T operator;

    protected BinaryExpression(AbstractSyntaxTreeNode left, AbstractSyntaxTreeNode right, T operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    /**
     * Accessor method for the left side of the tree.
     *
     * @return {@link #left}
     */
    public AbstractSyntaxTreeNode getLeft() {
        return left;
    }

    /**
     * Accessor method for the right side of the tree.
     *
     * @return {@link #right}
     */
    public AbstractSyntaxTreeNode getRight() {
        return right;
    }

    public T getOperator() {
        return operator;
    }

}