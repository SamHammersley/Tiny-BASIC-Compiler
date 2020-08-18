package uk.ac.tees.syntax.grammar.expression;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;

/**
 * Represents an expression. According to the grammar this may be a factor, a term or some sequence of the two,
 * separated by +, -, * or /. Subtypes of this class are:
 * <li>{@link BinaryExpression}</li>
 * <li>{@link UnaryExpression}</li>
 * <li>{@link uk.ac.tees.syntax.grammar.factor.NumberFactor}</li>
 * <li>{@link uk.ac.tees.syntax.grammar.factor.IdentifierFactor}</li>
 *
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public interface Expression extends AbstractSyntaxTreeNode {
}