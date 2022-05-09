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
    : exprStmt
    | assignmentStmt
    | ifStmt
    | whileStmt
    | compoundStmt
    ;

exprStmt
    : expr SEMICOLON
    ;

assignmentStmt
    : identifier ASSIGNMENT expr SEMICOLON
    ;

ifStmt
    : IF OPEN_PARENS cond=expr CLOSE_PARENS
        then=compoundStmt
        (ELSE else=compoundStmt)?
    ;

whileStmt
    : WHILE OPEN_PARENS cond=expr CLOSE_PARENS
        body=compoundStmt
    ;

compoundStmt
    : OPEN_BRACKET stmt* CLOSE_BRACKET
    ;

expr
    : arithExpr
    | cmpExpr
    | booleanExpr
    ;

arithExpr
    : number                                                          #numberA
    | identifier                                                      #identifierA
    | OPEN_PARENS arithExpr CLOSE_PARENS                              #parensA
    | <assoc=right> lhs=arithExpr op=POW rhs=arithExpr                #binaryA
    | op=MINUS arithExpr                                              #unaryA
    | <assoc=left> lhs=arithExpr op=(MUL | DIV) rhs=arithExpr         #binaryA
    | <assoc=left> lhs=arithExpr op=(PLUS | MINUS) rhs=arithExpr      #binaryA
    ;

cmpExpr
    : OPEN_PARENS cmpExpr CLOSE_PARENS                                #parensC
    | lhs=arithExpr op=(LT | LE | GT | GE | EQ | NEQ) rhs=arithExpr   #binaryC
    ;

booleanExpr
    : identifier                                                      #identifierB
    | cmpExpr                                                         #cmpB
    | OPEN_PARENS booleanExpr CLOSE_PARENS                            #parensB
    | op=NOT booleanExpr                                              #unaryB
    | <assoc=right> lhs=booleanExpr op=AND rhs=booleanExpr            #binaryB
    | <assoc=right> lhs=booleanExpr op=OR rhs=booleanExpr             #binaryB
    ;

identifier
    : IDENTIFIER
    ;

number
    : NUMBER
    ;
