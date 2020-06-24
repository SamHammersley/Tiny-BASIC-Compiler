package uk.ac.tees.x86_64;

import org.junit.jupiter.api.Test;
import uk.ac.tees.codegeneration.x86_64.X86_64NetwideAssemblyCompiler;
import uk.ac.tees.syntax.grammar.Line;
import uk.ac.tees.syntax.grammar.Program;
import uk.ac.tees.syntax.grammar.UnassignedIdentifier;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticOperator;
import uk.ac.tees.syntax.grammar.expression.factor.IdentifierFactor;
import uk.ac.tees.syntax.grammar.expression.factor.NumberFactor;
import uk.ac.tees.syntax.grammar.expression.factor.StringLiteral;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalOperator;
import uk.ac.tees.syntax.grammar.statement.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class X86_64NetwideAssemblyCompilerTest {

    private Program manualAbstractSyntaxTree() {
        // B * B (6 * 6)
        ArithmeticBinaryExpression e1Left = new ArithmeticBinaryExpression(
                new IdentifierFactor('B'), new IdentifierFactor('B'), ArithmeticOperator.MUL);

        // B - 1 (6 - 1)
        ArithmeticBinaryExpression e2Left = new ArithmeticBinaryExpression(
                new IdentifierFactor('B'), new NumberFactor(1), ArithmeticOperator.SUB);

        // e2Left * 10 (5 * 10)
        ArithmeticBinaryExpression e1Right = new ArithmeticBinaryExpression(e2Left,  new NumberFactor(10), ArithmeticOperator.MUL);

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

    private static final String ASSEMBLY_OUTPUT =
        "section .rodata\n" +
                "    new_line_char: db 0x0A\n" +
                "\n" +
                "section .text\n" +
                "    global _start\n" +
                "_start:\n" +
                "    push rbp\n" +
                "    mov rbp, rsp\n" +
                "    sub rsp, 8\n" +
                "_line_10:\n" +
                "    push 6\n" +
                "    pop rax\n" +
                "    mov [rbp - 8], rax\n" +
                "_line_20:\n" +
                "    mov rax, [rbp - 8]\n" +
                "    push rax\n" +
                "    mov rax, [rbp - 8]\n" +
                "    push rax\n" +
                "    pop rbx\n" +
                "    pop rax\n" +
                "    imul rbx\n" +
                "    push rax\n" +
                "    mov rax, [rbp - 8]\n" +
                "    push rax\n" +
                "    push 1\n" +
                "    pop rbx\n" +
                "    pop rax\n" +
                "    sub rax, rbx\n" +
                "    push rax\n" +
                "    push 10\n" +
                "    pop rbx\n" +
                "    pop rax\n" +
                "    imul rbx\n" +
                "    push rax\n" +
                "    pop rbx\n" +
                "    pop rax\n" +
                "    add rax, rbx\n" +
                "    push rax\n" +
                "    call ascii_conversion\n" +
                "    mov rax, 1\n" +
                "    mov rdi, 1\n" +
                "    mov rsi, rsp\n" +
                "    mov rdx, 8\n" +
                "    syscall\n" +
                "    mov rax, 1\n" +
                "    mov rdi, 1\n" +
                "    mov rsi, new_line_char\n" +
                "    mov rdx, 1\n" +
                "    syscall\n" +
                "    pop rax\n" +
                "_line_30:\n" +
                "    mov rax, [rbp - 8]\n" +
                "    push rax\n" +
                "    mov rax, [rbp - 8]\n" +
                "    push rax\n" +
                "    pop rax\n" +
                "    pop rbx\n" +
                "    cmp rbx, rax\n" +
                "    jle _line_40\n" +
                "    mov rax, 0\n" +
                "    mov rsp, rbp\n" +
                "    pop rbp\n" +
                "    mov rax, 60\n" +
                "    mov rdi, 0\n" +
                "    syscall\n" +
                "_line_40:\n" +
                "    mov rax, 0\n" +
                "    mov rsp, rbp\n" +
                "    pop rbp\n" +
                "    mov rax, 60\n" +
                "    mov rdi, 0\n" +
                "    syscall\n" +
                "ascii_conversion:\n" +
                "    pop r8\n" +
                "    pop rax\n" +
                "    mov r9, 10\n" +
                "    mov r10, 0\n" +
                "add_ascii_offset_loop:\n" +
                "    xor rdx, rdx\n" +
                "    idiv r9\n" +
                "    add rdx, 48\n" +
                "    or r10, rdx\n" +
                "    shl r10, 8\n" +
                "    cmp rax, 0\n" +
                "    jne add_ascii_offset_loop\n" +
                "    push r10\n" +
                "    push r8\n" +
                "    ret";

    @Test
    void testCompile() {
        X86_64NetwideAssemblyCompiler compiler = new X86_64NetwideAssemblyCompiler();
        String assembly = compiler.visitTree(manualAbstractSyntaxTree());

        assertEquals(assembly, ASSEMBLY_OUTPUT);
    }

    @Test
    void testInputStatement() {
        final String expectedOutput =
                "    lea r8, [rbp - 8]\n" +
                "    mov rax, 0\n" +
                "    mov rdi, 0\n" +
                "    mov rsi, r8\n" +
                "    mov rdx, 8\n" +
                "    syscall\n" +
                "    lea r8, [rbp - 16]\n" +
                "    mov rax, 0\n" +
                "    mov rdi, 0\n" +
                "    mov rsi, r8\n" +
                "    mov rdx, 8\n" +
                "    syscall\n";

        X86_64NetwideAssemblyCompiler compiler = new X86_64NetwideAssemblyCompiler();
        List<UnassignedIdentifier> ids = List.of(
                new UnassignedIdentifier('N'), new UnassignedIdentifier('O'));

        new InputStatement(ids).accept(compiler);

        assertEquals(expectedOutput, compiler.toString());
    }

    @Test
    void testCompoundPrintStatement() {
        final String expectedOutput =
                "    push 5\n" +
                "    call ascii_conversion\n" +
                "    mov rax, 1\n" +
                "    mov rdi, 1\n" +
                "    mov rsi, rsp\n" +
                "    mov rdx, 8\n" +
                "    syscall\n" +
                "    mov rax, 1\n" +
                "    mov rdi, 1\n" +
                "    mov rsi, new_line_char\n" +
                "    mov rdx, 1\n" +
                "    syscall\n" +
                "    pop rax\n" +
                "    mov rax, [rbp - null]\n" +
                "    push rax\n" +
                "    call ascii_conversion\n" +
                "    mov rax, 1\n" +
                "    mov rdi, 1\n" +
                "    mov rsi, rsp\n" +
                "    mov rdx, 8\n" +
                "    syscall\n" +
                "    mov rax, 1\n" +
                "    mov rdi, 1\n" +
                "    mov rsi, new_line_char\n" +
                "    mov rdx, 1\n" +
                "    syscall\n" +
                "    pop rax\n" +
                "    mov rax, 1\n" +
                "    mov rdi, 1\n" +
                "    mov rsi, rodata0\n" +
                "    mov rdx, 7\n" +
                "    syscall\n" +
                "    mov rax, 1\n" +
                "    mov rdi, 1\n" +
                "    mov rsi, new_line_char\n" +
                "    mov rdx, 1\n" +
                "    syscall\n";

        X86_64NetwideAssemblyCompiler compiler = new X86_64NetwideAssemblyCompiler();

        CompoundPrintStatement statement = new CompoundPrintStatement();
        statement.addExpression(new NumberFactor(5));
        statement.addExpression(new IdentifierFactor('X'));
        statement.addExpression(new StringLiteral("Test Test"));
        statement.accept(compiler);

        assertEquals(expectedOutput, compiler.toString());
    }

    @Test
    void testGoSubStatement() {
        final String expectedOutput = "    call _line_20\n    ret\n";

        X86_64NetwideAssemblyCompiler compiler = new X86_64NetwideAssemblyCompiler();
        GoSubStatement gosubStatement = new GoSubStatement(20);
        ReturnStatement returnStatement = new ReturnStatement();

        gosubStatement.accept(compiler);
        returnStatement.accept(compiler);

        assertEquals(expectedOutput, compiler.toString());
    }
}