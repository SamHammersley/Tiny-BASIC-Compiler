package uk.ac.tees.syntax.grammar;

/**
 * {@link AbstractSyntaxTreeNode} representing a variable identifier in Tiny BASIC source code.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public abstract class Identifier implements AbstractSyntaxTreeNode {

    /**
     * The name of the identifier.
     */
    protected final char name;

    protected Identifier(char name) {
        this.name = name;
    }

    public char getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Var(" + name + ")";
    }

}