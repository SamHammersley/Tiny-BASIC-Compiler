package uk.ac.tees.codegeneration.x86_64;

import uk.ac.tees.optimise.ConstantExpressionEvaluator;
import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.grammar.Line;
import uk.ac.tees.syntax.grammar.Program;
import uk.ac.tees.syntax.grammar.UnassignedIdentifier;
import uk.ac.tees.syntax.grammar.expression.UnaryExpression;
import uk.ac.tees.syntax.grammar.expression.UnaryOperator;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalBinaryExpression;
import uk.ac.tees.syntax.grammar.factor.IdentifierFactor;
import uk.ac.tees.syntax.grammar.factor.NumberFactor;
import uk.ac.tees.syntax.grammar.factor.StringLiteral;
import uk.ac.tees.syntax.grammar.statement.*;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;
import uk.ac.tees.syntax.visitor.Visitor;

import java.util.HashMap;
import java.util.Map;

import static uk.ac.tees.codegeneration.x86_64.X86_64CompilerConstants.*;

/**
 * A {@link AbstractSyntaxTreeVisitor} that compiles an {@link AbstractSyntaxTreeNode} to x86-64 Netwide Assembler
 * assembly code.
 *
 * <p>The program stack is used to evaluate arithmetic expressions. Effectively the expressions are in postfix
 * notation (due to the order in which the {@link AbstractSyntaxTreeNode}s are visited). This facilitates the
 * evaluation of expressions since the operands can be pushed onto the stack and then popped off, followed by the
 * operation/operator. Once evaluated, the value can be pushed onto the stack for use in another operation.</p>
 *
 * <p>Using the stack to evaluate expressions eliminates the need for register management since the two operands for
 * the operation would be at the top of the stack. This makes the compilation process really simple however leads to
 * long sequences of stack operations, repeatedly pushing and popping off the stack.</p>
 *
 * <p>Operands are also pushed onto the stack for such operations as writing to standard output and reading from
 * standard input. In the case of a string operand, the address of the string (label, usually declared in read-memory
 * data section) is passed to the system call.</p>
 *
 * <p>Local variables are allocated 8 bytes that reside below, at uniform intervals (dependant on the variable name),
 * the rbp register. Since variable names are restricted to one uppercase character there may only be 26 (A-Z).</p>
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class X86_64NetwideAssemblyGenerator extends AbstractSyntaxTreeVisitor<String, Program> {

    /**
     * The read-only data section holding string data, with generated labels.
     */
    private final X86_64DataSection dataSection = new X86_64DataSection(X86_64CompilerConstants.DataSectionType.READ_ONLY);

    /**
     * Used to build the assembly code output.
     */
    private final StringBuilder builder = new StringBuilder();

    /**
     * Characters mapped to address offsets, these offsets are from rbp and are reserved for local variables.
     */
    private final Map<Character, Integer> localVariableAddress = new HashMap<>();

    /**
     * The current line of source code.
     */
    private int currentLine;

    /**
     * Denotes whether the decimal to ascii or ascii to decimal subroutines are required.
     */
    private boolean includeAsciiUtil;

    /**
     * Gets the offset from stack frame base pointer for the given identifier.
     *
     * @param identifier the identifier to get the offset for.
     * @return an integer offset, that is taken from rbp register for the address of the given identifier.
     */
    private int getLocalVariableAddressOffset(char identifier) {
        return localVariableAddress.getOrDefault(identifier,
                (1 + localVariableAddress.size()) * Long.BYTES);
    }

    @Override
    public String visitTree(Program root) {
        root.accept(this);

        // this is inserted since data section is not ready before hand.
        builder.insert(0, dataSection);

        int index = builder.indexOf(LOCAL_VAR_RESERVE_PLACE_HOLDER);

        // stack grows down so we sub to reserve enough space on the stack for local variables.
        String reserved = INDENTATION + "sub rsp, " + localVariableAddress.size() * Long.BYTES + "\n";
        builder.replace(index, index + LOCAL_VAR_RESERVE_PLACE_HOLDER.length(), reserved);

        if (includeAsciiUtil) {
            builder.insert(0, INCLUDE_ASCII_UTILS);
        }

        return builder.toString();
    }

    @Visitor
    private void visit(Program root) {
        builder.append("section .text\n")
                .append(INDENTATION).append("global _start\n")
                .append("_start:\n")
                // preserve old stack frame.
                .append(INDENTATION).append("push rbp\n")
                // move rbp to rsp
                .append(INDENTATION).append("mov rbp, rsp\n")
                // stack grows down so we sub to reserve enough space on the stack for 26 local variables,
                // +1 for base pointer at the top of the stack.
                .append(LOCAL_VAR_RESERVE_PLACE_HOLDER);
    }

    @Visitor
    private void visit(EndStatement node) {
        // clear the rax register.
        builder.append(INDENTATION).append("xor rax, rax\n")
                // change the stack pointers back to original values.
                .append(INDENTATION).append("mov rsp, rbp\n")
                .append(INDENTATION).append("pop rbp\n");

        // sys_exit
        systemCall(builder, SYS_EXIT_ID, 0);
    }

    @Visitor
    private void visit(IdentifierFactor node) {
        char identifier = node.getName();
        builder.append(INDENTATION).append("mov rax, [rbp - ")
                .append(localVariableAddress.get(identifier)).append("]\n")
                .append(INDENTATION).append("push rax\n");
    }

    @Visitor
    private void visit(NumberFactor node) {
        builder.append(INDENTATION).append("push ").append(node.getValue()).append('\n');
    }

    @Visitor
    private void visit(StringLiteral node) {
        // convert the string to an array of hex strings (for each character).
        String[] hexArray = X86_64CompilerConstants.stringLiteralCharsToHex(node.getValue());
        String operand = String.join(",", hexArray);

        dataSection.addEntry(operand, "db");
    }

    @Visitor
    private void visit(UnaryExpression node) {
        if (node.getOperator().equals(UnaryOperator.SUB)) {
            builder.append(INDENTATION).append("pop rbx\n")
                    .append(INDENTATION).append("mov rax, 0\n")
                    .append(INDENTATION).append(node.getOperator().name().toLowerCase()).append(" rax, rbx\n")
                    .append(INDENTATION).append("push rax\n");
        }
    }

    @Visitor
    private void visit(ArithmeticBinaryExpression node) {
        X86_64ExpressionCompiler expressionCompiler = new X86_64ExpressionCompiler(this);

        ConstantExpressionEvaluator<String, X86_64ExpressionCompiler> evaluator
                = new ConstantExpressionEvaluator<>(expressionCompiler);

        builder.append(evaluator.visitTree(node));
    }

    @Visitor
    private void visit(Line node) {
        builder.append("_line_").append(node.getLineNumber()).append(":\n");

        currentLine = node.getLineNumber();
    }

    @Visitor
    private void visit(LetStatement node) {
        if (localVariableAddress.size() > MAX_LOCAL_VARIABLE_COUNT) {
            throw new RuntimeException("Too many variables");
        }

        char identifier = node.getIdentifier().getName();
        int addressOffset = getLocalVariableAddressOffset(identifier);

        localVariableAddress.put(identifier, addressOffset);
        builder.append(INDENTATION).append("pop rax\n")
                .append(INDENTATION).append("mov [rbp - ").append(addressOffset).append("], rax\n");
    }

    @Visitor
    private void visit(PrintStatement node) {
        X86_64PrintStatementCompiler printer = new X86_64PrintStatementCompiler(dataSection);

        builder.append(printer.visitTree(node));
        includeAsciiUtil |= printer.shouldConvert();
    }

    @Visitor
    private void visit(RelationalBinaryExpression node) {
        builder.append(INDENTATION).append("pop rax\n")
                .append(INDENTATION).append("pop rbx\n")
                .append(INDENTATION).append("cmp rbx, rax\n");
    }

    @Visitor
    private void visit(IfStatement node) {
        String operation = getJumpOperation(node.getExpression().getOperator().negate());

        builder.append(INDENTATION).append(operation).append(" _line_").append(currentLine + 10).append('\n');
    }

    @Visitor
    private void visit(GoToStatement node) {
        builder.append(INDENTATION).append("jmp _line_").append(node.getLineNumber()).append('\n');
    }

    @Visitor
    private void visit(GoSubStatement node) {
        builder.append(INDENTATION).append("call _line_").append(node.getLineNumber()).append('\n');
    }

    @Visitor
    private void visit(ReturnStatement node) {
        builder.append(INDENTATION).append("ret\n");
    }

    @Visitor
    private void visit(InputStatement node) {
        for (UnassignedIdentifier identifier : node.getIdentifiers()) {
            int addressOffset = getLocalVariableAddressOffset(identifier.getName());

            localVariableAddress.put(identifier.getName(), addressOffset);

            builder.append(INDENTATION).append("lea r8, [rbp - ").append(addressOffset).append("]\n");
            systemCall(builder, SYS_READ_ID, STD_IN_FILE_DESCRIPTOR, "r8", 8);
            builder.append(INDENTATION).append("mov rax, [rbp - ").append(addressOffset).append("]\n");
            builder.append(INDENTATION).append("push rax").append('\n');
            builder.append(INDENTATION).append(CALL_ASCII_DECONVERSION).append('\n');
            builder.append(INDENTATION).append("pop rax").append('\n');
            builder.append(INDENTATION).append("mov [rbp - ").append(addressOffset).append("], rax").append('\n');
        }
        includeAsciiUtil = true;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}