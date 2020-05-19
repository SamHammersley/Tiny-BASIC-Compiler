package uk.ac.tees.syntax.grammar.expression.factor;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeNodeVisitor;

import java.util.Objects;

/**
 * A factor that has an identifier.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class IdentifierFactor implements AbstractSyntaxTreeNode {

    private final String identifier;

    public IdentifierFactor(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "Var(" + identifier + ")";
    }

    @Override
    public void accept(AbstractSyntaxTreeNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), identifier);
    }

}