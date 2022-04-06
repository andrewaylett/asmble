package com.athaydes.wasm

import eu.aylett.asmble.ast.SExpr
import eu.aylett.asmble.ast.Script
import eu.aylett.asmble.compile.jvm.AsmToBinary
import eu.aylett.asmble.compile.jvm.AstToAsm
import eu.aylett.asmble.compile.jvm.ClsContext
import eu.aylett.asmble.io.BinaryToAst
import eu.aylett.asmble.io.ByteReader
import eu.aylett.asmble.io.SExprToAst
import eu.aylett.asmble.io.StrToSExpr
import eu.aylett.asmble.util.Logger
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.compile.AbstractCompile
import java.io.File
import java.io.FileOutputStream

@CacheableTask
abstract class CompileWasmTask : AbstractCompile() {

    private data class ClassName(val qualified: String, val simple: String, val packages: List<String>)

    @TaskAction
    fun compile() {
        logger.debug("Compiling WASM to {}", destinationDirectory.get())
        destinationDirectory.asFile.get().mkdirs()
        this.source.files.forEach { fileOrDirectory ->
            logger.info("Compiling WASM from {}", fileOrDirectory)
            project.fileTree(fileOrDirectory).visit {
                if (it.isDirectory) return@visit

                val className = resolvePkgAndClassName(it)
                val classFile = classFileFor(className)

                classFile.parentFile?.mkdirs()

                logger.info("Compiling wasm file $it to class ${className.qualified}")

                data class GradleLogger(override val level: Logger.Level) : Logger {
                    override fun log(atLevel: Logger.Level, str: String) {
                        val level = when (atLevel) {
                            Logger.Level.TRACE -> LogLevel.DEBUG
                            Logger.Level.DEBUG -> LogLevel.DEBUG
                            Logger.Level.INFO -> LogLevel.INFO
                            Logger.Level.WARN -> LogLevel.WARN
                            Logger.Level.ERROR -> LogLevel.ERROR
                            Logger.Level.OFF -> LogLevel.QUIET
                        }
                        logger.log(level, str)
                    }
                }

                val asmbleLogger = GradleLogger(Logger.Level.TRACE)

                // Get format
                val inFormat = it.file.extension
                val inBytes = it.file.readBytes()
                val script = when (inFormat) {
                    "wast" -> StrToSExpr.parse(inBytes.toString(Charsets.UTF_8)).let { res ->
                        when (res) {
                            is StrToSExpr.ParseResult.Error -> error("Error [${res.pos.line}:${res.pos.char}] - ${res.pos}")
                            is StrToSExpr.ParseResult.Success -> SExprToAst.toScript(SExpr.Multi(res.vals))
                        }
                    }
                    "wasm" ->
                        Script(
                            listOf(
                                Script.Cmd.Module(
                                    BinaryToAst(logger = asmbleLogger).toModule(
                                        ByteReader.InputStream(inBytes.inputStream())
                                    ),
                                    null
                                )
                            )
                        )
                    else -> error("Unknown in format '$inFormat'")
                }
                val mod =
                    (script.commands.firstOrNull() as? Script.Cmd.Module)
                        ?: error("Only a single sexpr for (module) allowed")
                val outStream = FileOutputStream(classFile)
                outStream.use { out ->
                    val ctx = ClsContext(
                        packageName = className.packages.joinToString("."),
                        className = className.simple,
                        mod = mod.module,
                        modName = mod.name,
                        logger = asmbleLogger,
                        includeBinary = false
                    )
                    AstToAsm.fromModule(ctx)
                    out.write(AsmToBinary(logger = asmbleLogger).fromClassNode(ctx.cls))
                }
            }
        }
    }

    private fun resolvePkgAndClassName(file: FileVisitDetails): ClassName {
        val fullClassName = file.file.nameWithoutExtension

        val parts = fullClassName.split(".").filter { it.isNotBlank() }
        return ClassName(
            qualified = fullClassName,
            simple = parts.last(),
            packages = parts.dropLast(1)
        )
    }

    private fun classFileFor(name: ClassName): File {
        val dir = name.packages.fold(destinationDirectory.get()) { acc, s -> acc.dir(s) }
        return dir.file("${name.simple}.class").asFile
    }
}
