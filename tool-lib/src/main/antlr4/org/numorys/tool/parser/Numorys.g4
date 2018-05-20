grammar Numorys;

module: MODULE ID declaration* NEWLINE? EOF;

declaration: signature | binding;

signature: NEWLINE function DBL_COLON type;

binding: NEWLINE function param* EQUALS statement* expr;

param: var | num;

statement: NEWLINE WS var BIND expr+ SEMI;

expr:
	 expr ((BRACKET_OPEN expr+ BRACKET_CLOSE) | var | num)+
    | <assoc=right> expr INFIX expr 
    | BRACKET_OPEN expr+ BRACKET_CLOSE
	| var
	| num
	;

function: ID | INFIX;

type: 
	(  bracket_type
	| simple_type )
	(ARROW (  bracket_type
		| simple_type )
		)*
	;

bracket_type: BRACKET_OPEN type BRACKET_CLOSE;
simple_type: ID;

var: ID;

num: NUMBER;

MODULE: 'module';

DBL_COLON: '::';

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

NEWLINE : [\n\r]+;

WS : [ \t]+ -> skip;

INFIX: ~[0-9a-zA-Z\u0080-\u00FF_ \t\n\r]+;