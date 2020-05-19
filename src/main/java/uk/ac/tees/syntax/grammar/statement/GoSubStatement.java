package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeNodeVisitor;

import java.util.Objects;

/**
 * GOSUB statement, similar to {@link GoToStatement} branch statement however previous instruction pointer is
 * tracked and therefore upon calling RETURN from the GOSUB subroutine, the current instruction pointer will return
 * to where it was previously, before GOSUB.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class GoSubStatement extends Statement {

    /**
     * The expression that evaluates to the target location.
     */
    private final AbstractSyntaxTreeNode expression;

    public GoSubStatement(AbstractSyntaxTreeNode expression) {
        super("GOSUB");
        this.expression = expression;
    }

    public AbstractSyntaxTreeNode getExpression() {
        return expression;
    }

    @Override
    public void accept(AbstractSyntaxTreeNodeVisitor visitor) {
        // Accept the expression first.
        expression.accept(visitor);

        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expression);
    }
}