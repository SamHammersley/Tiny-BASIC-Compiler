package uk.ac.tees.syntax.grammar.expression.factor;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

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
    public void accept(AbstractSyntaxTreeVisitor visitor) {
        visitor.visitNode(this);
    }

}