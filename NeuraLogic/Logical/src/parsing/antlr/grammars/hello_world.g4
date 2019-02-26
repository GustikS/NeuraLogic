grammar hello_world;
rule2   : 'Hello' WORD+;
WORD  : [a-z] ;
WS  : [ \t\r\n]+ -> skip ;