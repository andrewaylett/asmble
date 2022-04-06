plugins {
    id("asmble.java-conventions")
    `java-gradle-plugin`
    id("asmble.publish-conventions")
}

dependencies {
    implementation(project(":compiler"))
    api(project(":annotations"))
}

gradlePlugin {
    plugins {
        create("asmbleGradlePlugin") {
            id = "eu.aylett.asmble.gradle-plugin"
            implementationClass = "com.athaydes.wasm.WasmGradlePlugin"
        }
    }
}
