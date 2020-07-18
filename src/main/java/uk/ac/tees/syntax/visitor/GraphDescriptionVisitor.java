package uk.ac.tees.syntax.visitor;

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

import java.util.*;

/**
 * An {@link AbstractSyntaxTreeVisitor} that creates a DOT graph description for a given Abstract Syntax Tree. DOT
 * is a graph description language whereby described graphs can be visualised using Graphviz software.
 *
 * A graph description can be generated for any Abstract Syntax Tree or sub-tree of an Abstract Syntax Tree.
 *
 * <p>
 * @see <a href="https://graphviz.gitlab.io/_pages/doc/info/lang.html">DOT Language</a>
 * <p>
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class GraphDescriptionVisitor extends AbstractSyntaxTreeVisitor<String, AbstractSyntaxTreeNode> {

    /**
     * The first part of the dot graph description representing abstract syntax trees.
     */
    private static final String DOT_FILE_HEADER_FORMAT = "digraph %s {\n";

    /**
     * The last part of the dot graph description.
     */
    private static final String DOT_FILE_FOOTER = "}";

    /**
     * The string denoting that there is an edge between two given nodes.
     */
    private static final String DOT_FILE_ASSOCIATION = " -> ";

    /**
     * {@link StringBuilder} to build the graph description.
     */
    private final StringBuilder graphBuilder;

    /**
     * Maps parent node ids to a set of child node ids, whereby the parent node is associated, downward, to each of
     * the child nodes.
     */
    private final Map<Integer, Set<Integer>> associations = new LinkedHashMap<>();

    /**
     * Maps node ids to corresponding graph labels.
     */
    private final Map<Integer, String> labels = new LinkedHashMap<>();

    /**
     * Constructs a {@link GraphDescriptionVisitor} with a given outputFile and graphName. If no outputFile is given,
     * the graph description produced will not be persisted in a file.
     *
     * @param graphName  the name of the graph (typically the name of the program).
     */
    public GraphDescriptionVisitor(String graphName) {
        this.graphBuilder = new StringBuilder(String.format(DOT_FILE_HEADER_FORMAT, graphName));
    }

    /**
     * Associates the given parent {@link AbstractSyntaxTreeNode} with the given child {@link AbstractSyntaxTreeNode},
     * arrow going down.
     *
     * @param parent the parent node.
     * @param child the child node.
     */
    private void associate(AbstractSyntaxTreeNode parent, AbstractSyntaxTreeNode child) {
        int parentId = System.identityHashCode(parent);

        Set<Integer> children = associations.get(parentId);
        children.add(System.identityHashCode(child));

        associations.put(parentId, children);
    }

    /**
     * Removes the given {@link AbstractSyntaxTreeNode} from the graph.
     *
     * @param node the node to remove.
     */
    private void remove(AbstractSyntaxTreeNode node) {
        int id = System.identityHashCode(node);

        labels.remove(id);
        associations.remove(id);
    }

    /**
     * Creates a new node in the graph structure for the given {@link AbstractSyntaxTreeNode}.
     *
     * @param node the node to add to the graph description.
     */
    private void create(AbstractSyntaxTreeNode node) {
        int id = System.identityHashCode(node);

        labels.put(id, node.toString());
        associations.put(id, new HashSet<>());
    }

    /**
     * Adds a graph node with the given id and label to the graph description. Nodes are identifiable by their
     * identity hashCode, {@link System#identityHashCode(Object)}.
     *
     * @param id the id of node to add to the graph description.
     * @param label the label for the node to add.
     */
    private void addNode(int id, String label) {
        graphBuilder.append("\t").append(id).append(" [label=\"").append(label).append("\"]\n");
    }

    /**
     * Adds an association/edge between a parent node and a child node. Using the following syntax:
     *
     * <pre>{@code parent_node_id -> child_node_id}</pre>
     *
     * In this case, {@link System#identityHashCode(Object)} is used to identify node objects.
     *
     * @param parentId the id of the parent node to connect the edge to.
     * @param childId  the id of the child node to connect the edge to.
     */
    private void addAssociation(int parentId, int childId) {
        graphBuilder.append("\t").append(parentId).append(DOT_FILE_ASSOCIATION).append(childId).append("\n");
    }

    @Override
    public String visitTree(AbstractSyntaxTreeNode root) {
        root.accept(this);

        labels.forEach(this::addNode);
        associations.forEach((parent, children) ->
                children.forEach(child -> addAssociation(parent, child)));

        return graphBuilder.append(DOT_FILE_FOOTER).toString();
    }

    @Visitor(types={
            UnassignedIdentifier.class, IdentifierFactor.class,
            NumberFactor.class, StringLiteral.class,
            ReturnStatement.class, EndStatement.class
    })
    private void visit(AbstractSyntaxTreeNode node) {
        create(node);
    }

    @Visitor
    private void visit(UnaryExpression node) {
        create(node);
        associate(node, node.getExpression());
    }

    @Visitor(types={ArithmeticBinaryExpression.class, RelationalBinaryExpression.class})
    private <T extends BinaryOperator> void visit(BinaryExpression<T> node) {
        create(node);
        associate(node, node.getLeft());
        associate(node, node.getRight());
    }

    @Visitor
    private void visit(Program root) {
        create(root);

        for (Line line : root.lines()) {
            associate(root, line.getStatement());
        }
    }

    @Visitor
    private void visit(IfStatement node) {
        create(node);
        associate(node, node.getExpression());
        associate(node, node.getStatement());
    }

    @Visitor
    private void visit(GoSubStatement node) {
        create(node);

        NumberFactor temp = new NumberFactor(node.getLineNumber());
        visit(temp);

        associate(node, temp);
    }

    @Visitor
    private void visit(GoToStatement node) {
        create(node);

        NumberFactor temp = new NumberFactor(node.getLineNumber());
        visit(temp);

        associate(node, temp);
    }

    @Visitor
    private void visit(InputStatement node) {
        create(node);

        for (UnassignedIdentifier factor : node.getIdentifiers()) {
            associate(node, factor);
        }
    }

    @Visitor
    private void visit(LetStatement node) {
        create(node);

        associate(node, node.getIdentifier());
        associate(node, node.getValue());
    }

    @Visitor
    private void visit(PrintStatement node) {
        create(node);

        associate(node, node.getExpression());
    }

    @Visitor
    private void visit(CompoundPrintStatement node) {
        create(node);

        for (PrintStatement child : node.getStatements()) {
            remove(child);
            associate(node, child.getExpression());
        }
    }
}