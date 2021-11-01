package uk.ac.tees.x86_64;

import org.junit.jupiter.api.Test;
import uk.ac.tees.codegeneration.x86_64.X86_64NetwideAssemblyGenerator;
import uk.ac.tees.syntax.grammar.Line;
import uk.ac.tees.syntax.grammar.Program;
import uk.ac.tees.syntax.grammar.UnassignedIdentifier;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticOperator;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalOperator;
import uk.ac.tees.syntax.grammar.factor.IdentifierFactor;
import uk.ac.tees.syntax.grammar.factor.NumberFactor;
import uk.ac.tees.syntax.grammar.factor.StringLiteral;
import uk.ac.tees.syntax.grammar.statement.*;
import uk.ac.tees.syntax.graph.GraphDescriptionVisitor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class X86_64NetwideAssemblyGeneratorTest {

    private static final String ASSEMBLY_OUTPUT =
            """
                    %include "ascii_util.asm"
                    section .rodata

                    section .text
                        global _start
                    _start:
                        push rbp
                        mov rbp, rsp
                        sub rsp, 8
                    _line_10:
                        push 6
                        pop rax
                        mov [rbp - 8], rax
                    _line_20:
                        mov rax, [rbp - 8]
                        push rax
                        mov rax, [rbp - 8]
                        push rax
                        pop rbx
                        pop rax
                        imul rbx
                        push rax
                        mov rax, [rbp - 8]
                        push rax
                        push 1
                        pop rbx
                        pop rax
                        sub rax, rbx
                        push rax
                        push 10
                        pop rbx
                        pop rax
                        imul rbx
                        push rax
                        pop rbx
                        pop rax
                        add rax, rbx
                        push rax
                        call decimal_to_ascii
                        mov rax, 1
                        mov rdi, 1
                        mov rsi, rsp
                        mov rdx, 8
                        syscall
                        pop rax
                    _line_30:
                        mov rax, [rbp - 8]
                        push rax
                        mov rax, [rbp - 8]
                        push rax
                        pop rax
                        pop rbx
                        cmp rbx, rax
                        jle _line_40
                        xor rax, rax
                        mov rsp, rbp
                        pop rbp
                        mov rax, 60
                        mov rdi, 0
                        syscall
                    _line_40:
                        xor rax, rax
                        mov rsp, rbp
                        pop rbp
                        mov rax, 60
                        mov rdi, 0
                        syscall
                    """;

    private Program manualAbstractSyntaxTree() {
        // B * B (6 * 6)
        ArithmeticBinaryExpression e1Left = new ArithmeticBinaryExpression(
                new IdentifierFactor('B'), new IdentifierFactor('B'), ArithmeticOperator.MUL);

        // B - 1 (6 - 1)
        ArithmeticBinaryExpression e2Left = new ArithmeticBinaryExpression(
                new IdentifierFactor('B'), new NumberFactor(1), ArithmeticOperator.SUB);

        // e2Left * 10 (5 * 10)
        ArithmeticBinaryExpression e1Right = new ArithmeticBinaryExpression(e2Left, new NumberFactor(10), ArithmeticOperator.MUL);

        // e1Left + e1Right (36 + 50)
        ArithmeticBinaryExpression printExpression = new ArithmeticBinaryExpression(e1Left, e1Right, ArithmeticOperator.ADD);

        RelationalBinaryExpression relExp = new RelationalBinaryExpression(new IdentifierFactor('B'),
                new IdentifierFactor('B'), RelationalOperator.GREATER);

        Line line1 = new Line(10, new LetStatement(new UnassignedIdentifier('B'), new NumberFactor(6)));
        Line line2 = new Line(20, new PrintStatement(printExpression));
        Line line3 = new Line(30, new IfStatement(relExp, new EndStatement()));
        Line line4 = new Line(40, new EndStatement());

        List<Line> lines = List.of(line1, line2, line3, line4);

        return new Program("test", lines);
    }

    @Test
    void testCompile() {
        X86_64NetwideAssemblyGenerator compiler = new X86_64NetwideAssemblyGenerator();
        String assembly = compiler.visitTree(manualAbstractSyntaxTree());

        assertEquals(ASSEMBLY_OUTPUT, assembly);
    }

    @Test
    void testInputStatement() {
        final String expectedOutput =
                """
                            lea r8, [rbp - 8]
                            mov rax, 0
                            mov rdi, 0
                            mov rsi, r8
                            mov rdx, 8
                            syscall
                            mov rax, [rbp - 8]
                            push rax
                            call ascii_to_decimal
                            pop rax
                            mov [rbp - 8], rax
                            lea r8, [rbp - 16]
                            mov rax, 0
                            mov rdi, 0
                            mov rsi, r8
                            mov rdx, 8
                            syscall
                            mov rax, [rbp - 16]
                            push rax
                            call ascii_to_decimal
                            pop rax
                            mov [rbp - 16], rax
                        """;

        X86_64NetwideAssemblyGenerator compiler = new X86_64NetwideAssemblyGenerator();
        List<UnassignedIdentifier> ids = List.of(
                new UnassignedIdentifier('N'), new UnassignedIdentifier('O'));

        new InputStatement(ids).accept(compiler);

        assertEquals(expectedOutput, compiler.toString());
    }

    @Test
    void testGoSubStatement() {
        final String expectedOutput = "    call _line_20\n    ret\n";

        X86_64NetwideAssemblyGenerator compiler = new X86_64NetwideAssemblyGenerator();
        GoSubStatement gosubStatement = new GoSubStatement(20);
        ReturnStatement returnStatement = new ReturnStatement();

        gosubStatement.accept(compiler);
        returnStatement.accept(compiler);

        assertEquals(expectedOutput, compiler.toString());
    }
}