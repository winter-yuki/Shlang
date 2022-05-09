package ru.itmo.fl.shlang.frontend

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import ru.itmo.fl.shlang.AbstractShlangTestArgumentProvider
import ru.itmo.fl.shlang.frontend.ast.AssignmentStmt
import ru.itmo.fl.shlang.frontend.ast.BinOp
import ru.itmo.fl.shlang.frontend.ast.BinOpNode
import ru.itmo.fl.shlang.frontend.ast.CompoundStmt
import ru.itmo.fl.shlang.frontend.ast.ExprStmt
import ru.itmo.fl.shlang.frontend.ast.Identifier
import ru.itmo.fl.shlang.frontend.ast.IfStmt
import ru.itmo.fl.shlang.frontend.ast.NumberLiteral
import ru.itmo.fl.shlang.frontend.ast.WhileStmt
import java.nio.file.Path
import kotlin.test.assertEquals

internal class ParserTest {

    private val parser = Parser()

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentProvider::class)
    fun test(src: String, expected: String) {
        val program = parser.parse(src)
        assertEquals(expected, program.toString())
    }

    @Test
    fun `if`() = test(
        IfStmt(
            cond = BinOpNode(BinOp.Eq, Identifier("a"), Identifier("b")),
            then = CompoundStmt(listOf(ExprStmt(NumberLiteral(1)))),
            `else` = CompoundStmt(listOf(ExprStmt(NumberLiteral(2))))
        )
    ) {
        """
            | if (a == b) { 1; } else { 2; }
        """.trimMargin()
    }

    @Test
    fun `while`() = test(
        WhileStmt(
            cond = BinOpNode(BinOp.Neq, Identifier("a"), Identifier("b")),
            body = CompoundStmt(statements = listOf(
                AssignmentStmt(
                    id = "a",
                    expr = BinOpNode(BinOp.Plus, Identifier("a"), NumberLiteral(1))
                ),
                AssignmentStmt(
                    id = "b",
                    expr = BinOpNode(BinOp.Minus, Identifier("b"), NumberLiteral(1))
                )
            ))
        )
    ) {
        """
            |while (a /= b) {
            |    a = a + 1;
            |    b = b - 1;
            |}
        """.trimMargin()
    }
}

private class ParserArgumentProvider : AbstractShlangTestArgumentProvider(
    path = Path.of("parser"),
)
