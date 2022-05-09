parser grammar ShlangParser;

@header {
package ru.itmo.fl.shlang.frontend.antlr;
}

options {
  tokenVocab=ShlangLexer;
}

program
    : stmt* EOF
    ;

stmt
    : assignmentStmt
    | ifStmt
    | whileStmt
    | compoundStmt
    ;

assignmentStmt
    : identifier ASSIGNMENT expr SEMICOLON
    ;

ifStmt
    : IF OPEN_PARENS cond=expr CLOSE_PARENS then=compoundStmt (ELSE else=compoundStmt)?
    ;

whileStmt
    : WHILE OPEN_PARENS cond=expr CLOSE_PARENS body=compoundStmt
    ;

compoundStmt
    : OPEN_BRACKET stmt* CLOSE_BRACKET
    ;

identifier
    : IDENTIFIER
    ;

expr
    : NUMBER
    ;
