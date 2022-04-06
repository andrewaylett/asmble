rootProject.name = "wasm-on-jvm-examples"

include("hello-world", "configured-c-to-wasm")

pluginManagement {
    @Suppress("UnstableApiUsage")
    includeBuild("../../")
}
