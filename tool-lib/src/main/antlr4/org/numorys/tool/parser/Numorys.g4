grammar Numorys;

module: MODULE ID NEWLINE* declaration* EOF;

declaration: signature | binding;

signature: function COLON type (ARROW type)*;

binding: function var* EQUALS statement* expr+;

statement: var BIND expr+ SEMI;

expr:
      expr INFIX expr 
	| BRACKET_OPEN expr+ BRACKET_CLOSE
	| var
	| num
	;

function: ID | INFIX;

type: ID;

var: ID;

num: NUMBER;

MODULE: 'module';

COLON: ':';

SEMI: ';';

ARROW: '->';

BIND: '<-';

EQUALS: '=';


BRACKET_OPEN: '(';
BRACKET_CLOSE: ')';

ID : (LETTER|'_') (LETTER|DIGIT|'_'|'\'')*; 

fragment LETTER : [a-zA-Z\u0080-\u00FF_] ; 

NUMBER : '-'? ('.' DIGIT+ | DIGIT+ ('.' DIGIT*)? ) ; 

fragment DIGIT : [0-9] ;

WS : [ \t\n\r]+ -> skip ;

INFIX: ~[0-9a-zA-Z\u0080-\u00FF_ \t\n\r]*;