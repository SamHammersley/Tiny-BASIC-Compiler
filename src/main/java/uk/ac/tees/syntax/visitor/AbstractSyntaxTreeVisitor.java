package uk.ac.tees.syntax.visitor;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * A method is an appropriate visitor where the method is annotated with the {@link Visitor} annotation and the
 * the given node's type either matches that of the parameter or is listed in the annotation as an accepted type.
 *
 * If there is no appropriate visitor method for a type of node, then there is no implemented behaviour for nodes of
 * that type in this implementation.
 *
 * This class is an implementation of the Visitor pattern, a behavioural design pattern. It allows the tree to be
 * visited and evaluated in a certain way without adding behaviour to the nodes themselves.
 *
 * @param <T> the expected output type from visiting an Abstract Syntax Tree.
 * @param <K> the expected input type of the root node of the visited Abstract Syntax Tree.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public abstract class AbstractSyntaxTreeVisitor<T, K extends AbstractSyntaxTreeNode> {

    /**
     * Visits a tree from the given root node. This can be a subtree of another abstract syntax tree.
     *
     * @param rootNode the root {@link AbstractSyntaxTreeNode} of the tree to visit.
     * @return the output from visiting the tree of the given root node.
     */
    public abstract T visitTree(K rootNode);

    /**
     * Visits the given node by finding the appropriate visitor method and invoking that method.
     *
     * @param node the node to visit.
     */
    public void visitNode(AbstractSyntaxTreeNode node) {
        Optional<Method> visitor = Arrays
                .stream(getClass().getDeclaredMethods())
                .filter(m -> isAppropriateVisitor(node, m))
                .findAny();

        visitor.ifPresent(m -> invokeVisitor(m, node));
    }

    /**
     * Invokes a method with the given {@link AbstractSyntaxTreeNode} as a parameter.
     *
     * @param visitor the method to invoke.
     * @param node the node to be given as a parameter to the visitor method.
     */
    private void invokeVisitor(Method visitor, AbstractSyntaxTreeNode node) {
        try {
            visitor.setAccessible(true);
            visitor.invoke(this, node);

        } catch (InvocationTargetException e) {
            throw (RuntimeException) e.getCause();

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This function determines whether a given method is appropriate for the given node.
     *
     * @param node the node that is to be visited.
     * @param method the method that is being checked.
     * @return {@code true} if the given method is appropriate for the given node.
     */
    private boolean isAppropriateVisitor(AbstractSyntaxTreeNode node, Method method) {
        if (!method.isAnnotationPresent(Visitor.class) || method.getParameterCount() != 1) {
            return false;
        }

        Visitor annotation = method.getAnnotation(Visitor.class);
        for (Class<? extends AbstractSyntaxTreeNode> type : annotation.types()) {
            if (node.getClass().equals(type)) {
                return true;
            }
        }

        return node.getClass().isAssignableFrom(method.getParameterTypes()[0]);
    }

}