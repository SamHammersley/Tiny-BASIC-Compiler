package uk.ac.tees.tokenization.regex;

import uk.ac.tees.tokenization.Token;
import uk.ac.tees.tokenization.UnexpectedCharacterException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokenizes some string input given a set of regular expressions that are mapped to {@link Token.Type}s.
 *
 * This tokenizer iterates over the regex patterns and repeatedly checks the beginning of the input string,
 * for matches with said patterns. If all patterns tested and none matched with the start of the remaining string,
 * there is an unexpected character(s).
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class IteratingRegexTokenizer extends RegexTokenizer {

    /**
     * Regex pattern for horizontal whitespace.
     */
    private static final String WHITESPACE_PATTERN = "\\h+";

    /**
     * The column at which the character(s) are being tokenized.
     */
    private int currentCharIndex = 1;

    /**
     * The row at which the character(s) are being tokenized.
     */
    private int currentLine = 1;

    /**
     * Construct a new {@link RegexTokenizer}.
     *
     * @param grammarRules the regex rules.
     */
    public IteratingRegexTokenizer(Map<Pattern, Token.Type> grammarRules) {
        super(grammarRules);
    }

    @Override
    public Queue<Token> tokenize(String input) throws UnexpectedCharacterException {
        Queue<Token> tokens = new LinkedList<>();

        String remainingInput = input;

        // Whilst there is still some input to be tokenized.
        while(!remainingInput.isBlank()) {
            remainingInput = removeWhiteSpace(remainingInput);

            Optional<Token> matchedToken = Optional.empty();

            for (Pattern pattern : patterns.keySet()) {
                Matcher matcher = pattern.matcher(remainingInput);

                // Check if the start of remaining input matches one of the regex rules.
                if (matcher.lookingAt()) {

                    // Remove the tokenized portion of the string.
                    remainingInput = remainingInput.substring(matcher.end());
                    // Progress the pointer
                    currentCharIndex += matcher.end() - matcher.start();

                    Token.Type type = patterns.get(pattern);

                    if (type.equals(Token.Type.NEW_LINE)) {
                        currentLine++;
                        currentCharIndex = 1;
                    }

                    matchedToken = Optional.of(new Token(type, matcher.group()));
                    break;
                }
            }

            // Add the token if one exists, otherwise throw an exception since there was no rule to match input.
            tokens.add(matchedToken.orElseThrow(() -> new UnexpectedCharacterException(currentLine, currentCharIndex)));
        }

        return tokens;
    }

    /**
     * Removes the next whitespace character and progresses the char index pointer.
     *
     * @param input the input to remove the first white space character(s) from.
     * @return input string excluding first white space character(s).
     */
    private String removeWhiteSpace(String input) {
        Matcher wsMatcher = Pattern.compile(WHITESPACE_PATTERN).matcher(input);

        if (wsMatcher.lookingAt()) {
            // Remove the found white space.
            input = input.substring(wsMatcher.end());
            // Progress the pointer to after white space.
            currentCharIndex += wsMatcher.end() - wsMatcher.start();
        }

        return input;
    }

}