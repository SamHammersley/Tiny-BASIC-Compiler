package uk.ac.tees.syntax.grammar;

import uk.ac.tees.syntax.grammar.statement.Statement;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

import java.util.Objects;

/**
 * Represents a line of Tiny BASIC source code, the structure of which is defined by the following:
 *
 * <pre>{@code <line> ::= <number> <statement> LF}</pre>
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class Line implements AbstractSyntaxTreeNode {

    /**
     * The line number, multiple of 10.
     */
    private final int lineNumber;

    /**
     * The statement for the line, there will always exist a statement on every line of source code.
     */
    private final Statement statement;

    public Line(int lineNumber, Statement statement) {
        this.lineNumber = lineNumber;
        this.statement = statement;
    }

    /**
     * Accessor method for {@link #statement} field.
     *
     * @return the statement for this line.
     */
    public Statement getStatement() {
        return statement;
    }

    /**
     * Accessor method for {@link #lineNumber} field.
     *
     * @return the line number of this line.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return "Line(" + lineNumber + ")";
    }

    @Override
    public <T, K extends AbstractSyntaxTreeNode> void accept(AbstractSyntaxTreeVisitor<T, K> visitor) {
        visitor.visitNode(this);

        statement.accept(visitor);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Line)) {
            return false;
        }
        Line other = (Line) object;

        return lineNumber == other.lineNumber && statement.equals(other.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNumber, statement);
    }
}