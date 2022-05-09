package ru.itmo.fl.shlang.frontend

import org.junit.jupiter.api.Test
import ru.itmo.fl.shlang.frontend.ast.BinOp
import ru.itmo.fl.shlang.frontend.ast.BinOpNode
import ru.itmo.fl.shlang.frontend.ast.Expr
import ru.itmo.fl.shlang.frontend.ast.ExprStmt
import ru.itmo.fl.shlang.frontend.ast.Identifier
import ru.itmo.fl.shlang.frontend.ast.NumberLiteral
import ru.itmo.fl.shlang.frontend.ast.UnOp
import ru.itmo.fl.shlang.frontend.ast.UnOpNode

internal class ExprTest {

    @Test
    fun number() = test(
        NumberLiteral(42)
    ) {
        "42"
    }

    @Test
    fun `unary minus`() = test(
        UnOpNode(op = UnOp.Minus, expr = NumberLiteral(1))
    ) {
        "  - 1 "
    }

    @Test
    fun plus() = test(
        BinOpNode(
            op = BinOp.Plus,
            lhs = NumberLiteral(1),
            rhs = NumberLiteral(2)
        )
    ) {
        "  1 + 2"
    }

    @Test
    fun `plus plus`() = test(
        BinOpNode(
            op = BinOp.Plus,
            lhs = BinOpNode(
                op = BinOp.Plus,
                lhs = NumberLiteral(1),
                rhs = NumberLiteral(2)
            ),
            rhs = NumberLiteral(3)
        )
    ) {
        "1 + 2 + 3"
    }

    @Test
    fun `plus (plus)`() = test(
        BinOpNode(
            op = BinOp.Plus,
            lhs = NumberLiteral(1),
            rhs = BinOpNode(
                op = BinOp.Plus,
                lhs = NumberLiteral(2),
                rhs = NumberLiteral(3)
            )
        )
    ) {
        " 1 + (2 + 3)"
    }

    @Test
    fun `plus plus (minus) plus`() = test(
        BinOpNode(
            op = BinOp.Minus,
            lhs = BinOpNode(
                op = BinOp.Plus,
                lhs = BinOpNode(
                    op = BinOp.Minus,
                    lhs = NumberLiteral(1),
                    rhs = NumberLiteral(2)
                ),
                rhs = BinOpNode(
                    op = BinOp.Minus,
                    lhs = NumberLiteral(3),
                    rhs = NumberLiteral(4)
                )
            ),
            rhs = NumberLiteral(5)
        )
    ) {
        "1 - 2 + (3 - 4) - 5"
    }

    @Test
    fun `mul and div`() = test(
        BinOpNode(
            op = BinOp.Div,
            lhs = BinOpNode(
                op = BinOp.Mul,
                lhs = NumberLiteral(1),
                rhs = Identifier("_1")
            ),
            rhs = BinOpNode(
                op = BinOp.Mul,
                lhs = Identifier("b1"),
                rhs = NumberLiteral(4)
            )
        )
    ) {
        " 1 * _1 / (b1 * 4)"
    }

    @Test
    fun pow() = test(
        BinOpNode(
            op = BinOp.Plus,
            lhs = BinOpNode(
                op = BinOp.Mul,
                lhs = Identifier("a"),
                rhs = Identifier("b")
            ),
            rhs = BinOpNode(
                op = BinOp.Pow,
                lhs = NumberLiteral(3),
                rhs = BinOpNode(
                    op = BinOp.Pow,
                    lhs = Identifier("a"),
                    rhs = NumberLiteral(2)
                )
            )
        )
    ) {
        "a * b + 3 ^ (a) ^ 2"
    }

    @Test
    fun boolean() = test(
        BinOpNode(
            op = BinOp.Or,
            lhs = BinOpNode(BinOp.Lt, Identifier("a"), Identifier("b")),
            rhs = BinOpNode(
                op = BinOp.And,
                lhs = BinOpNode(BinOp.Neq, Identifier("c"), NumberLiteral(3)),
                rhs = Identifier("a")
            )
        )
    ) {
        " a < b || c /= 3 && a"
    }
}

private inline fun test(expr: Expr, code: () -> String) = test(
    ExprStmt(expr)
) {
    code() + ';'
}
