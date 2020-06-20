package uk.ac.tees.syntax.grammar.expression.factor;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

import java.util.Objects;

/**
 * Represents a string literal factor, a string of characters surrounded by "".
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class StringLiteral implements AbstractSyntaxTreeNode {

    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "String(" + value + ")";
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
        if (!(object instanceof StringLiteral)) {
            return false;
        }

        return value.equals(((StringLiteral) object).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}