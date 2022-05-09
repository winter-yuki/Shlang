package ru.itmo.fl.shlang.frontend.ast

data class Program(val statements: List<Stmt>) : AstNode
