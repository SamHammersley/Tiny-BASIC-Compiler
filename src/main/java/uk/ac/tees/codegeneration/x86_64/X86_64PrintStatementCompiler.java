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

    /**
     * Denotes whether the ascii conversion util subroutines are required.
     */
    private boolean includeAsciiUtil;

    X86_64PrintStatementCompiler(X86_64DataSection dataSection) {
        this.dataSection = dataSection;
    }

    @Override
    public String visitTree(PrintStatement node) {
        for (AbstractSyntaxTreeNode expression : node.getExpressions()) {
            visitNode(expression);
        }

        return builder.toString();
    }

    public boolean shouldConvert() {
        return includeAsciiUtil;
    }

    @Visitor(types = {NumberFactor.class, IdentifierFactor.class, ArithmeticBinaryExpression.class})
    private void visit(AbstractSyntaxTreeNode node) {
        includeAsciiUtil = true;
        builder.append(INDENTATION).append(CALL_ASCII_CONVERSION).append('\n');
        // syscall expects value/operand in rsp, so we can just push the value onto the stack.
        print("rsp", 8);
        // the value/operand is at the top of the stack, pop into rax and essentially discard.
        builder.append(INDENTATION).append("pop rax\n");
    }

    @Visitor
    private void visit(StringLiteral node) {
        // remove quotation marks and unescape double-escaped characters.
        String[] hexArray = X86_64CompilerConstants.stringLiteralCharsToHex(node.getValue());
        String operand = String.join(",", hexArray);

        print(dataSection.getLabel(operand), hexArray.length);
    }

    /**
     * Calls {@link X86_64CompilerConstants#systemCall} with parameters for writing to standard out,
     *
     * @param address     the address of the bytes to write.
     * @param operandSize the amount of bytes to print.
     */
    private void print(String address, int operandSize) {
        systemCall(builder, SYS_WRITE_ID, STD_OUT_FILE_DESCRIPTOR, address, operandSize);
    }
}