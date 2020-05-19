package uk.ac.tees.syntax.grammar.expression.factor;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeNodeVisitor;

import java.util.Objects;

/**
 * A factor that has a numeric value.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class NumberFactor implements AbstractSyntaxTreeNode {

    private final int value;

    public NumberFactor(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Num(" + value + ")";
    }

    @Override
    public void accept(AbstractSyntaxTreeNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

}