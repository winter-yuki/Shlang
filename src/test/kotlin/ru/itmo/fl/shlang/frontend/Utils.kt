package ru.itmo.fl.shlang.frontend

import ru.itmo.fl.shlang.frontend.ast.Stmt
import kotlin.test.assertEquals

internal inline fun test(vararg statements: Stmt, code: () -> String) {
    val parser = Parser()
    val program = parser.parse(code())
    assertEquals(statements.toList(), program.statements)
}
