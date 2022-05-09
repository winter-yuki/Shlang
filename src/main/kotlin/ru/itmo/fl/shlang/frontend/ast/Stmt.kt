package ru.itmo.fl.shlang.frontend.ast

sealed interface Stmt : AstNode

data class AssignmentStmt(val id: String, val expr: Expr) : Stmt

data class IfStmt(val cond: Expr, val then: CompoundStmt, val `else`: CompoundStmt?) : Stmt

data class WhileStmt(val cond: Expr, val body: CompoundStmt) : Stmt

data class CompoundStmt(val statements: List<Stmt>) : Stmt
