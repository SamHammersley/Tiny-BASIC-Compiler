# Tiny BASIC compiler

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
<factor> ::= <identifier> | <number> | <expression>
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
There are three different strategies used to tokenize Tiny BASIC source code input. Two of which use regular expressions to find suppported tokens in an input source. The tokenizers throw an exception where there are unsupported/undefined tokens in the input.

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
