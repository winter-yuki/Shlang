package ru.itmo.fl.shlang

import ru.itmo.fl.shlang.runtime.Interpreter
import ru.itmo.fl.shlang.runtime.execute
import java.nio.file.Path

fun main(args: Array<String>) {
    val interpreter = Interpreter()
    if (args.isEmpty()) {
        println("Welcome to Shlang!")
        loop(interpreter)
    } else {
        args.forEach { arg ->
            val path = Path.of(arg)
            interpreter.execute(path)
        }
    }
}

private fun loop(interpreter: Interpreter) {
    while (true) {
        print("> ")
        val line = readlnOrNull() ?: break
        interpreter.execute(line)
    }
}
