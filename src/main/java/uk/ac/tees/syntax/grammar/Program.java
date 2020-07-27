package uk.ac.tees.syntax.grammar;

import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

import java.util.List;
import java.util.Objects;

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
    public <T, K extends AbstractSyntaxTreeNode> void accept(AbstractSyntaxTreeVisitor<T, K> visitor) {
        // Visit this node.
        visitor.visitNode(this);

        // Accept each of the lines of code.
        lines.forEach(l -> l.accept(visitor));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Program)) {
            return false;
        }

        Program other = (Program) object;
        return name.equals(other.name) && lines.equals(other.lines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lines);
    }
}
