package eu.aylett.asmble

import eu.aylett.asmble.util.Logger

abstract class TestBase : Logger by logger {
    companion object {
        val logger = Logger.Print(Logger.Level.INFO)
    }
}
