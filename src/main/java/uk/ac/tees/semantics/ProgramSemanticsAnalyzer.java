package uk.ac.tees.semantics;

import uk.ac.tees.syntax.grammar.Line;
import uk.ac.tees.syntax.grammar.Program;
import uk.ac.tees.syntax.grammar.UnassignedIdentifier;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.factor.IdentifierFactor;
import uk.ac.tees.syntax.grammar.expression.factor.NumberFactor;
import uk.ac.tees.syntax.grammar.expression.factor.StringLiteral;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalBinaryExpression;
import uk.ac.tees.syntax.grammar.statement.*;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

import java.util.*;

/**
 * Traverses an Abstract Syntax Tree starting, invariably, at a {@link Program} node as the root node. The purpose
 * of this class is to further analyse the source input, in the form of an abstract syntax tree intermediate
 * representation, after it has been parsed.
 *
 * <p>This includes the following verifications:
 * <l>
 *    <li>That line numbers are consecutive multiples of 10.</li>
 *    <li>That branch statements point to existing lines.</li>
 *    <li>That a gosub statement has a corresponding return statement.</li>
 *    <li>That referenced variables have been declared.</li>
 *    <li>That a program ends.</li>
 * </l>
 * </p>
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class ProgramSemanticsAnalyzer implements AbstractSyntaxTreeVisitor<Program, Program> {

    private final Deque<Integer> lineNumbers = new ArrayDeque<>();

    private final List<Integer> branchStatementTargets = new ArrayList<>();

    private final Set<Character> identifiers = new HashSet<>();

    private boolean ends;

    private boolean requiresReturn;

    @Override
    public Program visitTree(Program root) {
        root.accept(this);

        verifyBranchStatements();

        if (!ends) {
            throw new RuntimeException("No end statement!");
        }

        return root;
    }

    private void verifyBranchStatements() {
        if (requiresReturn) {
            throw new RuntimeException("GoSub statement without return!");
        }

        for (int targetLine : branchStatementTargets) {
            if (!lineNumbers.contains(targetLine)) {
                throw new RuntimeException("Branch statement directs to non-existent line!");
            }
        }
    }

    @Override
    public void visit(Line node) {
        if (lineNumbers.contains(node.getLineNumber())) {
            throw new RuntimeException("Duplicate line numbers!");
        }

        int previousLine = lineNumbers.isEmpty() ? node.getLineNumber() : lineNumbers.peek();

        if (previousLine > node.getLineNumber()) {
            throw new RuntimeException("Disordered line numbers!");
        }

        if (node.getLineNumber() % 10 != 0) {
            throw new RuntimeException("Line number is not a multiple of 10!");
        }

        lineNumbers.push(node.getLineNumber());
    }

    @Override
    public void visit(IdentifierFactor node) {
        if (!identifiers.contains(node.getName())) {
            throw new RuntimeException("Variable referenced without INPUT or LET statement!");
        }
    }

    @Override
    public void visit(EndStatement node) {
        ends = true;
    }

    @Override
    public void visit(ReturnStatement node) {
        requiresReturn = false;
    }

    @Override
    public void visit(GoSubStatement node) {
        requiresReturn = true;

        branchStatementTargets.add(node.getLineNumber().getValue());
    }

    @Override
    public void visit(GoToStatement node) {
        branchStatementTargets.add(node.getLineNumber().getValue());
    }

    @Override
    public void visit(LetStatement node) {
        identifiers.add(node.getIdentifier().getName());
    }

    @Override
    public void visit(InputStatement node) {
        node.getIdentifiers().forEach(i -> identifiers.add(i.getName()));
    }

    @Override
    public void visit(Program root) {
    }

    @Override
    public void visit(UnassignedIdentifier node) {

    }

    @Override
    public void visit(NumberFactor node) {

    }

    @Override
    public void visit(StringLiteral node) {

    }

    @Override
    public void visit(ArithmeticBinaryExpression node) {

    }

    @Override
    public void visit(RelationalBinaryExpression node) {

    }

    @Override
    public void visit(IfStatement node) {

    }

    @Override
    public void visit(PrintStatement node) {

    }

    @Override
    public void visit(CompoundPrintStatement node) {

    }

}