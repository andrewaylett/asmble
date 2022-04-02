package eu.aylett.asmble.compile.jvm

import eu.aylett.asmble.ast.Node
import eu.aylett.asmble.util.Logger

data class FuncContext(
    val cls: ClsContext,
    val node: Node.Func,
    val insns: List<Insn>,
    val memIsLocalVar: Boolean = false
) : Logger by cls.logger {
    fun actualLocalIndex(givenIndex: Int) = node.actualLocalIndex(givenIndex)
}
