package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.expression.relational.RelationalBinaryExpression;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

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
    public void accept(AbstractSyntaxTreeVisitor visitor) {
        // Accept (visit) the expression first.
        expression.accept(visitor);

        visitor.visitNode(this);

        statement.accept(visitor);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof IfStatement)) {
            return false;
        }

        IfStatement other = (IfStatement) object;

        return expression.equals(other.expression) && statement.equals(other.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, statement);
    }
}