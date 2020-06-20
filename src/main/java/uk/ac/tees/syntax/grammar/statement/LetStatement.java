package uk.ac.tees.syntax.grammar.statement;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.grammar.UnassignedIdentifier;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

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
    private final UnassignedIdentifier identifier;

    /**
     * The value to be assigned to the identifier.
     */
    private final AbstractSyntaxTreeNode value;

    public LetStatement(UnassignedIdentifier identifier, AbstractSyntaxTreeNode value) {
        super("LET");
        this.identifier = identifier;
        this.value = value;
    }

    public UnassignedIdentifier getIdentifier() {
        return identifier;
    }

    public AbstractSyntaxTreeNode getValue() {
        return value;
    }

    @Override
    public <T, K extends AbstractSyntaxTreeNode> void accept(AbstractSyntaxTreeVisitor<T, K> visitor) {
        identifier.accept(visitor);
        value.accept(visitor);

        visitor.visitNode(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof LetStatement)) {
            return false;
        }

        LetStatement other = (LetStatement) object;

        return identifier.equals(other.identifier) && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, value);
    }
}