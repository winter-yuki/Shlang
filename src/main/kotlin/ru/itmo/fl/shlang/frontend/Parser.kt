package ru.itmo.fl.shlang.frontend

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.itmo.fl.shlang.frontend.antlr.ShlangLexer
import ru.itmo.fl.shlang.frontend.antlr.ShlangParser
import ru.itmo.fl.shlang.frontend.antlr.ShlangParserBaseVisitor
import ru.itmo.fl.shlang.frontend.ast.AssignmentStmt
import ru.itmo.fl.shlang.frontend.ast.CompoundStmt
import ru.itmo.fl.shlang.frontend.ast.Expr
import ru.itmo.fl.shlang.frontend.ast.IfStmt
import ru.itmo.fl.shlang.frontend.ast.NumberLiteral
import ru.itmo.fl.shlang.frontend.ast.Program
import ru.itmo.fl.shlang.frontend.ast.Stmt
import ru.itmo.fl.shlang.frontend.ast.WhileStmt

class Parser {
    fun parse(src: String): Program {
        val stream = CharStreams.fromString(src)
        val lexer = ShlangLexer(stream)
        val tokens = CommonTokenStream(lexer)
        val parser = ShlangParser(tokens)
        val root = parser.program()
        return ProgramVisitor().visitProgram(root)
    }
}

private class ProgramVisitor(private val stmtVisitor: StmtVisitor = StmtVisitor()) :
    ShlangParserBaseVisitor<Program>() {

    override fun visitProgram(ctx: ShlangParser.ProgramContext): Program {
        val statements = ctx.stmt().map(stmtVisitor::visit)
        return Program(statements)
    }
}

private class StmtVisitor(val exprVisitor: ExprVisitor = ExprVisitor()) : ShlangParserBaseVisitor<Stmt>() {
    override fun visitAssignmentStmt(ctx: ShlangParser.AssignmentStmtContext): Stmt {
        val id = ctx.identifier().IDENTIFIER().text
        val expr = exprVisitor.visit(ctx.expr())
        return AssignmentStmt(id, expr)
    }

    override fun visitIfStmt(ctx: ShlangParser.IfStmtContext): Stmt {
        val cond = exprVisitor.visitExpr(ctx.cond)
        val then = visitCompoundStmt(ctx.then)
        val `else` = ctx.else_?.let { visitCompoundStmt(it) }
        return IfStmt(cond, then, `else`)
    }

    override fun visitWhileStmt(ctx: ShlangParser.WhileStmtContext): Stmt {
        val cond = exprVisitor.visitExpr(ctx.cond)
        val body = visitCompoundStmt(ctx.body)
        return WhileStmt(cond, body)
    }

    override fun visitCompoundStmt(ctx: ShlangParser.CompoundStmtContext): CompoundStmt {
        val statements = ctx.stmt().map { visitStmt(it) }
        return CompoundStmt(statements)
    }
}

private class ExprVisitor : ShlangParserBaseVisitor<Expr>() {
    override fun visitExpr(ctx: ShlangParser.ExprContext): Expr {
        val number = ctx.NUMBER().text.toInt()
        return NumberLiteral(number)
    }
}
