package uk.ac.tees.codegeneration.x86_64;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticBinaryExpression;
import uk.ac.tees.syntax.grammar.factor.IdentifierFactor;
import uk.ac.tees.syntax.grammar.factor.NumberFactor;
import uk.ac.tees.syntax.grammar.factor.StringLiteral;
import uk.ac.tees.syntax.grammar.statement.PrintStatement;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;
import uk.ac.tees.syntax.visitor.Visitor;

import static uk.ac.tees.codegeneration.x86_64.X86_64CompilerConstants.*;
import static uk.ac.tees.codegeneration.x86_64.X86_64CompilerConstants.STD_OUT_FILE_DESCRIPTOR;

/**
 * An {@link AbstractSyntaxTreeVisitor} for compiling {@link PrintStatement}s,
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class X86_64PrintStatementCompiler extends AbstractSyntaxTreeVisitor<String, PrintStatement> {

    /**
     * {@link X86_64DataSection} section for access to string data labels.
     */
    private final X86_64DataSection dataSection;

    /**
     * {@link StringBuilder} to build the output for {@link PrintStatement}s.
     */
    private final StringBuilder builder = new StringBuilder();

    X86_64PrintStatementCompiler(X86_64DataSection dataSection) {
        this.dataSection = dataSection;
    }

    @Override
    public String visitTree(PrintStatement node) {
        visitNode(node.getExpression());

        return builder.toString();
    }

    @Visitor(types = {NumberFactor.class, IdentifierFactor.class, ArithmeticBinaryExpression.class})
    private void visit(AbstractSyntaxTreeNode node) {
        builder.append(INDENTATION).append("call ascii_conversion\n");
        print("rsp", 8);
        builder.append(INDENTATION).append("pop rax\n");
    }

    @Visitor
    private void visit(StringLiteral node) {
        String operand = node.getValue();
        String label = dataSection.getLabel(operand);
        // -2 for the quotation marks
        int operandSize = operand.length() - 2;
        print(label, operandSize);
    }

    /**
     * Calls {@link X86_64CompilerConstants#systemCall} with parameters for writing to standard out,
     *
     * @param address the address of the bytes to write.
     * @param operandSize the amount of bytes to print.
     */
    private void print(String address, int operandSize) {
        systemCall(builder, SYS_WRITE_ID, STD_OUT_FILE_DESCRIPTOR, address, operandSize);

        systemCall(builder, SYS_WRITE_ID, STD_OUT_FILE_DESCRIPTOR, "new_line_char", 1);
    }
}