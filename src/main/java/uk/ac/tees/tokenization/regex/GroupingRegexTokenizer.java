package uk.ac.tees.tokenization.regex;

import uk.ac.tees.tokenization.Token;
import uk.ac.tees.tokenization.UnexpectedCharacterException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * @param grammarRules the regex rules.
     */
    public GroupingRegexTokenizer(Map<Pattern, Token.Type> grammarRules) {
        super(grammarRules);
    }

    @Override
    public Queue<Token> tokenize(String input) throws UnexpectedCharacterException {
        LinkedList<TokenMatchResult> matchResults = new LinkedList<>();

        // Check every pattern for matches in the input string.
        for (Pattern pattern : patterns.keySet()) {

            Matcher matcher = pattern.matcher(input);
            Token.Type type = patterns.get(pattern);

            // Map each pattern match with a TokenMatchResult.
            matcher.results().forEach(r -> matchResults.add(TokenMatchResult.fromMatchResult(type, r)));
        }

        // Sort the results by the start index of the match, so they're in the same order as the input string.
        Collections.sort(matchResults);

        return validateResults(matchResults, input);
    }

    /**
     * Validates the given {@link List} of {@link TokenMatchResult}s and omits unnecessary detail (starting and ending
     * positions) to produce a list of {@link Token}s.
     *
     * @param results the regex pattern matching results.
     * @param input the input string.
     * @return a validated {@link List} of {@link Token}s.
     * @throws UnexpectedCharacterException when unexpected character detected.
     */
    private Queue<Token> validateResults(LinkedList<TokenMatchResult> results, String input) throws UnexpectedCharacterException {
        Queue<Token> tokens = new LinkedList<>();

        int lineCount = 1;
        int lastNewLineIndex = -1;

        // Check start of input is valid.
        validateStart(results.peekFirst(), input, lineCount);

        TokenMatchResult previous = null;
        for (TokenMatchResult r : results) {
            Token token = r.getToken();
            tokens.add(token);

            validateMiddle(previous, r, input, lineCount, lastNewLineIndex);

            if (token.getType().equals(Token.Type.NEW_LINE)) {
                lineCount++;
                lastNewLineIndex = r.start();
            }
            previous = r;
        }

        // Check start of input is valid.
        validateEnd(results.peekLast(), input, lineCount);

        return tokens;
    }

    /**
     * Validates the start of the string by checking the gap between it and the first match. If the first match starts
     * more than 1 character from the start and there is no whitespace at the start, there is an unexpected character.
     *
     * @param first the first {@link TokenMatchResult}.
     * @param input the input string to validate the start of.
     * @param lineCount the current line count.
     * @throws UnexpectedCharacterException when unexpected character is detected.
     */
    private void validateStart(TokenMatchResult first, String input, int lineCount)
            throws UnexpectedCharacterException {
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
     * @param last the last {@link TokenMatchResult}.
     * @param input the input string to validate the start of.
     * @param lineCount the current line count.
     * @throws UnexpectedCharacterException when unexpected character is detected.
     */
    private void validateEnd(TokenMatchResult last, String input, int lineCount)
            throws UnexpectedCharacterException {
        if (input.length() - last.end() > 0) {
            String potential = input.substring(last.end()).trim();

            // There is unexpected character at the end
            if (!potential.isBlank()) {
                throw new UnexpectedCharacterException(lineCount, last.end() - input.lastIndexOf("\n"));
            }
        }
    }

    /**
     * Validates the end of the string by checking the gap between the last match and it. If the last match ends
     * more than 1 character from the end and there is no whitespace at the end, there is an unexpected character.
     *
     * @param previous the previous {@link TokenMatchResult}.
     * @param next the next {@link TokenMatchResult}.
     * @param input the input string to validate.
     * @param lineCount the current line count.
     * @param lastNewLineIndex the index of the last new line character.
     * @throws UnexpectedCharacterException when unexpected character is detected.
     */
    private void validateMiddle(TokenMatchResult previous, TokenMatchResult next, String input, int lineCount, int lastNewLineIndex)
            throws UnexpectedCharacterException {
        // Check every match with the previous match for too much space between matches.
        if (previous != null && next.start() - previous.end() > 1) {
            String nonTokenized = input.substring(previous.end(), next.start());

            // There is unexpected character (not just whitespace) between matches.
            if (!nonTokenized.trim().isBlank()) {
                int charIndex = input.indexOf(nonTokenized.trim()) - lastNewLineIndex;

                throw new UnexpectedCharacterException(lineCount, charIndex);
            }
        }
    }

}