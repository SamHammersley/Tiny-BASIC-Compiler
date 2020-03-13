package uk.ac.tees.tokenization.regex.provider;

import uk.ac.tees.tokenization.Token;
import uk.ac.tees.tokenization.regex.RegexTokenizerPatternsCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class FromFileProvider implements RegexPatternsProvider {

    private final String file;

    public FromFileProvider(String file) {
        this.file = file;
    }

    @Override
    public RegexTokenizerPatternsCache cache() {
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
            e.printStackTrace();
        }

        return new RegexTokenizerPatternsCache(map);
    }
}