package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.UnassignedIdentifier;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

import java.util.List;

/**
 * Signifies that there should be n numbers input from the user, one for each identifier.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class InputStatement extends Statement {

    /**
     * {@link List} of identifiers to assign values to, from the user.
     */
    private final List<UnassignedIdentifier> identifiers;

    public InputStatement(List<UnassignedIdentifier> identifiers) {
        super("INPUT");

        this.identifiers = identifiers;
    }

    public List<UnassignedIdentifier> getIdentifiers() {
        return identifiers;
    }

    @Override
    public void accept(AbstractSyntaxTreeVisitor visitor) {
        identifiers.forEach(i -> i.accept(visitor));

        visitor.visit(this);
    }

}