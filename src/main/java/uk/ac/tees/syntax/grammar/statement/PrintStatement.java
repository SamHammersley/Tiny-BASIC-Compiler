package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * PRINT statement prints the expression to standard out, followed by a new line.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public class PrintStatement extends Statement {

    /**
     * The {@link AbstractSyntaxTreeNode} expression to print.
     */
    private final List<AbstractSyntaxTreeNode> expressions;

    public PrintStatement(List<AbstractSyntaxTreeNode> expressions) {
        super("PRINT");
        this.expressions = expressions;
    }

    public PrintStatement(AbstractSyntaxTreeNode...expressions) {
        this(Arrays.asList(expressions));
    }

    public List<AbstractSyntaxTreeNode> getExpressions() {
        return expressions;
    }

    @Override
    public <T, K extends AbstractSyntaxTreeNode> void accept(AbstractSyntaxTreeVisitor<T, K> visitor) {
        for (AbstractSyntaxTreeNode e : expressions) {
            e.accept(visitor);
        }

        visitor.visitNode(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof PrintStatement other)) {
            return false;
        }

        return expressions.equals(other.expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expressions);
    }
}