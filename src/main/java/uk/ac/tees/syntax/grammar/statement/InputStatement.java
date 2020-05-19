package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.expression.factor.IdentifierFactor;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeNodeVisitor;

import java.util.List;
import java.util.Objects;

/**
 * Signifies that there should be n numbers input from the user, one for each identifier.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class InputStatement extends Statement {

    /**
     * {@link List} of identifiers to assign values to, from the user.
     */
    private final List<IdentifierFactor> identifiers;

    public InputStatement(List<IdentifierFactor> identifiers) {
        super("INPUT");

        this.identifiers = identifiers;
    }

    public List<IdentifierFactor> getIdentifiers() {
        return identifiers;
    }

    @Override
    public void accept(AbstractSyntaxTreeNodeVisitor visitor) {
        identifiers.forEach(i -> i.accept(visitor));

        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), identifiers);
    }

}