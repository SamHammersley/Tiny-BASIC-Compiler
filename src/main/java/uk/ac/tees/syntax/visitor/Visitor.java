package uk.ac.tees.syntax.visitor;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Visitor {

    /**
     * The types of {@link AbstractSyntaxTreeNode}s that can be visited by the annotated method.
     *
     * @return an array of {@link Class}es that can be visited.
     */
    Class<? extends AbstractSyntaxTreeNode>[] types() default {};

}