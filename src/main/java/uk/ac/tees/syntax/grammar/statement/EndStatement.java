package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

/**
 * An END statement, denotes the end of the program.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class EndStatement extends Statement {

    public EndStatement() {
        super("END");
    }

    @Override
    public <T, K extends AbstractSyntaxTreeNode> void accept(AbstractSyntaxTreeVisitor<T, K> visitor) {
        visitor.visitNode(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        return object instanceof EndStatement;
    }
}