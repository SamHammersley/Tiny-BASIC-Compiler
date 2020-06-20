package uk.ac.tees.syntax.grammar;

import uk.ac.tees.syntax.visitor.AbstractSyntaxTreeVisitor;

/**
 * Represents a node in an Abstract Syntax Tree.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Abstract_syntax_tree">Abstract Syntax Tree</a>
 * @see <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor Pattern</a>
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public interface AbstractSyntaxTreeNode {

    /**
     * This function is called when traversing the tree, implementations should accept any child nodes, in the
     * correct order. This function should invoke the visit function, corresponding to this node type, of the given
     * {@link AbstractSyntaxTreeVisitor} to visit this node and accept any child nodes.
     *
     * Where there are child nodes, the order in which they are accepted and this node is visited should be considered.
     * For example, visiting this node before accepting the child nodes would be pre-order traversal. On the contrary,
     * accepting child nodes before visiting this node would be post-order traversal.
     *
     * @param visitor defines the actions to be executed upon visiting a node.
     */
    <T, K extends AbstractSyntaxTreeNode> void accept(AbstractSyntaxTreeVisitor<T, K> visitor);

}