package eu.aylett.asmble.compile.jvm

import eu.aylett.asmble.TestBase
import eu.aylett.asmble.io.SExprToAst
import eu.aylett.asmble.io.StrToSExpr
import eu.aylett.asmble.run.jvm.ModuleBuilder
import org.junit.Test
import java.util.UUID

class NamesTest : TestBase() {
    @Test
    fun testNames() {
        // Compile and make sure the names are set right
        val (_, mod) = SExprToAst.toModule(
            StrToSExpr.parseSingleMulti(
                """
            (module ${'$'}mod_name
                (import "foo" "bar" (func ${'$'}import_func (param i32)))
                (type ${'$'}some_sig (func (param ${'$'}type_param i32)))
                (func ${'$'}some_func
                    (type ${'$'}some_sig)
                    (param ${'$'}func_param i32)
                    (local ${'$'}func_local0 i32)
                    (local ${'$'}func_local1 f64)
                )
            )
                """.trimIndent()
            )
        )
        val ctx = ClsContext(
            packageName = "test",
            className = "Temp" + UUID.randomUUID().toString().replace("-", ""),
            mod = mod,
            logger = logger
        )
        AstToAsm.fromModule(ctx)
        val cls = ModuleBuilder.Compiled.SimpleClassLoader(javaClass.classLoader, logger).fromBuiltContext(ctx)
        // Make sure the import field and the func are present named
        cls.getDeclaredField("import_func")
        cls.getDeclaredMethod("some_func", Integer.TYPE)
    }
}
