package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.grammar.UnassignedIdentifier;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

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
    private final List<UnassignedIdentifier> identifiers;

    public InputStatement(List<UnassignedIdentifier> identifiers) {
        super("INPUT");

        this.identifiers = identifiers;
    }

    public List<UnassignedIdentifier> getIdentifiers() {
        return identifiers;
    }

    @Override
    public <T, K extends AbstractSyntaxTreeNode> void accept(AbstractSyntaxTreeVisitor<T, K> visitor) {
        identifiers.forEach(i -> i.accept(visitor));

        visitor.visitNode(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof InputStatement)) {
            return false;
        }

        InputStatement other = (InputStatement) object;

        return identifiers.equals(other.identifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifiers);
    }
}