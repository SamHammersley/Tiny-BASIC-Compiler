package uk.ac.tees.codegeneration.x86_64;

import uk.ac.tees.syntax.grammar.expression.Expression;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticBinaryExpression;
import uk.ac.tees.syntax.grammar.factor.IdentifierFactor;
import uk.ac.tees.syntax.grammar.factor.NumberFactor;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;
import uk.ac.tees.syntax.visitor.Visitor;

import static uk.ac.tees.codegeneration.x86_64.X86_64CompilerConstants.INDENTATION;

public final class X86_64ExpressionCompiler extends AbstractSyntaxTreeVisitor<String, Expression> {

    private final StringBuilder builder = new StringBuilder();

    private final X86_64NetwideAssemblyGenerator x86_64Generator;

    public X86_64ExpressionCompiler(X86_64NetwideAssemblyGenerator x86_64Generator) {
        this.x86_64Generator = x86_64Generator;
    }

    @Override
    public String visitTree(Expression node) {
        visitNode(node);

        return builder.toString();
    }

    @Visitor(types = {NumberFactor.class, IdentifierFactor.class})
    private void visit(Expression node) {
        x86_64Generator.visitNode(node);
    }

    @Visitor
    private void visit(ArithmeticBinaryExpression node) {
        builder.append(INDENTATION).append("pop rbx\n")
                .append(INDENTATION).append("pop rax\n");

        String operatorName = node.getOperator().name().toLowerCase();

        switch (node.getOperator()) {
            case ADD:
            case SUB:
                builder.append(INDENTATION).append(operatorName).append(" rax,");
                break;

            case DIV:
                builder.append(INDENTATION).append("xor rdx, rdx\n");
            case MUL:
                builder.append(INDENTATION).append('i').append(operatorName);
                break;
        }

        builder.append(" rbx\n")
                .append(INDENTATION).append("push rax\n");
    }

}