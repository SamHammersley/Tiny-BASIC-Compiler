package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.expression.factor.NumberFactor;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

/**
 * GOTO jumps to the given target line.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class GoToStatement extends Statement {

    /**
     * The numerical value of the line to go to.
     */
    private final NumberFactor lineNumber;

    public GoToStatement(NumberFactor lineNumber) {
        super("GOTO");
        this.lineNumber = lineNumber;
    }

    public NumberFactor getLineNumber() {
        return lineNumber;
    }

    @Override
    public void accept(AbstractSyntaxTreeVisitor visitor) {
        lineNumber.accept(visitor);

        visitor.visit(this);
    }

}