plugins {
    java
    kotlin("jvm")
    id("com.diffplug.spotless")
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
    testImplementation("junit", "junit")
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

spotless {
    java {
        prettier(mapOf("prettier" to "2.0.5", "prettier-plugin-java" to "0.8.0")).config(mapOf("parser" to "java", "tabWidth" to 4))
    }
    kotlin {
        ktlint()
    }
}
