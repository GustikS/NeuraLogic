grammar Hello;

@header {
//    package parsers.Hello;
}

r   : 'Hello' ID;
ID  : [a-z]+ ;
WS  : [ \t\r\n]+ -> skip ;