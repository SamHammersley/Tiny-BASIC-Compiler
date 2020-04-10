package uk.ac.tees.tokenizer;

import java.util.Queue;

/**
 * Abstraction of a tokenizer; takes Tiny BASIC source code and translate to a sequence of tokens.
 */
public interface TinyBasicTokenizer {

    Queue<Token> tokenize(String input) throws TokenizationException;

}