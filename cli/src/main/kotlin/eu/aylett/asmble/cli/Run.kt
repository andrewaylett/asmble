package eu.aylett.asmble.cli

import eu.aylett.asmble.run.jvm.ScriptContext

open class Run : ScriptCommand<Run.Args>() {

    override val name = "run"
    override val desc = "Run WebAssembly script commands"

    override fun args(bld: ArgsBuilder) = Args(
        scriptArgs = scriptArgs(bld),
        script = bld.arg(
            name = "scriptFile",
            desc = "The script file to run all commands for. This can be '--' for stdin. Must be wast format."
        )
    ).also { bld.done() }

    override fun run(args: Args) {
        val ctx = prepareContext(args.scriptArgs)
        val ast = Translate.inToAst(args.script, "wast")
        ast.commands.fold(ctx, ScriptContext::runCommand)
    }

    data class Args(
        val scriptArgs: ScriptArgs,
        val script: String
    )

    companion object : Run()
}
