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
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileOutputStream

abstract class CompileWasmTask : SourceTask() {

    private data class ClassName(val qualified: String, val simple: String, val packages: List<String>)

    @get:Internal("Tracked by this.getSource")
    val stableSources: FileCollection = project.files(this.source)

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val classNameByFile: MapProperty<String, String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun compile() {
        outputDir.asFile.get().mkdirs()
        stableSources.files.forEach { fileOrDirectory ->
            val rootDirectory = if (fileOrDirectory.isDirectory) {
                fileOrDirectory
            } else {
                project.projectDir
            }
            project.fileTree(fileOrDirectory).visit {
                if (it.isDirectory) return@visit

                val className = resolvePkgAndClassName(it, rootDirectory)
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
                outStream.use {
                    val ctx = ClsContext(
                        packageName = className.packages.joinToString("."),
                        className = className.simple,
                        mod = mod.module,
                        modName = mod.name,
                        logger = asmbleLogger,
                        includeBinary = false
                    )
                    AstToAsm.fromModule(ctx)
                    it.write(AsmToBinary(logger = asmbleLogger).fromClassNode(ctx.cls))
                }
            }
        }
    }

    private fun resolvePkgAndClassName(file: FileVisitDetails, rootDirectory: File): ClassName {
        val relativeClassName = file.file.relativeTo(rootDirectory).path

        val customName = classNameByFile.getting(relativeClassName)

        val fullClassName = joinClassNameWithPkg(customName.get() ?: file.file.nameWithoutExtension)

        val parts = fullClassName.split(".").filter { it.isNotBlank() }
        return ClassName(
            qualified = fullClassName,
            simple = parts.last(),
            packages = parts.dropLast(1)
        )
    }

    private fun joinClassNameWithPkg(name: String): String {
        return if (packageName.get().isBlank()) name else
            "${packageName.get()}.$name"
    }

    private fun classFileFor(name: ClassName): File {
        val dir = name.packages.fold(outputDir.get()) { acc, s -> acc.dir(s) }
        return dir.file("${name.simple}.class").asFile
    }
}
