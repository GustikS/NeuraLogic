grammar hello_world;
rule2   : 'Hello' ID+;
ID  : [a-z] ;
WS  : [ \t\r\n]+ -> skip ;