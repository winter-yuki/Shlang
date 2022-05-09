package ru.itmo.fl.shlang

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText

internal abstract class AbstractShlangTestArgumentProvider(
    private val path: Path,
    private val srcExtension: String = "shl",
    private val resExtension: String = "txt",
    private val root: Path = Path.of("src/test/resources/"),
) : ArgumentsProvider {

    init {
        require(srcExtension != resExtension)
    }

    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> =
        paths().stream().map { (src, res) -> Arguments.of(src.readText(), res.readText()) }

    private fun paths(): List<Pair<Path, Path>> {
        val files = Files.walk(root.resolve(path)).filter { it.isRegularFile() }.collect(Collectors.toList())
        require(files.size % 2 == 0) { "Tests are not paired with expectations" }
        return files.sorted().windowed(size = 2, step = 2).map { window ->
            val (src, res) = if (srcExtension < resExtension) window else window.reversed()
            require(src.nameWithoutExtension == res.nameWithoutExtension) { "src = $src, res = $res" }
            require(src.extension == srcExtension)
            require(res.extension == resExtension)
            src to res
        }
    }
}
