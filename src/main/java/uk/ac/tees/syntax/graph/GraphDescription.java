package uk.ac.tees.syntax.graph;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;

import java.util.*;

/**
 * Builds a graph description from {@link AbstractSyntaxTreeNode}s. Uses {@link System#identityHashCode(Object)} to
 * identify nodes, since {@link #hashCode()} has been override in some classes.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class GraphDescription {

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
     * {@link Map} of associations between parent node ids and child node ids.
     */
    private final Map<Integer, Set<Integer>> associations = new HashMap<>();

    /**
     * {@link Set} of labels, created for each node.
     */
    private final Map<Integer, String> labels = new LinkedHashMap<>();

    /**
     * Builds the graph as a {@link String}.
     *
     * @param name the name of the diagram.
     * @return A {@link String} description of the graph.
     */
    public String build(String name) {
        StringBuilder bldr = new StringBuilder(String.format(DOT_FILE_HEADER_FORMAT, name));

        labels.values().forEach(bldr::append);

        for (int parentNodeId : associations.keySet()) {
            associations.get(parentNodeId)
                    .stream()
                    .map(childId -> createAssociation(parentNodeId, childId))
                    .forEach(bldr::append);
        }

        return bldr.append(DOT_FILE_FOOTER).toString();
    }

    /**
     * Creates an association string for DOT graph descriptions, between a child and parent node.
     *
     * @param parentId the identifier of the parent node.
     * @param childId the identifier of the child node.
     * @return a {@link String} in the form <code>pId -> cId</code>
     */
    private String createAssociation(int parentId, int childId) {
        return "\t" + parentId + DOT_FILE_ASSOCIATION + childId + "\n";
    }

    /**
     * Creates a label string for the given {@link AbstractSyntaxTreeNode}.
     *
     * @param node the node to create a label for.
     * @return a {@link String} in the form <code>id [label=name]</code>
     */
    private String createLabel(AbstractSyntaxTreeNode node) {
        return "\t" + System.identityHashCode(node) + " [label=\"" + node.toString() + "\"]\n";
    }

    /**
     * Adds the given node to the graph.
     *
     * @param node the {@link AbstractSyntaxTreeNode} to add.
     */
    public void add(AbstractSyntaxTreeNode node) {
        labels.put(System.identityHashCode(node), createLabel(node));
    }

    /**
     * Associates the given parent with the given child {@link AbstractSyntaxTreeNode}.
     *
     * @param parent the parent node, above child.
     * @param child the child node, arrow going towards.
     */
    public void associate(AbstractSyntaxTreeNode parent, AbstractSyntaxTreeNode child) {
        int parentId = System.identityHashCode(parent);
        Set<Integer> assoc = associations.getOrDefault(parentId, new LinkedHashSet<>());

        assoc.add(System.identityHashCode(child));
        associations.put(parentId, assoc);
    }

    /**
     * Remove the given {@link AbstractSyntaxTreeNode} from {@link #associations} and {@link #labels}, effectively
     * removing them from the graph.
     *
     * @param node the node to remove.
     */
    public void remove(AbstractSyntaxTreeNode node) {
        int id = System.identityHashCode(node);
        associations.remove(id);
        labels.remove(id);
    }

}