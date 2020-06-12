package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A print statement that has multiple expressions. A {@link PrintStatement} is created for each of the expressions
 * and those statements make up a {@link CompoundPrintStatement}.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class CompoundPrintStatement extends Statement {

    /**
     * {@link List} of {@link PrintStatement}s that make up this {@link CompoundPrintStatement}.
     */
    private final List<PrintStatement> statements = new LinkedList<>();

    public CompoundPrintStatement() {
        super("PRINT");
    }

    /**
     * Adds a new {@link PrintStatement} for the given expression, as an {@link AbstractSyntaxTreeNode}, to this
     * {@link CompoundPrintStatement}.
     *
     * @param expression the expression to add a new print statement for.
     */
    public void addExpression(AbstractSyntaxTreeNode expression) {
        statements.add(new PrintStatement(expression));
    }

    /**
     * Get the list of {@link PrintStatement}s for this {@link CompoundPrintStatement}.
     *
     * @return {@link List} of constituent {@link PrintStatement}s.
     */
    public List<PrintStatement> getStatements() {
        return Collections.unmodifiableList(statements);
    }

    @Override
    public void accept(AbstractSyntaxTreeVisitor visitor) {
        statements.forEach(statement -> statement.accept(visitor));

        visitor.visitNode(this);
    }
}