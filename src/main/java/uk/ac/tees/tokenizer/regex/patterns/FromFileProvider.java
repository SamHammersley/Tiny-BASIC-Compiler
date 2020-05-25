package uk.ac.tees.tokenizer.regex.patterns;

import uk.ac.tees.tokenizer.Token;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Gets {@link Pattern}s for {@link Token.Type}s from a file. The expected syntax is as follows:
 * <pre>{@code TOKEN_NAME: Regex pattern}</pre>
 *
 * @author Sam Hammersley - Gonsalves (q5315908)
 */
public final class FromFileProvider extends TokenizerPatternsProvider {

    /**
     * The name of the file containing the regex patterns.
     */
    private final String file;

    /**
     * Constructs new {@link FromFileProvider} with the given file name.
     *
     * @param file the name of the file containing the patterns.
     */
    public FromFileProvider(String file) {
        this.file = file;
    }

    @Override
    protected Map<Token.Type, Pattern> getPatterns() {
        Path path = Paths.get(file);
        Map<Token.Type, Pattern> map = new LinkedHashMap<>();

        try {

            Files.lines(path).forEach(l -> {
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