package uk.ac.tees.syntax.parser;

import uk.ac.tees.syntax.grammar.AbstractSyntaxTreeNode;
import uk.ac.tees.syntax.grammar.Line;
import uk.ac.tees.syntax.grammar.Program;
import uk.ac.tees.syntax.grammar.UnassignedIdentifier;
import uk.ac.tees.syntax.grammar.expression.UnaryExpression;
import uk.ac.tees.syntax.grammar.expression.UnaryOperator;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.arithmetic.ArithmeticOperator;
import uk.ac.tees.syntax.grammar.expression.factor.IdentifierFactor;
import uk.ac.tees.syntax.grammar.expression.factor.NumberFactor;
import uk.ac.tees.syntax.grammar.expression.factor.StringLiteral;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalBinaryExpression;
import uk.ac.tees.syntax.grammar.expression.relational.RelationalOperator;
import uk.ac.tees.syntax.grammar.statement.*;
import uk.ac.tees.syntax.parser.exception.ParseException;
import uk.ac.tees.syntax.parser.exception.UnrecognisedCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * Represents a function that parses and returns a {@link Statement}.
     *
     * @author Sam Hammersley - Gonsalves (q5315908)
     */
    private interface StatementParser {

        /**
         * Parses a {@link Statement}, using tokens from {@link RecursiveDescentParser#supplier}.
         *
         * @return the parsed {@link Statement}.
         * @throws ParseException when input is syntactically incorrect.
         */
        Statement parse() throws ParseException;

    }

    /**
     * Maps keyword values to statement parsing functions.
     */
    private final Map<String, StatementParser> statementParsers = Map.of(
            "if", this::parseIfStatement,
            "print", this::parsePrintStatement,
            "let", this::parseLetStatement,
            "input", this::parseInputStatement,
            "goto", this::parseGotoStatement,
            "gosub", this::parseGoSubStatement,
            "return", this::parseReturnStatement,
            "end", this::parseEndStatement);

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
     * @throws ParseException if the expected token criteria is not matched.
     */
    private Statement parseStatement() throws ParseException {
        supplier.nextToken(KEYWORD);

        String keyword = supplier.getValue(String::toLowerCase);

        if (!statementParsers.containsKey(keyword)) {
            throw new UnrecognisedCommand(supplier.getCurrentToken());
        }

        if (supplier.hasNext()) {
            supplier.nextToken();
        }

        return statementParsers.get(keyword).parse();
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
     * @throws ParseException if the expected token criteria is not matched.
     */
    private Statement parsePrintStatement() throws ParseException {
        AbstractSyntaxTreeNode expression = parsePrintExpression();

        if (!supplier.currentTypeIs(COMMA)) {
            return new PrintStatement(expression);
        }

        CompoundPrintStatement statement = new CompoundPrintStatement();
        statement.addExpression(expression);

        while (supplier.currentTypeIs(COMMA)) {
            supplier.nextToken();

            statement.addExpression(parsePrintExpression());
        }

        return statement;
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
            supplier.nextToken();

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
     * @throws ParseException if the expected token criteria is not matched.
     */
    private InputStatement parseInputStatement() throws ParseException {
        List<UnassignedIdentifier> identifiers = new ArrayList<>();

        supplier.expectType(IDENTIFIER);
        identifiers.add(supplier.getValue(UnassignedIdentifier::new));
        supplier.nextToken();

        while (supplier.currentTypeIs(COMMA)) {
            supplier.nextToken(IDENTIFIER);

            identifiers.add(supplier.getValue(UnassignedIdentifier::new));

            supplier.nextToken(COMMA, NEW_LINE);
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
        supplier.expectType(NUMBER);
        int lineNumber = supplier.getValue(Integer::parseInt);

        supplier.nextToken();
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
        supplier.expectType(NUMBER);
        int lineNumber = supplier.getValue(Integer::parseInt);

        supplier.nextToken();
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
     * @return an {@link AbstractSyntaxTreeNode} representing a factor in an expression.
     * @throws ParseException where the given token sequence is syntactically incorrect.
     */
    private AbstractSyntaxTreeNode parseFactor() throws ParseException {
        supplier.expectType(L_PARENTHESES, NUMBER, IDENTIFIER);

        try {
            switch (supplier.getType()) {

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