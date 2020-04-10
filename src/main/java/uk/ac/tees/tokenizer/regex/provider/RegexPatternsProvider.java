package uk.ac.tees.tokenizer.regex.provider;

import uk.ac.tees.tokenizer.regex.RegexTokenizerPatternsCache;

/**
 * Abstracts away the details of the source of regular expression patterns.
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public interface RegexPatternsProvider {

    /**
     * Creates a {@link RegexTokenizerPatternsCache} containing supported token types mapped to corresponding regular
     * expression patterns.
     */
    RegexTokenizerPatternsCache cache();

}