lexer grammar ShlangLexer;

@header {
package ru.itmo.fl.shlang.frontend.antlr;
}

IF:    'if';
ELSE:  'else';
WHILE: 'while';

SEMICOLON:     ';';
OPEN_PARENS:   '(';
CLOSE_PARENS:  ')';
OPEN_BRACKET:  '{';
CLOSE_BRACKET: '}';
ASSIGNMENT:    '=';

fragment UNDERSCORE: '_';
fragment ALPHA:      [a-zA-Z];
fragment DIGIT:      [0-9];

NUMBER:     [1-9] DIGIT*;
IDENTIFIER: (UNDERSCORE | ALPHA) (UNDERSCORE | ALPHA | DIGIT)*;

fragment LINE_CHAR:   ~[\n\r];
fragment NEW_LINE:    '\n' | '\r\n';
fragment WHITE_SPACE: [ \t];

COMMENT: '//' LINE_CHAR* -> skip;
SPACE:   (WHITE_SPACE | NEW_LINE)+ -> skip;
