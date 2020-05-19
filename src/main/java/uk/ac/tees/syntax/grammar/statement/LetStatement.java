package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.grammar.expression.factor.IdentifierFactor;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeNodeVisitor;

import java.util.Objects;

/**
 * LET statement assigns the value of an expression to an identifier.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class LetStatement extends Statement {

    /**
     * The identifier to be assigned the value.
     */
    private final IdentifierFactor identifier;

    /**
     * The value to be assigned to the identifier.
     */
    private final AbstractSyntaxTreeNode value;

    public LetStatement(IdentifierFactor identifier, AbstractSyntaxTreeNode value) {
        super("LET");
        this.identifier = identifier;
        this.value = value;
    }

    public IdentifierFactor getIdentifier() {
        return identifier;
    }

    public AbstractSyntaxTreeNode getValue() {
        return value;
    }

    @Override
    public void accept(AbstractSyntaxTreeNodeVisitor visitor) {
        identifier.accept(visitor);
        value.accept(visitor);

        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), identifier, value);
    }

}