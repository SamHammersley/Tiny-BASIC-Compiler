package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.expression.RelationalBinaryExpression;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeNodeVisitor;

import java.util.Objects;

/**
 * Represents a one-armed if statement.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class IfStatement extends Statement {

    /**
     * A binary expression that has a relational operator. Should evaluate to {@code true} or {@code false}.
     */
    private final RelationalBinaryExpression expression;

    /**
     * The statement if the {@link #expression} passes.
     */
    private final Statement statement;

    public IfStatement(RelationalBinaryExpression expression, Statement statement) {
        super("IF");

        this.expression = expression;
        this.statement = statement;
    }

    public RelationalBinaryExpression getExpression() {
        return expression;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public void accept(AbstractSyntaxTreeNodeVisitor visitor) {
        // Accept the child nodes before visiting this node.
        expression.accept(visitor);
        statement.accept(visitor);

        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expression, statement);
    }

}