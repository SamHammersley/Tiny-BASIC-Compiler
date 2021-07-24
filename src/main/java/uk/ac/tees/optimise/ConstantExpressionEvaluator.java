package uk.ac.tees.optimise;

import uk.ac.tees.syntax.grammar.expression.Expression;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticOperator;
import uk.ac.tees.syntax.grammar.factor.NumberFactor;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

public final class ConstantExpressionEvaluator<T, V extends AbstractSyntaxTreeVisitor<T, Expression>>
        extends AbstractSyntaxTreeVisitor<T, ArithmeticBinaryExpression> {

    private final V visitor;

    public ConstantExpressionEvaluator(V visitor) {
        this.visitor = visitor;
    }

    private NumberFactor applyOperation(NumberFactor left, NumberFactor right, ArithmeticOperator operator) {
        switch (operator) {
            case ADD: return new NumberFactor(left.getValue() + right.getValue());
            case SUB: return new NumberFactor(left.getValue() - right.getValue());
            case MUL: return new NumberFactor(left.getValue() * right.getValue());
            case DIV: return new NumberFactor(left.getValue() / right.getValue());
        }
        return null;
    }

    private Expression evaluate(ArithmeticBinaryExpression expression) {
        Expression l = expression.getLeft();
        if (l instanceof ArithmeticBinaryExpression) {
            l = evaluate((ArithmeticBinaryExpression) l);
        }

        Expression r = expression.getRight();
        if (r instanceof ArithmeticBinaryExpression) {
            r = evaluate((ArithmeticBinaryExpression) r);
        }

        if (l instanceof NumberFactor && r instanceof NumberFactor) {
            return applyOperation((NumberFactor) l, (NumberFactor) r, expression.getOperator());
        }

        return expression;
    }

    @Override
    public T visitTree(ArithmeticBinaryExpression rootNode) {
        return visitor.visitTree(evaluate(rootNode));
    }
}