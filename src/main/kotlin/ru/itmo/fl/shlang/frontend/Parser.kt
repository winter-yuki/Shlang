package ru.itmo.fl.shlang.frontend

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.itmo.fl.shlang.frontend.antlr.ShlangLexer
import ru.itmo.fl.shlang.frontend.antlr.ShlangParser
import ru.itmo.fl.shlang.frontend.antlr.ShlangParserBaseVisitor
import ru.itmo.fl.shlang.frontend.ast.AssignmentStmt
import ru.itmo.fl.shlang.frontend.ast.BinOp
import ru.itmo.fl.shlang.frontend.ast.BinOpNode
import ru.itmo.fl.shlang.frontend.ast.CompoundStmt
import ru.itmo.fl.shlang.frontend.ast.Expr
import ru.itmo.fl.shlang.frontend.ast.ExprStmt
import ru.itmo.fl.shlang.frontend.ast.Identifier
import ru.itmo.fl.shlang.frontend.ast.IfStmt
import ru.itmo.fl.shlang.frontend.ast.NumberLiteral
import ru.itmo.fl.shlang.frontend.ast.Program
import ru.itmo.fl.shlang.frontend.ast.Stmt
import ru.itmo.fl.shlang.frontend.ast.UnOp
import ru.itmo.fl.shlang.frontend.ast.UnOpNode
import ru.itmo.fl.shlang.frontend.ast.WhileStmt

class ParseFailedException(e: Exception) : RuntimeException(e);

class Parser {
    fun parse(src: String): Program = try {
        val stream = CharStreams.fromString(src)
        val lexer = ShlangLexer(stream)
        val tokens = CommonTokenStream(lexer)
        val parser = ShlangParser(tokens)
        val root = parser.program()
        ProgramVisitor().visitProgram(root)
    } catch (e: Exception) {
        throw ParseFailedException(e)
    }
}

private class ProgramVisitor(private val stmtVisitor: StmtVisitor = StmtVisitor()) :
    ShlangParserBaseVisitor<Program>() {

    override fun visitProgram(ctx: ShlangParser.ProgramContext): Program {
        val statements = ctx.stmt().map(stmtVisitor::visit)
        return Program(statements)
    }
}

private class StmtVisitor(private val exprVisitor: ExprVisitor = ExprVisitor()) : ShlangParserBaseVisitor<Stmt>() {

    override fun visitExprStmt(ctx: ShlangParser.ExprStmtContext): Stmt {
        val expr = exprVisitor.visit(ctx.expr())
        return ExprStmt(expr)
    }

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

    override fun visitNumber(ctx: ShlangParser.NumberContext): Expr {
        val number = ctx.NUMBER().text.toInt()
        return NumberLiteral(number)
    }

    override fun visitIdentifier(ctx: ShlangParser.IdentifierContext): Expr {
        val id = ctx.IDENTIFIER().text
        return Identifier(id)
    }

    override fun visitUnaryA(ctx: ShlangParser.UnaryAContext): Expr {
        val op = UnOp.of(ctx.op.text)
        val expr = visit(ctx.arithExpr())
        return UnOpNode(op, expr)
    }

    override fun visitUnaryB(ctx: ShlangParser.UnaryBContext): Expr {
        val op = UnOp.of(ctx.op.text)
        val expr = visit(ctx.booleanExpr())
        return UnOpNode(op, expr)
    }

    override fun visitBinaryA(ctx: ShlangParser.BinaryAContext): Expr {
        val op = BinOp.of(ctx.op.text)
        val lhs = visit(ctx.lhs)
        val rhs = visit(ctx.rhs)
        return BinOpNode(op, lhs, rhs)
    }

    override fun visitBinaryB(ctx: ShlangParser.BinaryBContext): Expr {
        val op = BinOp.of(ctx.op.text)
        val lhs = visit(ctx.lhs)
        val rhs = visit(ctx.rhs)
        return BinOpNode(op, lhs, rhs)
    }

    override fun visitBinaryC(ctx: ShlangParser.BinaryCContext): Expr {
        val op = BinOp.of(ctx.op.text)
        val lhs = visit(ctx.lhs)
        val rhs = visit(ctx.rhs)
        return BinOpNode(op, lhs, rhs)
    }

    override fun visitParensA(ctx: ShlangParser.ParensAContext): Expr {
        return visit(ctx.arithExpr())
    }

    override fun visitParensB(ctx: ShlangParser.ParensBContext): Expr {
        return visit(ctx.booleanExpr())
    }

    override fun visitParensC(ctx: ShlangParser.ParensCContext): Expr {
        return visit(ctx.cmpExpr())
    }
}
