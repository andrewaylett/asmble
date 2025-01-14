package eu.aylett.asmble.cli

import eu.aylett.asmble.ast.Script
import eu.aylett.asmble.compile.jvm.javaIdent
import eu.aylett.asmble.run.jvm.Module
import eu.aylett.asmble.run.jvm.ModuleBuilder
import eu.aylett.asmble.run.jvm.ScriptContext
import java.io.File
import java.util.Locale
import java.util.UUID

abstract class ScriptCommand<T> : Command<T>() {

    fun scriptArgs(bld: ArgsBuilder) = ScriptArgs(
        inFiles = bld.args(
            name = "inFile",
            opt = "in",
            desc = "Files to add to classpath. Can be wasm, wast, or class file. " + "Named wasm/wast modules here are automatically registered unless -noreg is set.",
            default = emptyList()
        ),
        registrations = bld.args(
            name = "registration",
            opt = "reg",
            desc = "Register class name to a module name. Format: modulename=classname.",
            default = emptyList()
        ).map {
            it.split('=').also { require(it.size == 2) { "Invalid modulename=classname pair" } }.let { it[0] to it[1] }
        },
        disableAutoRegister = bld.flag(
            opt = "noreg", desc = "If set, this will not auto-register modules with names.", lowPriority = true
        ),
        specTestRegister = bld.flag(
            opt = "testharness", desc = "If set, registers the spec test harness as 'spectest'.", lowPriority = true
        ),
        defaultMaxMemPages = bld.arg(
            name = "defaultMaxMemPages",
            opt = "defmaxmempages",
            desc = "The maximum number of memory pages when a module doesn't say.",
            default = "5",
            lowPriority = true
        ).toInt()
    )

    fun prepareContext(args: ScriptArgs): ScriptContext {
        val builder = ModuleBuilder.Compiled(
            packageName = "asmble.temp" + UUID.randomUUID().toString().replace("-", ""),
            logger = logger,
            defaultMaxMemPages = args.defaultMaxMemPages
        )
        var ctx = ScriptContext(logger = logger, builder = builder)
        // Compile everything
        ctx = args.inFiles.foldIndexed(ctx) { index, ctxx, inFile ->
            try {
                when (inFile.substringAfterLast('.')) {
                    "class" -> builder.classLoader.addClass(File(inFile).readBytes()).let { ctxx }
                    else -> Translate.inToAst(inFile, inFile.substringAfterLast('.')).let { inAst ->
                        val (mod, name) = (inAst.commands.singleOrNull() as? Script.Cmd.Module)
                            ?: error("Input file must only contain a single module")
                        val className =
                            name?.javaIdent?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                ?: ("Temp" + UUID.randomUUID().toString().replace("-", ""))
                        ctxx.withBuiltModule(mod, className, name).let { ctx ->
                            if (name == null && index != args.inFiles.size - 1) logger.warn { "File '$inFile' not last and has no name so will be unused" }
                            if (name == null || args.disableAutoRegister) ctx
                            else ctx.runCommand(Script.Cmd.Register(name, null))
                        }
                    }
                }
            } catch (e: Exception) {
                throw Exception("Failed loading $inFile - ${e.message}", e)
            }
        }
        // Do registrations
        ctx = args.registrations.fold(ctx) { ctxx, (moduleName, className) ->
            @Suppress("DEPRECATION") ctxx.withModuleRegistered(
                Module.Native(moduleName, Class.forName(className, true, builder.classLoader).newInstance())
            )
        }
        if (args.specTestRegister) ctx = ctx.withHarnessRegistered()
        return ctx
    }

    data class ScriptArgs(
        val inFiles: List<String>,
        val registrations: List<Pair<String, String>>,
        val disableAutoRegister: Boolean,
        val specTestRegister: Boolean,
        val defaultMaxMemPages: Int
    )
}
