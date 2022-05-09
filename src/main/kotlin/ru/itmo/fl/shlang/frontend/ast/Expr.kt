package ru.itmo.fl.shlang.frontend.ast

sealed interface Expr : AstNode

data class NumberLiteral(val n: Int) : Expr

data class BinaryOperation(val op: Operation, val lhs: Expr, val rhs: Expr)

enum class Operation(val repr: String) {
    Plus("+"), Minus("-"),
    Mul("*"), Div("/"),
    Pow("^"),
    Or("||"), And("&&"),
    Not("!"),
    Gt(">"), Ge(">="),
    Lt("<"), Le("<="),
    Eq("=="), Neq("/=");

    companion object {
        fun of(repr: String): Operation =
            Operation.values().find { it.repr == repr }
                ?: error("Operation $repr not found")
    }
}

object Input : Expr

data class Output(val expr: Expr) : Expr
