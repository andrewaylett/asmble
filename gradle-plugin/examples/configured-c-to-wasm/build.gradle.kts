plugins {
    java
    application
    id("eu.aylett.asmble.gradle-plugin")
}
group = "com.athaydes.wasm"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.athaydes.wasm.c.Main")
}

val wasmDir = "build/generated/sources/wasm/main"
tasks.register("compileC") {
    description = "Compiles C sources into WASM binaries"
    inputs.dir( "src/main/c" )
    outputs.dir( wasmDir )

    doFirst {
        project.file(wasmDir).mkdirs()
    }
    doLast {
        exec {
            // See https://dassur.ma/things/c-to-webassembly/ for details about this
            commandLine = listOf("clang", "--target=wasm32", "-nostdlib",
                    "-Wl,--no-entry", "-Wl,--export-all", "-o", "${wasmDir}/com.athaydes.wasm.c.Adder.wasm",
                    "src/main/c/add.c")
        }
    }
}

sourceSets {
    main {
        wasm {
            srcDir(tasks.getByName("compileC").outputs)
        }
    }
}
