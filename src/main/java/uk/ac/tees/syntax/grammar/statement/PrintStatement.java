package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

import java.util.List;

/**
 * PRINT statement prints each of the expressions to standard out, seperated by \n.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class PrintStatement extends Statement {

    /**
     * {@link List} of expressions to print.
     */
    private final List<AbstractSyntaxTreeNode> expressions;

    public PrintStatement(List<AbstractSyntaxTreeNode> expressions) {
        super("PRINT");
        this.expressions = expressions;
    }

    public List<AbstractSyntaxTreeNode> getExpressions() {
        return expressions;
    }

    @Override
    public void accept(AbstractSyntaxTreeVisitor visitor) {
        expressions.forEach(e -> e.accept(visitor));

        visitor.visit(this);
    }

}