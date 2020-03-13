package uk.ac.tees.tokenization.regex.group;

import uk.ac.tees.tokenization.Token;
import uk.ac.tees.tokenization.UnexpectedCharacterException;
import uk.ac.tees.tokenization.regex.RegexTokenizer;
import uk.ac.tees.tokenization.regex.RegexTokenizerPatternsCache;

import java.util.*;
import java.util.regex.Matcher;

/**
 * Tokenizes some string input given a set of regular expressions that are mapped to {@link Token.Type}s.
 * <br />
 * This tokenizer finds all matches of each regex pattern and flattens them into a single list. The flattened
 * list is then sorted by the starting index (of each match) and then checks if there are any unexpected characters.
 * <br />
 * Those unexpected characters are detected by checking the remains of the input string. If there are parts that were
 * not matched then they are surely unexpected.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class GroupingRegexTokenizer extends RegexTokenizer {

    /**
     * Construct a new {@link RegexTokenizer}.
     *
     * @param patterns provides regex patterns and corresponding {@link Token.Type}s.
     */
    public GroupingRegexTokenizer(RegexTokenizerPatternsCache patterns) {
        super(patterns);
    }

    @Override
    public Queue<Token> tokenize(String input) throws UnexpectedCharacterException {
        LinkedList<TokenMatchResult> matchResults = new LinkedList<>();

        // Check each token type.
        for (Token.Type type : patterns.supportedTypes()) {
            Matcher matcher = patterns.getPattern(type).matcher(input);

            // Map each pattern match to a TokenMatchResult.
            matcher.results().forEach(r -> matchResults.add(TokenMatchResult.fromMatchResult(type, r)));
        }

        // Sort the results by the start index of the match, to maintain proper ordering.
        Collections.sort(matchResults);

        return validateResults(matchResults, input);
    }

    /**
     * Validates the given {@link List} of {@link TokenMatchResult}s, omits unnecessary detail (starting and ending
     * positions) and produces a list of {@link Token}s.
     * <br />
     * The beginning of the string, the end and middle are checked for unmatched, non-whitespace, characters; these
     * are unexpected and upon detection will throw an exception.
     *
     * @param results the regex pattern matching results.
     * @param input the input string.
     * @return a validated {@link List} of {@link Token}s.
     * @throws UnexpectedCharacterException when unexpected character detected.
     */
    private Queue<Token> validateResults(LinkedList<TokenMatchResult> results, String input)
            throws UnexpectedCharacterException {
        Queue<Token> tokens = new LinkedList<>();

        int lineCount = 1;
        int lastNewLineIndex = -1;

        // Check start of input is valid, no unexpected chars.
        validateStart(results, input, lineCount);

        TokenMatchResult previous = null;
        for (TokenMatchResult result : results) {
            Token token = result.getToken();
            tokens.add(token);

            // Check each non-tokenized section of the input between every interior match.
            validateMiddle(previous, result, input, lineCount, lastNewLineIndex);

            if (token.getType().equals(Token.Type.NEW_LINE)) {
                lastNewLineIndex = result.start();
                lineCount++;
            }
            previous = result;
        }

        // Check start of input is valid.
        validateEnd(results, input, lineCount);

        return tokens;
    }

    /**
     * Validates the start of the string by checking the gap between it and the first match. If the first match starts
     * more than 1 character from the start and there is no whitespace at the start, there is an unexpected character.
     *
     * @param results the {@link TokenMatchResult}s to validate the start of.
     * @param input the input string to validate the start of.
     * @param lineCount the current line count.
     * @throws UnexpectedCharacterException when unexpected character is detected.
     */
    private void validateStart(LinkedList<TokenMatchResult> results, String input, int lineCount)
            throws UnexpectedCharacterException {
        TokenMatchResult first = Objects.requireNonNull(results.peekFirst());

        if (first.start() > 0) {
            String potential = input.substring(0, first.start()).trim();

            // There is unexpected character at the start
            if (!potential.isBlank()) {
                throw new UnexpectedCharacterException(lineCount, 0);
            }
        }
    }

    /**
     * Validates the end of the string by checking the gap between the last match and it. If the last match ends
     * more than 1 character from the end and there is no whitespace at the end, there is an unexpected character.
     *
     * @param results the {@link TokenMatchResult}s to validate the start of.
     * @param input the input string to validate the start of.
     * @param lineCount the current line count.
     * @throws UnexpectedCharacterException when unexpected character is detected.
     */
    private void validateEnd(LinkedList<TokenMatchResult> results, String input, int lineCount)
            throws UnexpectedCharacterException {
        TokenMatchResult last = Objects.requireNonNull(results.peekLast());

        if (input.length() - last.end() > 0) {
            String potential = input.substring(last.end()).trim();

            // There is unexpected character at the end
            if (!potential.isBlank()) {
                throw new UnexpectedCharacterException(lineCount, last.end() - input.lastIndexOf("\n"));
            }
        }
    }

    /**
     * Validates the middle portion of the input by checking for non-whitespace characters between {@link TokenMatchResult}s.
     *
     * @param previous the previous {@link TokenMatchResult}.
     * @param next the next {@link TokenMatchResult}.
     * @param input the input string to validate.
     * @param lineCount the current line count.
     * @param lastNewLineIndex the index of the last new line character.
     * @throws UnexpectedCharacterException when unexpected character is detected.
     */
    private void validateMiddle(TokenMatchResult previous, TokenMatchResult next, String input, int lineCount,
                                int lastNewLineIndex) throws UnexpectedCharacterException {
        // Check every match with the previous match for too much space between matches.
        if (previous != null && next.start() - previous.end() > 1) {
            String nonTokenized = input.substring(previous.end(), next.start());

            // There is unexpected character (not just whitespace) between matches.
            if (!nonTokenized.trim().isBlank()) {
                // Get the index of the first non-whitespace character.
                int charIndex = input.indexOf(nonTokenized.trim()) - lastNewLineIndex;

                throw new UnexpectedCharacterException(lineCount, charIndex);
            }
        }
    }

}