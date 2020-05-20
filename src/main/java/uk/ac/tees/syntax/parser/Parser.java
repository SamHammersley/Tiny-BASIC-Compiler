package uk.ac.tees.syntax.parser;

import uk.ac.tees.syntax.grammar.Program;
import uk.ac.tees.syntax.parser.exception.ParseException;
import uk.ac.tees.tokenizer.Token;

/**
 * Parses a sequence of {@link Token}s and creates an Abstract Syntax Tree intermediate representation for syntax
 * analysis.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public abstract class Parser {

    /**
     * Sequentially supplies tokens, as requested, for parsing.
     */
    final TokenSupplier supplier;

    Parser(TokenSupplier supplier) {
        this.supplier = supplier;
    }

    /**
     * Parses a sequence of tokens as an Abstract Syntax Tree intermediate representation.
     *
     * @param name the name of the program.
     * @return a {@link Program} instance that is the root node of the abstract syntax tree.
     * @throws ParseException when unexpected or unidentified input is received.
     */
    public abstract Program parse(String name) throws ParseException;

}