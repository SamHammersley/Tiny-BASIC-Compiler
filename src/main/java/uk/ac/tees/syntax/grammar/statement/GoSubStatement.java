package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

/**
 * GOSUB statement, similar to {@link GoToStatement} branch statement however previous instruction pointer is
 * tracked and therefore upon calling RETURN from the GOSUB subroutine, the current instruction pointer will return
 * to where it was previously, before GOSUB.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class GoSubStatement extends Statement {

    /**
     * The numerical value of the line to go to.
     */
    private final int lineNumber;

    public GoSubStatement(int lineNumber) {
        super("GOSUB");
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public void accept(AbstractSyntaxTreeVisitor visitor) {
        visitor.visitNode(this);
    }

}