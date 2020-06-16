package uk.ac.tees.syntax.grammar.expression.factor;

import uk.ac.tees.syntax.grammar.Identifier;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

import java.util.Objects;

/**
 * An {@link Identifier} that is a factor of an expression; this means objects of this type must be assigned a value.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class IdentifierFactor extends Identifier {

    public IdentifierFactor(char name) {
        super(name);
    }

    public IdentifierFactor(String name) {
        super(name.charAt(0));
    }

    @Override
    public void accept(AbstractSyntaxTreeVisitor visitor) {
        visitor.visitNode(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof IdentifierFactor)) {
            return false;
        }

        return name == ((IdentifierFactor) object).name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}