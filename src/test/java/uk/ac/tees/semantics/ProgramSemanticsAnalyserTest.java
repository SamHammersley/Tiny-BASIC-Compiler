package uk.ac.tees.semantics;

import org.junit.jupiter.api.Test;
import uk.ac.tees.semantics.exception.InvalidLineNumberException;
import uk.ac.tees.semantics.exception.SemanticException;
import uk.ac.tees.syntax.grammar.Line;
import uk.ac.tees.syntax.grammar.Program;
import uk.ac.tees.syntax.grammar.UnassignedIdentifier;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticOperator;
import uk.ac.tees.syntax.grammar.factor.IdentifierFactor;
import uk.ac.tees.syntax.grammar.factor.NumberFactor;
import uk.ac.tees.syntax.grammar.statement.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class ProgramSemanticsAnalyserTest {

    private Program manualAbstractSyntaxTree() {
        Line line1 = new Line(10, new LetStatement(new UnassignedIdentifier('B'), new NumberFactor(6)));

        // B * B
        ArithmeticBinaryExpression e1Left = new ArithmeticBinaryExpression(
                new IdentifierFactor('B'), new IdentifierFactor('B'), ArithmeticOperator.MUL);

        // B - 1
        ArithmeticBinaryExpression e2Left = new ArithmeticBinaryExpression(
                new IdentifierFactor('B'), new NumberFactor(1), ArithmeticOperator.SUB);

        // e2Left * 10
        ArithmeticBinaryExpression e1Right = new ArithmeticBinaryExpression(e2Left,  new NumberFactor(10), ArithmeticOperator.MUL);

        // e1Left + e1Right
        ArithmeticBinaryExpression printExpression = new ArithmeticBinaryExpression(e1Left, e1Right, ArithmeticOperator.ADD);

        Line line2 = new Line(20, new PrintStatement(printExpression));

        Line line3 = new Line(30, new EndStatement());

        List<Line> lines = List.of(line1, line2, line3);

        return new Program("test", lines);
    }

    @Test
    void testVisitTree() {
        ProgramSemanticsAnalyser analyser = new ProgramSemanticsAnalyser();

        Program analyzed = analyser.visitTree(manualAbstractSyntaxTree());

        assertEquals(manualAbstractSyntaxTree(), analyzed);
    }

    @Test
    void testThrowsNoReturn() {
        ProgramSemanticsAnalyser analyser = new ProgramSemanticsAnalyser();

        List<Line> lines = List.of(new Line(10, new GoSubStatement(10)),
                new Line(20, new EndStatement()));
        Program program = new Program("throws no return", lines);
        String errorMessage = assertThrows(SemanticException.class, () -> analyser.visitTree(program)).getMessage();
        assertEquals("GoSub statement without return!", errorMessage);
    }

    @Test
    void testThrowsInvalidBranchTarget() {
        ProgramSemanticsAnalyser analyser = new ProgramSemanticsAnalyser();

        List<Line> lines = List.of(new Line(10, new GoSubStatement(15)), new Line(20, new ReturnStatement()));
        Program program = new Program("invalid branch target", lines);
        String errorMessage = assertThrows(SemanticException.class, () -> analyser.visitTree(program)).getMessage();
        assertEquals("Branch statement directs to non-existent line! 15", errorMessage);
    }

    @Test
    void testThrowsMissingEnd() {
        ProgramSemanticsAnalyser analyser = new ProgramSemanticsAnalyser();

        List<Line> lines = List.of(new Line(10, new ReturnStatement()));
        Program program = new Program("throws missing end", lines);
        String errorMessage = assertThrows(SemanticException.class, () -> analyser.visitTree(program)).getMessage();
        assertEquals("Missing end statement!", errorMessage);
    }

    @Test
    void testThrowsDuplicateLineNumbers() {
        ProgramSemanticsAnalyser analyser = new ProgramSemanticsAnalyser();

        List<Line> lines = List.of(new Line(10, new ReturnStatement()), new Line(10, new ReturnStatement()));
        Program program = new Program("throws duplicate line numbers", lines);
        String errorMessage = assertThrows(InvalidLineNumberException.class, () -> analyser.visitTree(program)).getMessage();
        assertEquals("Duplicate line numbers! 10", errorMessage);
    }

    @Test
    void testThrowsDisorderedLineNumbers() {
        ProgramSemanticsAnalyser analyser = new ProgramSemanticsAnalyser();

        List<Line> lines = List.of(new Line(50, new ReturnStatement()), new Line(10, new ReturnStatement()));
        Program program = new Program("throws disordered line numbers", lines);
        String errorMessage = assertThrows(InvalidLineNumberException.class, () -> analyser.visitTree(program)).getMessage();
        assertEquals("Disordered line numbers! The previous line, 50, is larger than the current, 10", errorMessage);
    }

    @Test
    void testThrowsInvalidLineNumber() {
        ProgramSemanticsAnalyser analyser = new ProgramSemanticsAnalyser();

        List<Line> lines = List.of(new Line(1, new ReturnStatement()));
        Program program = new Program("throws line number not multiple of ten", lines);
        String errorMessage = assertThrows(InvalidLineNumberException.class, () -> analyser.visitTree(program)).getMessage();
        assertEquals("Line number is not a multiple of 10! 1", errorMessage);
    }

    @Test
    void testUndeclaredVariableReference() {
        ProgramSemanticsAnalyser analyser = new ProgramSemanticsAnalyser();

        List<Line> lines = List.of(new Line(10, new PrintStatement(new IdentifierFactor('X'))));
        Program program = new Program("throws undeclared variable reference", lines);
        String errorMessage = assertThrows(SemanticException.class, () -> analyser.visitTree(program)).getMessage();
        assertEquals("Variable X referenced without assignment!", errorMessage);
    }

}