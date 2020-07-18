package uk.ac.tees.syntax.grammar;

import uk.ac.tees.syntax.grammar.factor.IdentifierFactor;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

import java.util.Objects;

/**
 * An {@link AbstractSyntaxTreeNode} representing an identifier for a variable that is to be assigned a value. The
 * distinction between this class and {@link IdentifierFactor} is that {@link IdentifierFactor} is part of an
 * expression and should have already been assigned a value, that is stored in some memory address. Objects of this
 * class may not have been assigned a value yet.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class UnassignedIdentifier extends Identifier {

    public UnassignedIdentifier(char name) {
        super(name);
    }

    public UnassignedIdentifier(String name) {
        super(name.charAt(0));
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

        if (!(object instanceof UnassignedIdentifier)) {
            return false;
        }

        return name == ((UnassignedIdentifier) object).name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}