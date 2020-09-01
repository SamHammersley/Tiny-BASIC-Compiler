package uk.ac.tees.syntax.graph;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.grammar.Line;
import uk.ac.tees.syntax.grammar.Program;
import uk.ac.tees.syntax.grammar.UnassignedIdentifier;
import uk.ac.tees.syntax.grammar.expression.BinaryExpression;
import uk.ac.tees.syntax.grammar.expression.BinaryOperator;
import uk.ac.tees.syntax.grammar.expression.UnaryExpression;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalBinaryExpression;
import uk.ac.tees.syntax.grammar.factor.IdentifierFactor;
import uk.ac.tees.syntax.grammar.factor.NumberFactor;
import uk.ac.tees.syntax.grammar.factor.StringLiteral;
import uk.ac.tees.syntax.grammar.statement.*;
import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;
import uk.ac.tees.syntax.visitor.Visitor;

/**
 * An {@link AbstractSyntaxTreeVisitor} that creates a DOT graph description for a given Abstract Syntax Tree. DOT
 * is a graph description language whereby described graphs can be visualised using Graphviz software.
 * <p>
 * A graph description can be generated for any Abstract Syntax Tree or sub-tree of an Abstract Syntax Tree.
 *
 * <p>
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 * @see <a href="https://graphviz.gitlab.io/_pages/doc/info/lang.html">DOT Language</a>
 * <p>
 */
public final class GraphDescriptionVisitor extends AbstractSyntaxTreeVisitor<String, AbstractSyntaxTreeNode> {

    /**
     * The name of the graph, this is typically the name given to the program.
     */
    private final String graphName;

    /**
     * {@link GraphDescription} to build the graph description.
     */
    private final GraphDescription graphDescription;

    /**
     * Constructs a {@link GraphDescriptionVisitor} with a given outputFile and graphName. If no outputFile is given,
     * the graph description produced will not be persisted in a file.
     *
     * @param graphName the name of the graph (typically the name of the program).
     */
    public GraphDescriptionVisitor(String graphName) {
        this.graphName = graphName;
        this.graphDescription = new GraphDescription();
    }

    @Override
    public String visitTree(AbstractSyntaxTreeNode root) {
        root.accept(this);

        return graphDescription.build(graphName);
    }

    @Visitor(types = {
            UnassignedIdentifier.class, IdentifierFactor.class,
            NumberFactor.class, StringLiteral.class,
            ReturnStatement.class, EndStatement.class
    })
    private void visit(AbstractSyntaxTreeNode node) {
        graphDescription.add(node);
    }

    @Visitor
    private void visit(UnaryExpression node) {
        graphDescription.add(node);
        graphDescription.associate(node, node.getExpression());
    }

    @Visitor(types = {ArithmeticBinaryExpression.class, RelationalBinaryExpression.class})
    private <T extends BinaryOperator> void visit(BinaryExpression<T> node) {
        graphDescription.add(node);
        graphDescription.associate(node, node.getLeft());
        graphDescription.associate(node, node.getRight());
    }

    @Visitor
    private void visit(Program node) {
        graphDescription.add(node);

        for (Line line : node.lines()) {
            graphDescription.associate(node, line.getStatement());
        }
    }

    @Visitor
    private void visit(IfStatement node) {
        graphDescription.add(node);
        graphDescription.associate(node, node.getExpression());
        graphDescription.associate(node, node.getStatement());
    }

    @Visitor
    private void visit(GoSubStatement node) {
        graphDescription.add(node);

        NumberFactor temp = new NumberFactor(node.getLineNumber());
        visit(temp);

        graphDescription.associate(node, temp);
    }

    @Visitor
    private void visit(GoToStatement node) {
        graphDescription.add(node);

        NumberFactor temp = new NumberFactor(node.getLineNumber());
        visit(temp);

        graphDescription.associate(node, temp);
    }

    @Visitor
    private void visit(InputStatement node) {
        graphDescription.add(node);

        for (UnassignedIdentifier factor : node.getIdentifiers()) {
            graphDescription.associate(node, factor);
        }
    }

    @Visitor
    private void visit(LetStatement node) {
        graphDescription.add(node);

        graphDescription.associate(node, node.getIdentifier());
        graphDescription.associate(node, node.getValue());
    }

    @Visitor
    private void visit(PrintStatement node) {
        graphDescription.add(node);

        graphDescription.associate(node, node.getExpression());
    }

    @Visitor
    private void visit(CompoundPrintStatement node) {
        graphDescription.add(node);

        for (PrintStatement child : node.getStatements()) {
            graphDescription.remove(child);
            graphDescription.associate(node, child.getExpression());
        }
    }
}