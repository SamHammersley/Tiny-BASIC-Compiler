package uk.ac.tees.tokenization.regex;

import uk.ac.tees.tokenization.Token;
import uk.ac.tees.tokenization.UnexpectedCharacterException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokenizes some string input given a set of regular expressions that are mapped to {@link Token.Type}s.
 * <br />
 * This tokenizer iterates over the regex patterns and repeatedly checks the beginning of the input string,
 * for matches with said patterns. If all patterns tested and none matched with the start of the remaining string,
 * there is an unexpected character(s).
 * <br />
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class IteratingRegexTokenizer extends RegexTokenizer {

    /**
     * Regex pattern for horizontal whitespace.
     */
    private static final String WHITESPACE_PATTERN = "\\h+";

    /**
     * Construct a new {@link RegexTokenizer}.
     *
     * @param patterns the regex patterns to match tokens.
     */
    public IteratingRegexTokenizer(Map<Pattern, Token.Type> patterns) {
        super(patterns);
    }

    @Override
    public Queue<Token> tokenize(String input) throws UnexpectedCharacterException {
        Queue<Token> tokens = new LinkedList<>();

        String remainingInput = input;
        int currentCharIndex = 1;
        int currentLine = 1;

        // Whilst there is still some input to be tokenized.
        while(!remainingInput.isBlank()) {
            Optional<String> leadingWhiteSpace = leadingWhiteSpace(remainingInput);
            if (leadingWhiteSpace.isPresent()) {
                String w = leadingWhiteSpace.get();
                // Remove the found white space.
                remainingInput = remainingInput.replaceFirst(w,"");
                // Progress the pointer to after white space.
                currentCharIndex += w.length();
            }

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

            final int unexpectedLine = currentLine, unexpectedIndex = currentCharIndex;
            // Add the token if one exists, otherwise throw an exception since there was no rule to match input.
            tokens.add(matchedToken.orElseThrow(() -> new UnexpectedCharacterException(unexpectedLine, unexpectedIndex)));
        }

        return tokens;
    }

    /**
     * Gets the leading white space of the given string.
     *
     * @param input the string to search for white space in.
     * @return the white space string.
     */
    private Optional<String> leadingWhiteSpace(String input) {
        Matcher wsMatcher = Pattern.compile(WHITESPACE_PATTERN).matcher(input);

        if (wsMatcher.lookingAt()) {
            return Optional.of(wsMatcher.group());
        }

        return Optional.empty();
    }

}