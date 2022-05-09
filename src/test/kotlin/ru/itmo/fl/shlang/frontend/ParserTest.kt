package ru.itmo.fl.shlang.frontend

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import ru.itmo.fl.shlang.AbstractShlangTestArgumentProvider
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
}

private class ParserArgumentProvider : AbstractShlangTestArgumentProvider(
    path = Path.of("parser"),
)
