package eu.aylett.asmble.run.jvm

import eu.aylett.asmble.TestBase
import eu.aylett.asmble.compile.jvm.AsmToBinary
import eu.aylett.asmble.compile.jvm.AstToAsm
import eu.aylett.asmble.compile.jvm.ClsContext
import eu.aylett.asmble.io.BinaryToAst
import org.junit.Assert
import org.junit.Test
import java.util.UUID

class LargeInitFuncTest : TestBase() {
    // This used to fail because large <init> methods didn't properly split
    @Test
    fun testLargeInitFunc() {
        // Build ASM
        val ctx = ClsContext(
            packageName = "test",
            className = "Temp" + UUID.randomUUID().toString().replace("-", ""),
            logger = logger,
            // This was taken from the Rust Regex compile
            mod = BinaryToAst.toModule(javaClass.getResource("/large-init-func-test.wasm")!!.readBytes())
        )
        AstToAsm.fromModule(ctx)
        // Compile
        val bytes = AsmToBinary(logger = logger).fromClassNode(ctx.cls)
        val cl = object : ClassLoader() {
            fun defineClass(name: String, bytes: ByteArray) = defineClass(name, bytes, 0, bytes.size)
        }
        val cls = cl.defineClass("${ctx.packageName}.${ctx.className}", bytes)
        // Just make sure it can init
        var instance: Any? = null
        for (constructor in cls.declaredConstructors) {
            if (constructor.parameters[0].type.isAssignableFrom(Int::class.java)) {
                instance = constructor.newInstance(600 * 65536)
            }
        }
        Assert.assertNotNull("Expected to initialise an instance", instance)
    }
}
