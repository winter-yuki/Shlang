package ru.itmo.fl.shlang.frontend.ast

sealed interface Expr : AstNode

data class NumberLiteral(val n: Int) : Expr

data class Identifier(val id: String) : Expr

data class BinOpNode(val op: BinOp, val lhs: Expr, val rhs: Expr) : Expr

enum class BinOp(val repr: String) {
    Plus("+"), Minus("-"),
    Mul("*"), Div("/"),
    Pow("^"),
    Or("||"), And("&&"),
    Gt(">"), Ge(">="),
    Lt("<"), Le("<="),
    Eq("=="), Neq("/=");

    companion object {
        fun of(repr: String): BinOp = values().find { it.repr == repr }
            ?: error("Binary operation $repr not found")
    }
}

data class UnOpNode(val op: UnOp, val expr: Expr) : Expr

enum class UnOp(val repr: String) {
    Minus("-"),
    Not("!");

    companion object {
        fun of(repr: String): UnOp = values().find { it.repr == repr }
            ?: error("Unary operation $repr not found")
    }
}

object Input : Expr

data class Output(val expr: Expr) : Expr
