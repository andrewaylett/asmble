package eu.aylett.asmble.cli

open class Help : Command<Help.Args>() {

    override val name = "help"
    override val desc = "Show command help"

    override fun args(bld: ArgsBuilder) = Args(
        command = bld.arg(
            name = "command",
            desc = "The command to see details for."
        )
    ).also { bld.done() }

    override fun run(args: Args) {
        val command = commands.find { it.name == args.command } ?: error("Unable to find command '${args.command}'")
        val argDefBld = ArgsBuilder.ArgDefBuilder()
        Main.globalArgs(argDefBld)
        command.args(argDefBld)

        val out = StringBuilder()
        out.appendLine("Command: ${command.name}")
        out.appendLine("Description: ${command.desc}")
        out.appendLine("Usage:")
        out.append("  ${command.name} ")
        argDefBld.argDefs.forEach { if (!it.lowPriority) it.argString(out).append(' ') }
        out.appendLine().appendLine()
        out.append("Args:")
        argDefBld.argDefs.sorted().forEach { out.appendLine().append("  ").let(it::descString) }
        println(out)
    }

    data class Args(val command: String)

    companion object : Help()
}