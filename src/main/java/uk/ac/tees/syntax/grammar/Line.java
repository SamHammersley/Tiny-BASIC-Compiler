package uk.ac.tees.syntax.grammar;

import uk.ac.tees.syntax.grammar.statement.Statement;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeNodeVisitor;

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
     * The statement for the line.
     */
    private final Statement statement;

    public Line(int lineNumber, Statement statement) {
        this.lineNumber = lineNumber;
        this.statement = statement;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public String toString() {
        return "Line(" + lineNumber + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lineNumber, statement);
    }

    @Override
    public void accept(AbstractSyntaxTreeNodeVisitor visitor) {
        visitor.visit(this);

        statement.accept(visitor);
    }
}