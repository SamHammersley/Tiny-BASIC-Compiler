package uk.ac.tees.syntax.parser;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.grammar.Line;
import uk.ac.tees.syntax.grammar.Program;
import uk.ac.tees.syntax.grammar.UnassignedIdentifier;
import uk.ac.tees.syntax.grammar.expression.Expression;
import uk.ac.tees.syntax.grammar.expression.UnaryExpression;
import uk.ac.tees.syntax.grammar.expression.UnaryOperator;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticOperator;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalOperator;
import uk.ac.tees.syntax.grammar.factor.IdentifierFactor;
import uk.ac.tees.syntax.grammar.factor.NumberFactor;
import uk.ac.tees.syntax.grammar.factor.StringLiteral;
import uk.ac.tees.syntax.grammar.statement.*;
import uk.ac.tees.syntax.parser.exception.ParseException;
import uk.ac.tees.syntax.parser.exception.UnrecognisedCommandException;

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

        return new Program(name, lines);
    }

    /**
     * Parses a line of Tiny BASIC source code.
     * <pre>
     * {@code <line> ::= <number> <statement> \n}
     * </pre>
     *
     * @return a {@link Line} object.
     * @throws ParseException if the expected token criteria is not matched.
     */
    public Line parseLine() throws ParseException {
        supplier.scan(NUMBER);

        int lineNumber = supplier.getValue(Integer::parseInt);
        Statement statement = parseStatement();

        if (supplier.hasNext()) {
            supplier.predictType(NEW_LINE);
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
     * @throws ParseException if the expected token criteria is not matched.
     */
    public Statement parseStatement() throws ParseException {
        supplier.scan(KEYWORD);

        String keyword = supplier.getValue(String::toLowerCase);
        if (supplier.hasNext()) {
            supplier.scan();
        }

        return switch(keyword) {
            case "print" -> parsePrintStatement();
            case "input" -> parseInputStatement();
            case "if" -> parseIfStatement();
            case "let" -> parseLetStatement();
            case "goto" -> parseGotoStatement();
            case "gosub" -> parseGoSubStatement();
            case "return" -> parseReturnStatement();
            case "end" -> parseEndStatement();
            default -> throw new UnrecognisedCommandException(supplier.getCurrentToken());
        };
    }

    /**
     * Parses an {@link IfStatement}, defined by the following rule:
     * <pre>
     * {@code IF <expression> <relational-op> <expression> THEN <statement>}
     * </pre>
     *
     * @return an {@link IfStatement} object.
     * @throws ParseException if the expected token criteria is not matched.
     */
    private IfStatement parseIfStatement() throws ParseException {
        Expression left = parseExpression();

        supplier.predictType(REL_OP);
        RelationalOperator operator = supplier.getValue(RelationalOperator::fromSymbol);
        supplier.scan();

        RelationalBinaryExpression expression = new RelationalBinaryExpression(left, parseExpression(), operator);

        supplier.predictValue("THEN");

        return new IfStatement(expression, parseStatement());
    }

    /**
     * Parses a {@link PrintStatement}, defined by the following rule:
     * <pre>
     * {@code PRINT <expression-list>}
     * </pre>
     *
     * @return a {@link PrintStatement} object.
     * @throws ParseException if the expected token criteria is not matched.
     */
    private PrintStatement parsePrintStatement() throws ParseException {
        List<AbstractSyntaxTreeNode> expressions = new ArrayList<>();
        expressions.add(parsePrintExpression());

        while (supplier.currentTypeIs(COMMA)) {
            supplier.scan();

            expressions.add(parsePrintExpression());
        }

        return new PrintStatement(expressions);
    }

    /**
     * Parses arguments for a print statement, these may be string literals or expressions as defined in the grammar.
     *
     * @return {@link AbstractSyntaxTreeNode} representing an argument of a {@link PrintStatement}.
     * @throws ParseException thrown if the {@link #supplier} runs out of tokens.
     */
    private AbstractSyntaxTreeNode parsePrintExpression() throws ParseException {
        AbstractSyntaxTreeNode expression;

        if (supplier.currentTypeIs(STRING_EXPRESSION)) {
            expression = supplier.getValue(StringLiteral::new);
            supplier.scan();

        } else {
            expression = parseExpression();
        }

        return expression;
    }

    /**
     * Parses a {@link LetStatement}, defined by the following rule:
     * <pre>
     * {@code LET <identifier = expression>}
     * </pre>
     *
     * @return a {@link LetStatement} object.
     * @throws ParseException if the expected token criteria is not matched.
     */
    private LetStatement parseLetStatement() throws ParseException {
        supplier.predictType(IDENTIFIER);
        UnassignedIdentifier identifier = supplier.getValue(UnassignedIdentifier::new);

        supplier.scan("=");
        supplier.scan();

        return new LetStatement(identifier, parseExpression());
    }

    /**
     * Parses an {@link InputStatement}, defined by the following rule:
     * <pre>
     * {@code INPUT <identifier-list>}
     * </pre>
     *
     * @return an {@link InputStatement} object.
     * @throws ParseException if the expected token criteria is not matched.
     */
    private InputStatement parseInputStatement() throws ParseException {
        List<UnassignedIdentifier> identifiers = new ArrayList<>();

        supplier.predictType(IDENTIFIER);
        identifiers.add(supplier.getValue(UnassignedIdentifier::new));
        supplier.scan();

        while (supplier.currentTypeIs(COMMA)) {
            supplier.scan(IDENTIFIER);

            identifiers.add(supplier.getValue(UnassignedIdentifier::new));

            supplier.scan(COMMA, NEW_LINE);
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
     * @throws ParseException if the expected token criteria is not matched.
     */
    private GoToStatement parseGotoStatement() throws ParseException {
        supplier.predictType(NUMBER);
        int lineNumber = supplier.getValue(Integer::parseInt);

        supplier.scan();
        return new GoToStatement(lineNumber);
    }

    /**
     * Parses a {@link GoSubStatement}, defined by the following rule:
     * <pre>
     * {@code GOSUB <expression>}
     * </pre>
     *
     * @return a {@link GoToStatement} object.
     * @throws ParseException if the expected token criteria is not matched.
     */
    private GoSubStatement parseGoSubStatement() throws ParseException {
        supplier.predictType(NUMBER);
        int lineNumber = supplier.getValue(Integer::parseInt);

        supplier.scan();
        return new GoSubStatement(lineNumber);
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
     * {@code <expression> ::= <term> [(+ | -) <term>]*}
     * </pre>
     * The tree produced as a result of calling this function may be a sub-tree of a larger parent abstract syntax tree.
     *
     * @return {@link AbstractSyntaxTreeNode} representing the root node of an expression abstract syntax tree.
     * @throws ParseException where the given token sequence is syntactically incorrect.
     */
    private Expression parseExpression() throws ParseException {
        Expression expression = parseTerm();

        while (supplier.currentTypeIs(PLUS, MINUS)) {
            ArithmeticOperator operator = supplier.getValue(ArithmeticOperator::fromSymbol);
            supplier.scan();

            expression = new ArithmeticBinaryExpression(expression, parseTerm(), operator);
        }

        return expression;
    }

    /**
     * Parses a, non-terminal {@link AbstractSyntaxTreeNode}, term. A term is defined by the following
     * context-free grammar production rule:
     * <pre>
     * {@code <term> ::= <factor> (("*" | "/") <factor>)*}
     * </pre>
     *
     * @return {@link AbstractSyntaxTreeNode} representing a, non-terminal, interior node of the residing abstract syntax
     * tree.
     * @throws ParseException where the given token sequence is syntactically incorrect.
     */
    private Expression parseTerm() throws ParseException {
        Expression term = parseFactor();

        while (supplier.currentTypeIs(MULTIPLY, DIV)) {
            ArithmeticOperator operator = supplier.getValue(ArithmeticOperator::fromSymbol);
            supplier.scan();

            term = new ArithmeticBinaryExpression(term, parseFactor(), operator);
        }

        return term;
    }

    /**
     * Parses a factor node as an {@link AbstractSyntaxTreeNode}. These nodes are terminal and defined by the following
     * rule:
     * <pre>
     * {@code <factor> ::= ("+" | "-") factor | <identifier> | <number> | "(" <expression> ")"}
     * </pre>
     *
     * @return an {@link AbstractSyntaxTreeNode} representing a factor in an expression.
     * @throws ParseException where the given token sequence is syntactically incorrect.
     */
    private Expression parseFactor() throws ParseException {
        supplier.predictType(PLUS, MINUS, L_PARENTHESES, NUMBER, IDENTIFIER);

        Expression factor;

        switch (supplier.getType()) {

            case PLUS:
            case MINUS:
                UnaryOperator operator = supplier.getValue(UnaryOperator::fromSymbol);
                supplier.scan();

                factor = new UnaryExpression(operator, parseFactor());
                break;

            case L_PARENTHESES:
                supplier.scan();
                Expression expression = parseExpression();
                supplier.predictType(R_PARENTHESES);

                factor = expression;
                supplier.scan();
                break;

            case NUMBER:
                factor = new NumberFactor(supplier.getValue(Integer::parseInt));
                supplier.scan();
                break;

            case IDENTIFIER:
                factor = new IdentifierFactor(supplier.getValue());
                supplier.scan();
                break;

            default:
                // This should never happen.
                return null;
        }

        return factor;
    }

}