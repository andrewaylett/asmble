package eu.aylett.asmble.run.jvm.script

import eu.aylett.asmble.ast.SExpr
import eu.aylett.asmble.io.SExprToAst
import eu.aylett.asmble.io.StrToSExpr
import javax.script.ScriptContext
import javax.script.ScriptException

class TextEngine : BaseEngine() {
    override fun toAst(fileName: String, script: String, ctx: ScriptContext) = StrToSExpr.parse(script).let { res ->
        when (res) {
            is StrToSExpr.ParseResult.Error -> throw ScriptException(res.msg, fileName, res.pos.line, res.pos.char)
            is StrToSExpr.ParseResult.Success -> SExprToAst.toScript(SExpr.Multi(res.vals))
        }
    }
}
