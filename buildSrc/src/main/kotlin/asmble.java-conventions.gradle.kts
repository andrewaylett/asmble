plugins {
    java
    kotlin("jvm")
}

group = "eu.aylett.asmble"
version = "1.0"

repositories {
    mavenCentral()
}

dependencyLocking {
    lockAllConfigurations()
}

dependencies {
    testImplementation("junit","junit")
    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit")
    constraints {
        testImplementation("junit", "junit") {
            version { require("4.+") }
        }
        testImplementation("org.jetbrains.kotlin", "kotlin-test-junit") {
            version { require("1.6.+") }
        }
    }
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
    }
}
