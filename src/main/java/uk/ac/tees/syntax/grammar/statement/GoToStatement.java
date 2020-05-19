package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeNodeVisitor;

import java.util.Objects;

/**
 * GOTO jumps to the given target line.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class GoToStatement extends Statement {

    /**
     * The expression that evaluates to the target location.
     */
    private final AbstractSyntaxTreeNode expression;

    public GoToStatement(AbstractSyntaxTreeNode expression) {
        super("GOTO");
        this.expression = expression;
    }

    public AbstractSyntaxTreeNode getExpression() {
        return expression;
    }

    @Override
    public void accept(AbstractSyntaxTreeNodeVisitor visitor) {
        expression.accept(visitor);

        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expression);
    }

}