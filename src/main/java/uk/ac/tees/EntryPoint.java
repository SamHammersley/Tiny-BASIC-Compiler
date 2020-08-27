package uk.ac.tees;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import uk.ac.tees.codegeneration.x86_64.X86_64NetwideAssemblyGenerator;
import uk.ac.tees.semantics.ProgramSemanticsAnalyser;
import uk.ac.tees.syntax.grammar.Program;
import uk.ac.tees.syntax.parser.Parser;
import uk.ac.tees.syntax.parser.RecursiveDescentParser;
import uk.ac.tees.syntax.parser.TokenSupplier;
import uk.ac.tees.syntax.parser.exception.ParseException;
import uk.ac.tees.syntax.visitor.GraphDescriptionVisitor;
import uk.ac.tees.tokenizer.TinyBasicTokenizer;
import uk.ac.tees.tokenizer.Token;
import uk.ac.tees.tokenizer.TokenizationException;
import uk.ac.tees.tokenizer.flag.FlagTokenizer;
import uk.ac.tees.tokenizer.regex.patterns.FromURLProvider;
import uk.ac.tees.tokenizer.regex.patterns.TokenizerPatternsCache;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "Tiny BASIC Compiler",
        mixinStandardHelpOptions = true,
        description = "Compiles Tiny BASIC source code to x86-64 Netwide Assembler assembly code")
public final class EntryPoint implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(EntryPoint.class.getName());

    @Option(names = {"-t", "--tokenizer"},
            description = "Type of tokenizer to tokenize Tiny BASIC input source code.")
    private Class<? extends TinyBasicTokenizer> tokenizerType = FlagTokenizer.class;

    @Option(names = {"-g", "--ast-graph"},
            description = "Graph file, if specified a dot graph description is generated and written to the given file.")
    private Path graphStructure;

    @Option(names = {"-o", "--output-file"},
            description = "The output path, this may be a path to a particular file or a directory in which to create" +
                    " a file. In the case of the latter, the name of the file is that of the input file.")
    private Path outputPath;

    @Option(names = {"-r", "--regex-file"},
            description = "Specify a text file containing token types with corresponding regular expressions. " +
                    "The format is as follows: TOKEN_TYPE_NAME: REGEX")
    private URL regexPath = EntryPoint.class.getClassLoader().getResource("regex");

    @Parameters(description = "The input file, containing Tiny BASIC source code.")
    private Path inputPath;

    public static void main(String[] args) {
        System.exit(new CommandLine(new EntryPoint()).execute(args));
    }

    @Override
    public void run() {
        String input = readInput();
        TinyBasicTokenizer tokenizer = instantiateTokenizer();

        try {
            TokenSupplier supplier = new TokenSupplier(tokenizer.tokenize(input));

            String fileName = inputPath.getFileName().toString();
            String name = fileName.substring(0, fileName.lastIndexOf('.'));

            Parser parser = new RecursiveDescentParser(supplier);
            Program abstractSyntaxTree = new ProgramSemanticsAnalyser().visitTree(parser.parse(name));

            compile(abstractSyntaxTree);

            Optional.ofNullable(graphStructure).ifPresent(path -> graphAbstractSyntaxTree(abstractSyntaxTree, path));

        } catch (TokenizationException e) {
            LOGGER.log(Level.SEVERE, "Error tokenizing: " + input, e);

        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, "Error parsing token queue!", e);
        }
    }

    /**
     * Reads input from the file at {@link #inputPath}.
     *
     * @return the Tiny BASIC source code read from the file at {@link #inputPath}.
     */
    private String readInput() {
        try {
            return Files.readString(inputPath);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading from file " + inputPath, e);

            throw new UncheckedIOException(e);
        }
    }

    /**
     * Creates an instance of the {@link #tokenizerType} class. This instance is used to tokenize given Tiny BASIC
     * source code.
     *
     * @return a {@link TinyBasicTokenizer} instance that is of type {@link #tokenizerType}.
     */
    private TinyBasicTokenizer instantiateTokenizer() {
        try {
            if (tokenizerType.equals(FlagTokenizer.class)) {
                return tokenizerType.getConstructor().newInstance();
            }

            return tokenizerType.getConstructor(TokenizerPatternsCache.class).newInstance(getRegexCache());

        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.SEVERE, "Error instantiating tokenizer: " + tokenizerType, e);

            throw new RuntimeException("Failed to instantiate tokenizer!", e);
        }
    }

    /**
     * Gets the regular expressions that define the grammar, the syntactic structure, of the Tiny BASIC language.
     *
     * @return an instance of {@link TokenizerPatternsCache} containing all the regular expressions for the different
     * {@link Token.Type}s.
     */
    private TokenizerPatternsCache getRegexCache() {
        return new FromURLProvider(regexPath).newCache();
    }

    /**
     * Generates a DOT graph description for the given {@link Program} as the root node of the abstract syntax tree.
     *
     * @param program the program to create a graph description of.
     * @param path    the {@link Path} to write the DOT graph description to.
     */
    private void graphAbstractSyntaxTree(Program program, Path path) {
        GraphDescriptionVisitor visitor = new GraphDescriptionVisitor(program.getName());
        String graphDescription = visitor.visitTree(program);

        if (Files.isDirectory(path)) {
            path = path.resolve(program.getName() + ".gv");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(graphDescription);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing graph description!", e);
        }
    }

    /**
     * Compiles the given program to x86-64 Netwide Assembler assembly code. The output is written to the file at
     * {@link #outputPath} if one has been specified or at a file named {@link Program#getName()} and extension .asm
     * in the current directory.
     *
     * @param program the program to compile.
     */
    private void compile(Program program) {
        X86_64NetwideAssemblyGenerator compiler = new X86_64NetwideAssemblyGenerator();
        String output = compiler.visitTree(program);

        Path path = Optional.ofNullable(outputPath).orElse(inputPath.getParent().resolve(program.getName() + ".asm"));

        if (Files.isDirectory(path)) {
            path = path.resolve(program.getName() + ".asm");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(output);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing output to file " + outputPath, e);

            throw new UncheckedIOException(e);
        }

        System.out.println(path.toAbsolutePath());
    }

}