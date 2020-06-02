package uk.ac.tees.syntax.grammar;

import uk.ac.tees.syntax.grammar.expression.factor.IdentifierFactor;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeNodeVisitor;

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
    public void accept(AbstractSyntaxTreeNodeVisitor visitor) {
        visitor.visit(this);
    }
}