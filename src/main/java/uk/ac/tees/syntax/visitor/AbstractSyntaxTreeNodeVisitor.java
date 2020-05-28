package uk.ac.tees.syntax.visitor;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.grammar.Line;
import uk.ac.tees.syntax.grammar.Program;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.factor.IdentifierFactor;
import uk.ac.tees.syntax.grammar.expression.factor.NumberFactor;
import uk.ac.tees.syntax.grammar.expression.factor.StringLiteral;
import uk.ac.tees.syntax.grammar.statement.*;

/**
 * Implementations of this interface define actions that are executed upon visiting a node of particular types.
 * <p>
 * TODO: needs refactoring, breaks open/closed principle (have to add new functions to add a new node type).
 * <p>
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public interface AbstractSyntaxTreeNodeVisitor<T> {

    /**
     * This function should be called with the root node of a tree, returning the result of visiting the whole tree.
     *
     * @param root the root node of the tree.
     * @return an instance of type T, the result of visiting the whole tree.
     */
    T visitTree(AbstractSyntaxTreeNode root);

    void visit(IdentifierFactor node);

    void visit(NumberFactor node);

    void visit(StringLiteral node);

    void visit(ArithmeticBinaryExpression node);

    void visit(RelationalBinaryExpression node);

    void visit(Program root);

    void visit(Line node);

    void visit(IfStatement node);

    void visit(EndStatement node);

    void visit(GoSubStatement node);

    void visit(GoToStatement node);

    void visit(InputStatement node);

    void visit(LetStatement node);

    void visit(PrintStatement node);

    void visit(ReturnStatement node);

}