plugins {
    id("asmble.java-conventions")
}

dependencyLocking {
    lockAllConfigurations()
}

buildscript {
    configurations.classpath {
        resolutionStrategy.activateDependencyLocking()
    }
}

val processExamples: String = System.getProperty("eu.aylett.asmble.processExamples", "no")

if (processExamples == "yes") {
    tasks.register("examples") {
        dependsOn(project.gradle.includedBuild("examples").task(":hello-world:run"))
        dependsOn(project.gradle.includedBuild("examples").task(":configured-c-to-wasm:run"))
    }
}
