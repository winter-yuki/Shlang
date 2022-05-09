package ru.itmo.fl.shlang.runtime

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import ru.itmo.fl.shlang.AbstractShlangTestArgumentProvider
import ru.itmo.fl.shlang.frontend.Parser
import java.io.StringReader
import java.io.StringWriter
import java.nio.file.Path
import kotlin.test.assertEquals

internal class InterpreterTest {

    private val parser = Parser()

    @ParameterizedTest
    @ArgumentsSource(InterpreterArgumentProvider::class)
    fun test(src: String, io: String) {
        val lines = io.split("\n").filter { !it.startsWith("//") }
        val input = lines.filter { it.startsWith("in: ") }.joinToString("\n") { it.drop(4) }
        val output = lines.filter { it.startsWith("out: ") }.joinToString("\n") { it.drop(5) + '\n' }
        val writer = StringWriter()
        val interpreter = Interpreter(
            input = StringReader(input),
            output = writer
        )
        interpreter.execute(src)
        assertEquals(output, writer.toString())
    }
}

private class InterpreterArgumentProvider : AbstractShlangTestArgumentProvider(
    path = Path.of("interpreter")
)
