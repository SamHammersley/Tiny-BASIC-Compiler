package uk.ac.tees.syntax.grammar;

import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

import java.util.List;

/**
 * Represents a named sequence of lines of Tiny BASIC source code that is referred to as a program.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class Program implements AbstractSyntaxTreeNode {

    /**
     * The name of the program.
     */
    private final String name;

    /**
     * A {@link List} of {@link Line}s that exist in this program.
     */
    private final List<Line> lines;

    public Program(String name, List<Line> lines) {
        this.name = name;
        this.lines = lines;
    }

    /**
     * Accessor method for {@link #lines}.
     *
     * @return the lines of code for this program.
     */
    public List<Line> lines() {
        return lines;
    }

    /**
     * Accessor method for {@link #name}.
     *
     * @return the name of the program.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Program(" + name + ")";
    }

    @Override
    public void accept(AbstractSyntaxTreeVisitor visitor) {
        // Visit this node.
        visitor.visit(this);

        // Accept each of the lines of code.
        lines.forEach(l -> l.accept(visitor));
    }

}
