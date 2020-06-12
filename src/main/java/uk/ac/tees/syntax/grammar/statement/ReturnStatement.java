package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

/**
 * RETURN statement, returns from subroutine to previous instruction.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class ReturnStatement extends Statement {

    public ReturnStatement() {
        super("RETURN");
    }

    @Override
    public void accept(AbstractSyntaxTreeVisitor visitor) {
        visitor.visitNode(this);
    }

}