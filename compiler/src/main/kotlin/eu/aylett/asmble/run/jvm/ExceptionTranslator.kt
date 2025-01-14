package eu.aylett.asmble.run.jvm

import java.lang.invoke.WrongMethodTypeException
import java.nio.charset.MalformedInputException
import java.util.Locale

open class ExceptionTranslator {
    fun translate(ex: Throwable): List<String> = when (ex) {
        is ArithmeticException -> when (ex.message) {
            "/ by zero", "BigInteger divide by zero" -> listOf("integer divide by zero")
            else -> listOf(ex.message!!.replaceFirstChar { it.lowercase(Locale.getDefault()) })
        }
        is ArrayIndexOutOfBoundsException ->
            listOf("out of bounds memory access", "undefined element", "elements segment does not fit")
        is eu.aylett.asmble.AsmErr -> ex.asmErrStrings
        is IndexOutOfBoundsException -> listOf("out of bounds memory access")
        is MalformedInputException -> listOf("invalid UTF-8 encoding")
        is NullPointerException -> listOf("undefined element", "uninitialized element")
        is StackOverflowError -> listOf("call stack exhausted")
        is UnsupportedOperationException -> listOf("unreachable executed")
        is WrongMethodTypeException -> listOf("indirect call type mismatch")
        is NumberFormatException -> listOf("i32 constant")
        else -> emptyList()
    }

    companion object : ExceptionTranslator()
}
