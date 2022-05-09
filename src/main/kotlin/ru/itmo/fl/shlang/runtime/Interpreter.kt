package ru.itmo.fl.shlang.runtime

import ru.itmo.fl.shlang.ShlangException
import ru.itmo.fl.shlang.frontend.Parser
import ru.itmo.fl.shlang.frontend.ast.AssignmentStmt
import ru.itmo.fl.shlang.frontend.ast.BinOp
import ru.itmo.fl.shlang.frontend.ast.BinOpNode
import ru.itmo.fl.shlang.frontend.ast.CompoundStmt
import ru.itmo.fl.shlang.frontend.ast.Expr
import ru.itmo.fl.shlang.frontend.ast.ExprStmt
import ru.itmo.fl.shlang.frontend.ast.Identifier
import ru.itmo.fl.shlang.frontend.ast.IfStmt
import ru.itmo.fl.shlang.frontend.ast.NumberLiteral
import ru.itmo.fl.shlang.frontend.ast.Print
import ru.itmo.fl.shlang.frontend.ast.Program
import ru.itmo.fl.shlang.frontend.ast.Read
import ru.itmo.fl.shlang.frontend.ast.Stmt
import ru.itmo.fl.shlang.frontend.ast.UnOp
import ru.itmo.fl.shlang.frontend.ast.UnOpNode
import ru.itmo.fl.shlang.frontend.ast.WhileStmt
import java.io.BufferedReader
import java.io.Reader
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path

abstract class ExecutionFailedException(message: String) : ShlangException(message)

class UnknownVariableException(name: String) : ExecutionFailedException("Variable $name is not defined")

class InputStreamClosedException : ExecutionFailedException("Input stream is already closed")

class NumberIsExpectedInInputException(value: String) : ExecutionFailedException("Number is expected but got $value")

class Interpreter(
    input: Reader = System.`in`.reader(),
    private val output: Writer = System.out.writer(),
    private val state: MutableMap<String, Int> = mutableMapOf(),
) {
    private val input = BufferedReader(input)

    fun execute(program: Program) {
        program.statements.forEach { stmt ->
            execute(stmt)
        }
    }

    private fun execute(stmt: Stmt) {
        when (stmt) {
            is AssignmentStmt -> state[stmt.id] = eval(stmt.expr)
            is CompoundStmt -> stmt.statements.forEach { execute(it) }
            is ExprStmt -> eval(stmt.expr)
            is IfStmt -> if (eval(stmt.cond) != 0) execute(stmt.then) else stmt.`else`?.let(::execute)
            is WhileStmt -> {
                while (eval(stmt.cond) != 0) {
                    execute(stmt.body)
                }
            }
        }
    }

    private fun eval(expr: Expr): Int =
        when (expr) {
            is BinOpNode -> eval(expr.op, eval(expr.lhs), eval(expr.rhs))
            is Identifier -> state[expr.id] ?: throw UnknownVariableException(expr.id)
            is NumberLiteral -> expr.n
            is Print -> eval(expr.expr).also {
                output.write("$it\n")
                output.flush()
            }
            is Read -> {
                val line = input.readLine() ?: throw InputStreamClosedException()
                val num = line.toIntOrNull() ?: throw NumberIsExpectedInInputException(line)
                num
            }
            is UnOpNode -> eval(expr.op, eval(expr.expr))
        }

    private fun eval(op: BinOp, l: Int, r: Int): Int =
        when (op) {
            BinOp.Plus -> l + r
            BinOp.Minus -> l - r
            BinOp.Mul -> l * r
            BinOp.Div -> l / r
            BinOp.Pow -> l pow r
            BinOp.Or -> if (l != 0) l else r
            BinOp.And -> if (l == 0) 0 else r
            BinOp.Gt -> if (l > r) 1 else 0
            BinOp.Ge -> if (l >= r) 1 else 0
            BinOp.Lt -> if (l < r) 1 else 0
            BinOp.Le -> if (l <= r) 1 else 0
            BinOp.Eq -> if (l == r) 1 else 0
            BinOp.Neq -> if (l != r) 1 else 0
        }

    private fun eval(op: UnOp, num: Int): Int =
        when (op) {
            UnOp.Minus -> -num
            UnOp.Not -> if (num == 0) 1 else num
        }
}

fun Interpreter.execute(src: String) {
    val parser = Parser()
    val program = parser.parse(src)
    execute(program)
}

fun Interpreter.execute(path: Path) {
    val text = Files.readString(path)
    execute(text)
}
