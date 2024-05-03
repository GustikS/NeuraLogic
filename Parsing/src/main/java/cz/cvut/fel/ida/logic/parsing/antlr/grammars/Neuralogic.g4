grammar Neuralogic;

//beware of changes - the antlr4 lexer is greedy and it's not easy to write the grammar correctly!
templateFile: templateLine*;
// format : valid line is either normal rule, or simple true fact, or conjunction of facts (constraint - for future)
// the rest of lines is metadata
templateLine: lrnnRule | fact | (conjunction '.') | predicateMetadata | predicateOffset | weightMetadata | templateMetadata;

//trainExamples may come in following formats (overloading the lrnn_rule):
//labeled: label query literals :- lifted examples, where the label query literal may also be a <link> to the queries file
//unlabeled: one big lifted example or one example per line (label query literals in separate queries file then)
examplesFile: (label impliedBy liftedExample)+ | liftedExample+ ;
//Examples may also contain rules (separated by whitespace, rules must end with '.' as well as example)
liftedExample: ((lrnnRule | conjunction)+ '.');
//label can be either <link> or one or more valued query literals themselves
label: conjunction;

// format with <link> :- query literals (lrnn_rule)
// or simple labeled trainQueries, one or more per line (line-to-line correspondence with example file)
queriesFile: (atom impliedBy conjunction '.')+ | (conjunction '.')+;

// atomic true statement
fact: atom '.';

atom: weight? negation? predicate termList?;

termList: LPAREN (term (COMMA term)*)? RPAREN;

//no function symbols support just yet
term: constant | variable;

variable: TYPE? VARIABLE;
constant: TYPE? ATOMIC_NAME | TYPE? INT | TYPE? FLOAT;
// on the level of the syntactic parser, predicates are indistinguishible from constants
predicate: PRIVATE? SPECIAL? ATOMIC_NAME (SLASH INT)?; //predicates also begin with lower-case letter!

conjunction: atom (COMMA atom)*;

metadataVal: ATOMIC_NAME ASSIGN (value | (DOLLAR? ATOMIC_NAME));
metadataList: LBRACKET (metadataVal (COMMA metadataVal)*)? RBRACKET;

lrnnRule: atom impliedBy conjunction (',' offset)? '.' metadataList?;

predicateOffset: predicate weight;
predicateMetadata: predicate metadataList;
weightMetadata: DOLLAR ATOMIC_NAME metadataList;
templateMetadata: metadataList;

// weights may have identifiers for explicit sharing
weight: (DOLLAR ATOMIC_NAME ASSIGN)? (fixedValue | value);
fixedValue: LANGLE value RANGLE;
offset: weight;

value: number | sparseVector | vector | sparseMatrix| matrix | dimensions;

number: INT | FLOAT;
vector: LBRACKET number (COMMA number)* RBRACKET;
sparseVector: LBRACKET INT PIPE ( | element (COMMA element)*) RBRACKET;
matrix: LBRACKET vector+ RBRACKET;
sparseMatrix: LBRACKET INT COMMA INT PIPE ( | element2d (COMMA element2d)*)  RBRACKET;
dimensions: LCURL number (COMMA number)* RCURL;

element: INT ':' number | LPAREN INT ':' number RPAREN;
element2d: INT COMMA INT ':' number | LPAREN INT COMMA INT ':' number RPAREN;

negation: NEGATION | SOFTNEGATION;

impliedBy: IMPLIED_BY | IMPLIED_BY2;

VARIABLE: UCASE_LETTER ALPHANUMERIC* | '_' ALPHANUMERIC+ | '_';

TYPE: ALPHA ALPHANUMERIC* ':';

// numbers
INT: [+-]? DIGIT+;
FLOAT: [+-]? DIGIT+ '.' DIGIT+ ( [eE] [+-]? DIGIT+ )?;

// i.e. any name of an atom (predicate or constant)
ATOMIC_NAME: TRUE | LCASE_LETTER ALPHANUMERIC*;

// generic chars
IMPLIED_BY: ':-';
IMPLIED_BY2: '<=';
ASSIGN: '=';
LCURL: '{';
RCURL: '}';
LANGLE: '<';
RANGLE: '>';
LBRACKET: '[';
RBRACKET: ']';
LPAREN: '(';
RPAREN: ')';
COMMA: ',';
SLASH: '/';
CARET: '^';
TRUE: 'true';
DOLLAR: '$';
NEGATION: '!';
SOFTNEGATION: '~';
SPECIAL: '@';
PRIVATE: '*';
PIPE: '|';

fragment ALPHANUMERIC: ALPHA | DIGIT ;
fragment ALPHA: '_' | '-' | LCASE_LETTER | UCASE_LETTER ;

fragment LCASE_LETTER: [a-z];
fragment UCASE_LETTER: [A-Z];
fragment DIGIT: [0-9];

fragment BOL : [\r\n\f]+ ;

// Be careful - all whitespace is completely IGNORED! (including new lines)
//WS : (' ' | '\t')+ -> channel(HIDDEN);
WS : [ \t\r\n]+ -> skip;

// ignore comments
COMMENT: '%' ~[\n\r]* ( [\n\r] | EOF) -> channel(HIDDEN) ;
MULTILINE_COMMENT: '/*' ( MULTILINE_COMMENT | . )*? ('*/' | EOF) -> channel(HIDDEN);