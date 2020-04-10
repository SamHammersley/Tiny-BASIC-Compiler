package uk.ac.tees.tokenizer.regex.sequential;

import uk.ac.tees.tokenizer.Token;
import uk.ac.tees.tokenizer.UnexpectedCharacterException;
import uk.ac.tees.tokenizer.regex.RegexTokenizer;
import uk.ac.tees.tokenizer.regex.RegexTokenizerPatternsCache;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokenizes some string input given a set of regular expressions that are mapped to {@link Token.Type}s.
 * <br />
 * This tokenizer iterates over the supported regex patterns and repeatedly checks the beginning of the input string,
 * for matches with these patterns. If all no patterns match with the start of the remaining string, there is an
 * unexpected character(s).
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class SequentialRegexTokenizer extends RegexTokenizer {

    /**
     * Regex pattern for horizontal whitespace.
     */
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\h+");

    /**
     * Construct a new {@link SequentialRegexTokenizer}.
     *
     * @param patterns provides regex patterns and corresponding {@link Token.Type}s.
     */
    public SequentialRegexTokenizer(RegexTokenizerPatternsCache patterns) {
        super(patterns);
    }

    @Override
    public Queue<Token> tokenize(String input) throws UnexpectedCharacterException {
        TokenizerCursor cursor = new TokenizerCursor(input);
        Queue<Token> tokens = new LinkedList<>();

        // Whilst there is still some remaining input to be tokenized.
        while(cursor.hasRemaining()) {
            Matcher wsMatcher = WHITESPACE_PATTERN.matcher(cursor.remaining());

            if (wsMatcher.lookingAt()) {
                // Advances the cursor and removes the matched string from remaining input.
                cursor.advance(wsMatcher.group());
            }

            Optional<Token> matchedToken = Optional.empty();

            for (Token.Type type : patterns.supportedTypes()) {
                Matcher matcher = patterns.getPattern(type).matcher(cursor.remaining());

                // Check if the start of remaining input matches one of the regex rules.
                if (matcher.lookingAt()) {
                    cursor.advance(matcher.group());

                    if (type.equals(Token.Type.NEW_LINE)) {
                        cursor.nextLine();
                    }

                    matchedToken = Optional.of(new Token(type, matcher.group()));
                    break;
                }
            }

            // Add the token if one exists, otherwise throw an exception since there was no rule to match input.
            tokens.add(matchedToken.orElseThrow(() -> new UnexpectedCharacterException(cursor.row(), cursor.column())));
        }

        return tokens;
    }

}