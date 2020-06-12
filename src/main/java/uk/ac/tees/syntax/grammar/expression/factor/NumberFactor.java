package uk.ac.tees.syntax.grammar.expression.factor;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

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

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Num(" + value + ")";
    }

    @Override
    public void accept(AbstractSyntaxTreeVisitor visitor) {
        visitor.visitNode(this);
    }

}