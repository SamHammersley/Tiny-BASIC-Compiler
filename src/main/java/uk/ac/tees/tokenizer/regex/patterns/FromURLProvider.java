package uk.ac.tees.tokenizer.regex.patterns;

import uk.ac.tees.tokenizer.Token;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Gets {@link Pattern}s for {@link Token.Type}s from a URL. The expected syntax is as follows:
 * <pre>{@code TOKEN_NAME: Regex pattern}</pre>
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class FromURLProvider extends TokenizerPatternsProvider {

    /**
     * The name of the file containing the regex patterns.
     */
    private final URL file;

    /**
     * Constructs new {@link FromURLProvider} with the given file name.
     *
     * @param file the name of the file containing the patterns.
     */
    public FromURLProvider(URL file) {
        this.file = file;
    }

    @Override
    protected Map<Token.Type, Pattern> getPatterns() {
        Map<Token.Type, Pattern> map = new LinkedHashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.openStream()));

            reader.lines().forEach(l -> {
                String[] parts = l.split(": ");

                if (parts.length != 2) {
                    throw new IllegalArgumentException("Incorrect number of parts");
                }

                map.put(Token.Type.valueOf(parts[0]), Pattern.compile(parts[1]));
            });

        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }

        return map;
    }

}