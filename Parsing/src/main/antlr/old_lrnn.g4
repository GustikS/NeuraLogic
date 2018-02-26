grammar old_lrnn;

// Parser rules based on defined tokens

term: CONSTANT | VARIABLE;
term_list: LPAREN (term (COMMA term)*)? RPAREN;
member_term_list: LPAREN VARIABLE COMMA (constant_list | VARIABLE) RPAREN;

normal_atomic_formula: PREDICATE term_list;
member_formula:  MEMBER_PREDICATE member_term_list;
builtin_formula: INTERNAL_PREDICATE term_list | TRUE;
special_formula: member_formula | builtin_formula;
atomic_formula:  special_formula | normal_atomic_formula;

constant_list: LBRACKET (CONSTANT (COMMA CONSTANT)*)? RBRACKET;

fact: weight? atomic_formula DOT;

weighted_conjunction: weight atomic_formula (COMMA atomic_formula)* DOT;

weight: LANGLE SIGNED_NUMBER RANGLE | SIGNED_NUMBER ;

metadata_value: ACTIVATION_FUNCTION | CARET VARIABLE;
metadata: LBRACKET (metadata_value (COMMA metadata_value)*)? RBRACKET;

lrnn_rule: weight? atomic_formula IMPLIED_BY atomic_formula (COMMA atomic_formula)* DOT metadata?;

predicate_metadata: (PREDICATE | INTERNAL_PREDICATE) SLASH INT metadata;
predicate_offset: (PREDICATE | INTERNAL_PREDICATE) SLASH INT weight;

rule_line: lrnn_rule | fact | predicate_metadata | predicate_offset;
example_line: lrnn_rule;
old_example_line: weighted_conjunction;
rule_file: rule_line+ | example_line+ | old_example_line+;


// Lexer tokens and universal constants

//STRING_INNER: ('\\\"'|[^"]);
//ESCAPED_STRING: '\"' STRING_INNER* '\"';

MEMBER_PREDICATE: 'member' | '_member';

CONSTANT: [a-z][a-zA-Z0-9_]*;
VARIABLE: [A-Z] ALPHANUMERIC*;
PREDICATE: [a-zA-Z_]ALPHANUMERIC*;
ACTIVATION_FUNCTION:  LETTER (LETTER | DIGIT) *;

INTERNAL_PREDICATE: '@' (LETTER | DIGIT) *;

ALPHANUMERIC: LCASE_LETTER | UCASE_LETTER | DIGIT | '_';

// generic chars
IMPLIED_BY: ':-';
LANGLE: '<';
RANGLE: '>';
LBRACKET: '[';
RBRACKET: ']';
LPAREN: '(';
RPAREN: ')';
DOT: '.';
COMMA: ',';
SLASH: '/';
CARET: '^';
TRUE: 'true';

// generic types
fragment LCASE_LETTER: ('a'..'z');
fragment UCASE_LETTER: ('A'..'Z');
fragment DIGIT: ('0'..'9');

SIGNED_NUMBER: ('+'|'-')? NUMBER;
INT: DIGIT+;
fragment SIGNED_INT: ('+'|'-')? INT;
fragment DECIMAL: INT '.' INT? | '.' INT;
fragment EXP: ('e'|'E') SIGNED_INT;
fragment FLOAT: INT EXP | DECIMAL EXP?;

fragment LETTER: UCASE_LETTER | LCASE_LETTER;

fragment NUMBER: FLOAT | INT;

// ignore whitespace
//WS : (' ' | '\t')+ -> channel(HIDDEN);
WS
   : [ \t\r\n]+ -> skip
   ;


COMMENT: '%' ~[\n\r]* ( [\n\r] | EOF) -> channel(HIDDEN) ;
MULTILINE_COMMENT: '/*' ( MULTILINE_COMMENT | . )*? ('*/' | EOF) -> channel(HIDDEN);