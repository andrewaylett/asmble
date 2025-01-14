package eu.aylett.asmble.run.jvm.script

import eu.aylett.asmble.ast.Script
import java.io.Reader
import javax.script.AbstractScriptEngine
import javax.script.Bindings
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.Invocable
import javax.script.ScriptContext
import javax.script.ScriptEngineFactory

abstract class BaseEngine : AbstractScriptEngine(), Compilable, Invocable {

    abstract fun toAst(fileName: String, script: String, ctx: ScriptContext): Script

    fun eval(script: Script, ctx: ScriptContext): Any {
        TODO()
    }

    override fun eval(script: String?, context: ScriptContext?): Any {
        TODO()
    }

    override fun eval(reader: Reader?, context: ScriptContext?): Any {
        TODO()
    }

    override fun createBindings(): Bindings {
        TODO()
    }

    override fun getFactory(): ScriptEngineFactory {
        TODO()
    }

    override fun compile(script: String?): CompiledScript {
        TODO()
    }

    override fun compile(script: Reader?): CompiledScript {
        TODO()
    }

    override fun invokeMethod(thiz: Any?, name: String?, vararg args: Any?): Any {
        TODO()
    }

    override fun <T : Any?> getInterface(clasz: Class<T>?): T {
        TODO()
    }

    override fun <T : Any?> getInterface(thiz: Any?, clasz: Class<T>?): T {
        TODO()
    }

    override fun invokeFunction(name: String?, vararg args: Any?): Any {
        TODO()
    }
}
