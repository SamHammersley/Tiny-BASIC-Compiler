package uk.ac.tees.tokenizer.regex.group;

import uk.ac.tees.tokenizer.Token;
import uk.ac.tees.tokenizer.UnexpectedCharacterException;
import uk.ac.tees.tokenizer.regex.RegexTokenizer;
import uk.ac.tees.tokenizer.regex.patterns.TokenizerPatternsCache;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

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
    public GroupingRegexTokenizer(TokenizerPatternsCache patterns) {
        super(patterns);
    }

    @Override
    public Queue<Token> tokenize(String input) throws UnexpectedCharacterException {
        List<TokenMatchResult> tokenMatches = new ArrayList<>();

        String remainingInput = input;
        for (Token.Type type : patterns.supportedTypes()) {
            Matcher matcher = patterns.getPattern(type).matcher(remainingInput);

            int previousIndex = -1;
            int row = 1;

            while (matcher.find()) {
                if (type.equals(Token.Type.NEW_LINE)) {
                    row++;
                }
                String tokenText = matcher.group();
                previousIndex = input.indexOf(tokenText, previousIndex + 1);
                int column = input.substring(0, matcher.start()).lastIndexOf('\n') + 1;

                Token token = new Token(type, tokenText, row, column);
                int endIndex = previousIndex + tokenText.length();
                tokenMatches.add(new TokenMatchResult(previousIndex, endIndex, token));
            }

            remainingInput = matcher.replaceAll("");
        }

        Collections.sort(tokenMatches);
        validateResults(input, remainingInput);

        return tokenMatches.stream().map(TokenMatchResult::getToken).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Validates the given input, if the remaining input is non-whitespace then there are unexpected characters.
     *
     * @param input          the initial input.
     * @param remainingInput the remaining input after tokenizer.
     * @throws UnexpectedCharacterException when unexpected character detected.
     */
    private void validateResults(String input, String remainingInput) throws UnexpectedCharacterException {
        if (!remainingInput.isBlank()) {
            int unexpectedIndex = input.indexOf(remainingInput.trim().charAt(0)) + 1;
            int lastNewLineCharacter = input.substring(0, unexpectedIndex).lastIndexOf("\n") + 1;
            int lineCount = countLines(input, unexpectedIndex);

            throw new UnexpectedCharacterException(lineCount, Math.abs(lastNewLineCharacter - unexpectedIndex));
        }
    }

    /**
     * Counts the number of new line characters in an input string up to the given index.
     *
     * @param input the string to count new lines from.
     * @param to    the index to count new line characters up to.
     * @return the number of new line characters in the specified input string.
     */
    private int countLines(String input, int to) {
        int lineCount = 1;

        for (int index = 0; index < to; index++) {
            if (input.charAt(index) == '\n') {
                lineCount++;
            }
        }

        return lineCount;
    }

}