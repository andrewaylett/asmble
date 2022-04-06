plugins {
    java
    application
    id("eu.aylett.asmble.gradle-plugin")
}

application {
    mainClass.set("Hello")
}

group = "com.athaydes.wasm"
version = "1.0-SNAPSHOT"
