# Tiny BASIC compiler
## Usage
The application expects some program arguments, they are as follows:
```
Usage: Tiny BASIC Compiler [-hV] [-g=<graphStructure>] [-o=<outputPath>]
                           [-r=<regexPath>] [-t=<tokenizerType>] <inputPath>
Compiles Tiny BASIC source code to x86-64 Netwide Assembler assembly code
      <inputPath>   The input file, containing Tiny BASIC source code.
  -g, --ast-graph=<graphStructure>
                    Graph file, if specified a dot graph description is
                      generated and written to the given file.
  -h, --help        Show this help message and exit.
  -o, --output-file=<outputPath>
                    The output path, this may be a path to a particular file or
                      a directory in which to create a file. In the case of the
                      latter, the name of the file is that of the input file.
  -r, --regex-file=<regexPath>
                    Specify a text file containing token types with
                      corresponding regular expressions. The format is as
                      follows: TOKEN_TYPE_NAME: REGEX
  -t, --tokenizer=<tokenizerType>
                    Type of tokenizer to tokenize Tiny BASIC input source code.
  -V, --version     Print version information and exit.
```
## Syntax
The following (backus-naur form) rewriting rules outline the supported grammar, these define the syntax of the implemented "flavour" of Tiny BASIC. Note that pre-processing directives are not supported however may possibly be implemented in the future.
```ebnf
<line> ::= <number> <statement> LF

<statement> ::= PRINT <expression-list>
              IF <expression> <relational-op> <expression> THEN <statement>
              INPUT <identifier-list>
              LET <identifier = expression>
              GOTO <expression>
              GOSUB <expression>
              RETURN
              END

<digit> ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
<letter> ::= A | B | C | D | E | F | G | H | I | J | K | L | M | N | O | P | Q | R | S | T | U | V | W | X | Y | Z | a | b | c | d | e | f | g | h | i | j | k | l | m | n | o | p | q | r | s | t | u | v | w | x | y
<factor> ::= <identifier> | <number> | (<expression>)
<term> ::= <factor> [(* | /) <factor>]*
<expression> ::= [+ | -] <term> [(+ | -) <term>]*
<expression-list> ::= <expression> [, <expression>]*
<identifier> ::= <letter>
<identifier-list> ::= <identifier> [, <identifier>]*
<number> ::= <digit> <digit>*
<relational-op> ::= < [> | =] | > [< | =] | =
<string> ::= "[<letter> | <digit>]*"
```
## Tokenization
There are three different strategies used to tokenize Tiny BASIC source code input. As seen above, there is an option to specify which of these is used to tokenize source code input. This option expects a fully qualified java class name, the available tokenizer types are:
 * uk.ac.tees.tokenizer.flag.FlagTokenizer
 * uk.ac.tees.tokenizer.regex.group.GroupingRegexTokenizer
 * uk.ac.tees.tokenizer.regex.sequential.SequentialRegexTokenizer

Two of the tokenizers use regular expressions to find supported tokens in an input source. The tokenizers throw an exception where there are unsupported/undefined tokens in the input.

### Regex Tokenizers
The difference between these two tokenization strategies is rooted in the order by which tokens are matched.

One iterates over the supported type regular expression patterns, checking if each of the patterns match some portion of the input at the start of the string. 

The other however, iterates over the patterns and finds all matches in the input string. These matches are then sorted by their corresponding starting index (the index at which the match occurs in the input string) for the proper ordering of tokens.

Regular expressions for supported token types can be specified in a file in the following format (seperated by a new line):
```
TokenTypeName: regular expression
STRING_EXPRESSION: "[^"]*"
```
To do this use the FromFileProvider implementation of RegexPatternsProvider, giving the file path string.

### Tokenizing by matching conditions for each individual character
Finally, the remaining tokenization strategy is to traverse the string checking each character satisfies a particular condition. For multicharacter token types, the character queue is polled until the condition ,that dictates if the current character is of a certain token type, is not met.

### DOT Graph description
If the -g option is specified then the application will write out to a text file a DOT graph description for the, parsed, given program.
