package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

/**
 * PRINT statement prints the expression to standard out, followed by a new line.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public class PrintStatement extends Statement {

    /**
     * The {@link AbstractSyntaxTreeNode} expression to print.
     */
    private final AbstractSyntaxTreeNode expression;

    public PrintStatement(AbstractSyntaxTreeNode expression) {
        super("PRINT");
        this.expression = expression;
    }

    public AbstractSyntaxTreeNode getExpression() {
        return expression;
    }

    @Override
    public void accept(AbstractSyntaxTreeVisitor visitor) {
        expression.accept(visitor);

        visitor.visitNode(this);
    }

}