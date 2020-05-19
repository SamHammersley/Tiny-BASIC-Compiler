package uk.ac.tees.syntax.grammar.expression;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeNodeVisitor;

import java.util.Objects;

/**
 * An arithmetic binary expression, that has an {@link ArithmeticOperator} and two operands.
 *
 * {@author Sam Hammersley - Gonsalves (q5315908)}
 */
public final class ArithmeticBinaryExpression implements AbstractSyntaxTreeNode {

    /**
     * The left hand side of the expression, this is a node of the tree and may itself be an expression.
     */
    private final AbstractSyntaxTreeNode left;

    /**
     * The right hand side of the expression, this is a node of the tree and may itself be an expression.
     */
    private final AbstractSyntaxTreeNode right;

    /**
     * The operator for this binary expression.
     */
    private final ArithmeticOperator operator;

    public ArithmeticBinaryExpression(AbstractSyntaxTreeNode left, AbstractSyntaxTreeNode right, ArithmeticOperator operator) {
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

    @Override
    public String toString() {
        return operator.toString();
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
    public int hashCode() {
        return Objects.hash(super.hashCode(), left, right, operator);
    }

}