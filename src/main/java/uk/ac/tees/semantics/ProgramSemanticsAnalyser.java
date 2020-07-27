package uk.ac.tees.semantics;

import uk.ac.tees.semantics.exception.InvalidLineNumberException;
import uk.ac.tees.semantics.exception.SemanticException;
import uk.ac.tees.syntax.grammar.Line;
import uk.ac.tees.syntax.grammar.Program;
import uk.ac.tees.syntax.grammar.factor.IdentifierFactor;
import uk.ac.tees.syntax.grammar.statement.*;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;
import uk.ac.tees.syntax.visitor.Visitor;

import java.util.*;

/**
 * Traverses an Abstract Syntax Tree starting, invariably, at a {@link Program} node as the root node. The purpose
 * of this class is to further analyse the source input, in the form of an abstract syntax tree intermediate
 * representation, after it has been parsed.
 * <p>
 * This includes the following verifications:
 * <ul>
 * <li>That line numbers are consecutive multiples of 10.</li>
 * <li>That branch statements point to existing lines.</li>
 * <li>That a gosub statement has a corresponding return statement.</li>
 * <li>That referenced variables have been declared.</li>
 * <li>That a program ends.</li>
 * </ul>
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class ProgramSemanticsAnalyser extends AbstractSyntaxTreeVisitor<Program, Program> {

    /**
     * Stack of line numbers, to verify that lines are numbered properly.
     */
    private final Deque<Integer> lineNumbers = new ArrayDeque<>();

    /**
     * Line numbers that branch statements target.
     */
    private final List<Integer> branchStatementTargets = new ArrayList<>();

    /**
     * {@link Set} of characters that are declared in the input.
     */
    private final Set<Character> identifiers = new HashSet<>();

    /**
     * Denotes that there is an end statement.
     */
    private boolean ends;

    /**
     * Denotes that a return statement is required.
     */
    private boolean requiresReturn;

    /**
     * Checks that GoSub has corresponding return statement and all branch statements target valid line numbers.
     */
    private void verifyBranchStatements() throws SemanticException {
        if (requiresReturn) {
            throw new SemanticException("GoSub statement without return!");
        }

        for (int targetLine : branchStatementTargets) {
            if (!lineNumbers.contains(targetLine)) {
                throw new InvalidLineNumberException("Branch statement directs to non-existent line! " + targetLine);
            }
        }
    }

    @Override
    public Program visitTree(Program root) {
        root.accept(this);

        verifyBranchStatements();

        if (!ends) {
            throw new SemanticException("Missing end statement!");
        }

        return root;
    }

    @Visitor
    private void visit(Line node) {
        if (lineNumbers.contains(node.getLineNumber())) {
            throw new InvalidLineNumberException("Duplicate line numbers! " + node.getLineNumber());
        }

        int previousLine = lineNumbers.isEmpty() ? node.getLineNumber() : lineNumbers.peek();

        if (previousLine > node.getLineNumber()) {
            String message = "Disordered line numbers! The previous line, %d, is larger than the current, %d";

            throw new InvalidLineNumberException(String.format(message, previousLine, node.getLineNumber()));
        }

        if (node.getLineNumber() % 10 != 0) {
            throw new InvalidLineNumberException("Line number is not a multiple of 10! " + node.getLineNumber());
        }

        lineNumbers.push(node.getLineNumber());
    }

    @Visitor
    private void visit(IdentifierFactor node) {
        if (!identifiers.contains(node.getName())) {
            throw new SemanticException("Variable " + node.getName() + " referenced without assignment!");
        }
    }

    @Visitor
    private void visit(EndStatement node) {
        ends = true;
    }

    @Visitor
    private void visit(ReturnStatement node) {
        requiresReturn = false;
    }

    @Visitor
    private void visit(GoSubStatement node) {
        requiresReturn = true;

        branchStatementTargets.add(node.getLineNumber());
    }

    @Visitor
    private void visit(GoToStatement node) {
        branchStatementTargets.add(node.getLineNumber());
    }

    @Visitor
    private void visit(LetStatement node) {
        identifiers.add(node.getIdentifier().getName());
    }

    @Visitor
    private void visit(InputStatement node) {
        node.getIdentifiers().forEach(i -> identifiers.add(i.getName()));
    }
}