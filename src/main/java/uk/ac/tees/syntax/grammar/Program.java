package uk.ac.tees.syntax.grammar;

import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeNodeVisitor;

import java.util.List;
import java.util.Objects;

/**
 *
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

    @Override
    public String toString() {
        return "Program(" + name + ")";
    }

    @Override
    public void accept(AbstractSyntaxTreeNodeVisitor visitor) {
        // Visit this node.
        visitor.visit(this);

        // Accept each of the lines of code.
        lines.forEach(l -> l.accept(visitor));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, lines);
    }

}
