package uk.ac.tees.syntax.grammar.expression.factor;

import uk.ac.tees.syntax.grammar.Identifier;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

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
        visitor.visit(this);
    }

}