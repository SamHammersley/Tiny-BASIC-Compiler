package uk.ac.tees.syntax.parser;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.grammar.Line;
import uk.ac.tees.syntax.grammar.Program;
import uk.ac.tees.syntax.grammar.UnassignedIdentifier;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticOperator;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalOperator;
import uk.ac.tees.syntax.grammar.expression.factor.IdentifierFactor;
import uk.ac.tees.syntax.grammar.expression.factor.NumberFactor;
import uk.ac.tees.syntax.grammar.expression.factor.StringLiteral;
import uk.ac.tees.syntax.grammar.statement.*;
import uk.ac.tees.syntax.parser.exception.ParseException;
import uk.ac.tees.syntax.parser.exception.UnrecognisedCommand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static uk.ac.tees.tokenizer.Token.Type.*;

/**
 * A recursive descent parser for Tiny BASIC source code. This parser takes a top-down approach starting with top level
 * production rules, defined by the context-free grammar, and then recursively descends into the, lower level,
 * constituent rules. The output of this parser is an Abstract Syntax Tree intermediate representation.
 * <p>
 * This class contains a set of mutually recursive procedures, each of which implementing the parsing functionality for
 * respective non-terminal nodes, that ultimately parse Tiny BASIC source code. For example, {@link #parseExpression()},
 * {@link #parseTerm()} and {@link #parseFactor()} are defined in terms of each other and parse expressions, terms and
 * factors respectively.
 * </p>
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class RecursiveDescentParser extends Parser {

    public RecursiveDescentParser(TokenSupplier supplier) {
        super(supplier);
    }

    @Override
    public Program parse(String name) throws ParseException {
        List<Line> lines = new ArrayList<>();

        while (supplier.hasNext()) {
            lines.add(parseLine());
        }

        supplier.expectValue("END"::equals);

        return new Program(name, lines);
    }

    /**
     * Parses a line of Tiny BASIC source code.
     * <pre>
     * {@code <line> ::= <number> <statement> \n}
     * </pre>
     *
     * @return a {@link Line} object.
     */
    private Line parseLine() throws ParseException {
        supplier.nextToken(NUMBER);

        int lineNumber = supplier.getValue(Integer::parseInt);
        Statement statement = parseStatement();

        if (supplier.hasNext()) {
            supplier.expectType(NEW_LINE);
        }

        return new Line(lineNumber, statement);
    }

    /**
     * Parses a Tiny BASIC statement, defined by the following rules.
     * This method uses reflection to find a method to parse {@link Statement}s of different types.
     * <pre>
     * {@code <statement> ::= PRINT <expression-list>
     *         IF <expression> <relational-op> <expression> THEN <statement>
     *         INPUT <identifier-list>
     *         LET <identifier = expression>
     *         GOTO <expression>
     *         GOSUB <expression>
     *         RETURN
     *         END}
     * </pre>
     *
     * @return a {@link Line} object.
     */
    private Statement parseStatement() throws ParseException {
        supplier.nextToken(KEYWORD);

        String keyword = supplier.getValue(s -> s.charAt(0) + s.toLowerCase().substring(1));

        if (supplier.hasNext()) {
            supplier.nextToken();
        }

        try {
            Method method = getClass().getDeclaredMethod("parse" + keyword + "Statement");
            method.setAccessible(true);

            return (Statement) method.invoke(this);

        } catch (NoSuchMethodException e) {
            throw new UnrecognisedCommand(supplier.getCurrentToken());

        } catch (InvocationTargetException e) {
            throw (ParseException) e.getCause();

        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Parses an {@link IfStatement}, defined by the following rule:
     * <pre>
     * {@code IF <expression> <relational-op> <expression> THEN <statement>}
     * </pre>
     *
     * @return an {@link IfStatement} object.
     */
    private IfStatement parseIfStatement() throws ParseException {
        AbstractSyntaxTreeNode left = parseExpression();

        supplier.expectType(REL_OP);
        RelationalOperator operator = supplier.getValue(RelationalOperator::fromSymbol);
        supplier.nextToken();

        RelationalBinaryExpression expression = new RelationalBinaryExpression(left, parseExpression(), operator);

        supplier.expectValue("THEN"::equals);

        return new IfStatement(expression, parseStatement());
    }

    /**
     * Parses a {@link PrintStatement}, defined by the following rule:
     * <pre>
     * {@code PRINT <expression-list>}
     * </pre>
     *
     * @return a {@link PrintStatement} object.
     */
    private PrintStatement parsePrintStatement() throws ParseException {
        List<AbstractSyntaxTreeNode> expressions = new ArrayList<>();
        expressions.add(parseExpression());

        while (supplier.currentTypeIs(COMMA)) {
            supplier.nextToken();

            expressions.add(parseExpression());
        }

        return new PrintStatement(expressions);
    }

    /**
     * Parses a {@link LetStatement}, defined by the following rule:
     * <pre>
     * {@code LET <identifier = expression>}
     * </pre>
     *
     * @return a {@link LetStatement} object.
     */
    private LetStatement parseLetStatement() throws ParseException {
        supplier.expectType(IDENTIFIER);
        UnassignedIdentifier identifier = supplier.getValue(UnassignedIdentifier::new);

        supplier.nextToken("="::equals);
        supplier.nextToken(NUMBER, IDENTIFIER);

        return new LetStatement(identifier, parseExpression());
    }

    /**
     * Parses an {@link InputStatement}, defined by the following rule:
     * <pre>
     * {@code INPUT <identifier-list>}
     * </pre>
     *
     * @return an {@link InputStatement} object.
     */
    private InputStatement parseInputStatement() throws ParseException {
        List<UnassignedIdentifier> identifiers = new ArrayList<>();

        supplier.expectType(IDENTIFIER);
        identifiers.add(supplier.getValue(UnassignedIdentifier::new));
        supplier.nextToken();

        while (supplier.currentTypeIs(COMMA)) {
            supplier.nextToken(IDENTIFIER);

            identifiers.add(supplier.getValue(UnassignedIdentifier::new));
        }

        return new InputStatement(identifiers);
    }

    /**
     * Parses a {@link GoToStatement}, defined by the following rule:
     * <pre>
     * {@code GOTO <expression>}
     * </pre>
     *
     * @return a {@link GoToStatement} object.
     */
    private GoToStatement parseGotoStatement() throws ParseException {
        supplier.expectType(NUMBER);
        int lineNumber = supplier.getValue(Integer::parseInt);

        supplier.nextToken();
        return new GoToStatement(new NumberFactor(lineNumber));
    }

    /**
     * Parses a {@link GoSubStatement}, defined by the following rule:
     * <pre>
     * {@code GOSUB <expression>}
     * </pre>
     *
     * @return a {@link GoToStatement} object.
     */
    private GoSubStatement parseGosubStatement() throws ParseException {
        supplier.expectType(NUMBER);
        int lineNumber = supplier.getValue(Integer::parseInt);

        supplier.nextToken();
        return new GoSubStatement(new NumberFactor(lineNumber));
    }

    /**
     * Parses a {@link ReturnStatement}, defined by the following rule:
     * <pre>
     * {@code RETURN}
     * </pre>
     *
     * @return a {@link ReturnStatement} object.
     */
    private ReturnStatement parseReturnStatement() {
        return new ReturnStatement();
    }

    /**
     * Parses an {@link EndStatement}, defined by the following rule:
     * <pre>
     * {@code END}
     * </pre>
     *
     * @return an {@link EndStatement} object.
     */
    private EndStatement parseEndStatement() {
        return new EndStatement();
    }

    /**
     * Parses an expression as an abstract syntax tree. An expression is defined by the following context-free grammar
     * production rule:
     * <pre>
     * {@code <expression> ::= [+ | -] <term> [(+ | -) <term>]*}
     * </pre>
     * The tree produced as a result of calling this function may be a sub-tree of a larger parent abstract syntax tree.
     *
     * @return {@link AbstractSyntaxTreeNode} representing the root node of an expression abstract syntax tree.
     * @throws ParseException where the given token sequence is syntactically incorrect.
     */
    private AbstractSyntaxTreeNode parseExpression() throws ParseException {
        AbstractSyntaxTreeNode expression = parseTerm();

        while (supplier.currentTypeIs(PLUS, MINUS)) {
            ArithmeticOperator operator = supplier.getValue(ArithmeticOperator::fromSymbol);
            supplier.nextToken();

            expression = new ArithmeticBinaryExpression(expression, parseTerm(), operator);
        }

        return expression;
    }

    /**
     * Parses a, non-terminal {@link AbstractSyntaxTreeNode}, term. An expression is defined by the following
     * context-free grammar production rule:
     * <pre>
     * {@code <factor> [(* | /) <factor>]*}
     * </pre>
     *
     * @return {@link AbstractSyntaxTreeNode} representing a, non-terminal, interior node of the residing abstract syntax
     * tree.
     * @throws ParseException where the given token sequence is syntactically incorrect.
     */
    private AbstractSyntaxTreeNode parseTerm() throws ParseException {
        AbstractSyntaxTreeNode term = parseFactor();

        while (supplier.currentTypeIs(MULTIPLY, DIV)) {
            ArithmeticOperator operator = supplier.getValue(ArithmeticOperator::fromSymbol);
            supplier.nextToken();

            term = new ArithmeticBinaryExpression(term, parseFactor(), operator);
        }

        return term;
    }

    /**
     * Parses a factor node as an {@link AbstractSyntaxTreeNode}. These nodes are terminal
     *
     * @return
     * @throws ParseException where the given token sequence is syntactically incorrect.
     */
    private AbstractSyntaxTreeNode parseFactor() throws ParseException {
        supplier.expectType(STRING_EXPRESSION, L_PARENTHESES, NUMBER, IDENTIFIER);

        try {
            switch (supplier.getType()) {
                case STRING_EXPRESSION:
                    // Technically not a factor but cleaner code.
                    return new StringLiteral(supplier.getValue());

                case L_PARENTHESES:
                    supplier.nextToken();
                    AbstractSyntaxTreeNode expression = parseExpression();
                    supplier.expectType(R_PARENTHESES);

                    return expression;

                case NUMBER:
                    return new NumberFactor(supplier.getValue(Integer::parseInt));

                case IDENTIFIER:
                    return new IdentifierFactor(supplier.getValue());

                default:
                    // This should never happen.
                    return null;
            }
        } finally {
            supplier.nextToken();
        }
    }

}