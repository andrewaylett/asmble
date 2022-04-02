package eu.aylett.asmble.compile.jvm

sealed class Insn {
    data class Node(val insn: eu.aylett.asmble.ast.Node.Instr) : Insn()
    data class ImportFuncRefNeededOnStack(val index: Int) : Insn()
    data class ImportGlobalSetRefNeededOnStack(val index: Int) : Insn()
    object ThisNeededOnStack : Insn()
    object MemNeededOnStack : Insn()
}
