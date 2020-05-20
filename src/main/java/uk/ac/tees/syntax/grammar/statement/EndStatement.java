package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeNodeVisitor;

/**
 * An END statement, denotes the end of the program.
 *
 * {@author Sam Hammersley - Gonsalves (q5315908)}
 */
public final class EndStatement extends Statement {

    public EndStatement() {
        super("END");
    }

    @Override
    public void accept(AbstractSyntaxTreeNodeVisitor visitor) {
        visitor.visit(this);
    }

}