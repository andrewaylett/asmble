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

dependencies {
    constraints {
        implementation("org.jetbrains.kotlin", "kotlin-stdlib") {
            version { require("1.6.+") }
        }
        implementation("org.jetbrains.kotlin", "kotlin-reflect") {
            version { require("1.6.+") }
        }
    }
}
