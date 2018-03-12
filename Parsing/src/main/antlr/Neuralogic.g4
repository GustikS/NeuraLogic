grammar Neuralogic;

//beware of changes - the antlr4 lexer is greedy and it's not easy to write the grammar correctly!
template_file: template_line*;
template_line: lrnn_rule | fact | predicate_metadata | predicate_offset | weight_metadata;

// examples may come in following formats:
//labeled: label query literal :- conjunction of atoms
//unlabeled: one big conjunction
//           or one fact or conjunction per line (label query literals in separate file)
examples_file: lrnn_rule+ | fact+ | (conjunction '.')+;

// simple labeled queries, one per line
queries_file: fact+;

fact: atom '.';

atom: weight? (negation)? predicate term_list?;

term_list: LPAREN (term (COMMA term)*)? RPAREN;

//no function symbols support just yet
term: constant | variable;

variable: VARIABLE;
constant: ATOMIC_NAME | INT | FLOAT;
// on the level of the syntactic parser, predicates are indistinguishible from constants
predicate: SPECIAL? ATOMIC_NAME (SLASH INT)?; //predicates also begin with lower-case letter!

conjunction: atom (COMMA atom)*;

metadata_val: ATOMIC_NAME '=' DOLLAR? ATOMIC_NAME;
metadata_list: LBRACKET (metadata_val (COMMA metadata_val)*)? RBRACKET;

lrnn_rule: atom IMPLIED_BY conjunction offset? '.' metadata_list?;

predicate_offset: predicate weight;
predicate_metadata: predicate metadata_list;
weight_metadata: DOLLAR ATOMIC_NAME metadata_list;

//weight: fixed_weight | SEPARATOR (INT | FLOAT) SEPARATOR;
//SEPARATOR: (' ' | BOL | EOF);

// weight may have identifiers for sharing
weight: (DOLLAR ATOMIC_NAME '=')? (fixed_value | value);
fixed_value: LANGLE value RANGLE;
offset: weight;

value: number | vector;

number: INT | FLOAT;
vector: LPAREN number (COMMA number)* RPAREN;

negation: NEGATION;

VARIABLE: UCASE_LETTER ALPHANUMERIC* | '_' ALPHANUMERIC+ | '_';

// numbers
INT: [+-]? DIGIT+;
FLOAT: [+-]? DIGIT+ '.' DIGIT+ ( [eE] [+-]? DIGIT+ )?;

// i.e. any name of an atom (predicate or constant)
ATOMIC_NAME: LCASE_LETTER ALPHANUMERIC*;

// generic chars
IMPLIED_BY: ':-';
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
NEGATION: '~';
SPECIAL: '@';

fragment ALPHANUMERIC: ALPHA | DIGIT ;
fragment ALPHA: '_' | '-' | LCASE_LETTER | UCASE_LETTER ;

fragment LCASE_LETTER: [a-z];
fragment UCASE_LETTER: [A-Z];
fragment DIGIT: [0-9];

fragment BOL : [\r\n\f]+ ;

// ignore whitespace
//WS : (' ' | '\t')+ -> channel(HIDDEN);
WS : [ \t\r\n]+ -> skip;

// ignore comments
COMMENT: '%' ~[\n\r]* ( [\n\r] | EOF) -> channel(HIDDEN) ;
MULTILINE_COMMENT: '/*' ( MULTILINE_COMMENT | . )*? ('*/' | EOF) -> channel(HIDDEN);