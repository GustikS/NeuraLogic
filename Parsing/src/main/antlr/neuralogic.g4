grammar neuralogic;

//beware of changes - the antlr4 lexer is greedy and it's not easy to write the grammar correctly!
template_file: template_line*;
template_line: lrnn_rule | fact | predicate_metadata | predicate_offset;

// examples may come in following formats:
//labeled: label query literal :- conjunction of atoms
//unlabeled: one big conjunction or one fact per line
examples_file: lrnn_rule+ | fact+ | conjunction+;

fact: atom '.';

atom: weight? predicate term_list?;

term_list: LPAREN (term (COMMA term)*)? RPAREN;

//no function symbols support yet
term: constant | variable;

variable: VARIABLE;
constant: ATOMIC_NAME | INT | FLOAT;
// on the level of the syntactic parser, predicates are indistinguishible from constants
predicate: ATOMIC_NAME | special_predicate; //predicates begin with lower-case letter!

special_predicate: '@' predicate;

conjunction: atom (COMMA atom)*;

metadata_val: ATOMIC_NAME '=' constant;
metadata_list: LBRACKET (metadata_val (COMMA metadata_val)*)? RBRACKET;

lrnn_rule: atom IMPLIED_BY conjunction offset? '.' metadata_list?;

predicate_offset: predicate SLASH INT weight;
predicate_metadata: predicate SLASH INT metadata_list;

//weight: fixed_weight | SEPARATOR (INT | FLOAT) SEPARATOR;
//SEPARATOR: (' ' | BOL | EOF);

weight: fixed_weight | (INT | FLOAT);
fixed_weight: LANGLE (INT | FLOAT) RANGLE;
offset: weight;

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

ALPHANUMERIC: ALPHA | DIGIT ;
ALPHA: '_' | '-' | LCASE_LETTER | UCASE_LETTER ;

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