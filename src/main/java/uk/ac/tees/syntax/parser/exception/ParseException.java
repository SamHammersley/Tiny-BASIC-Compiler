package uk.ac.tees.syntax.parser.exception;

import uk.ac.tees.syntax.parser.Parser;

/**
 * Represents an {@link Exception} that is thrown when a {@link Parser} exhibits exceptional behaviour.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public class ParseException extends Exception {

    public ParseException(String message) {
        super(message);
    }
}