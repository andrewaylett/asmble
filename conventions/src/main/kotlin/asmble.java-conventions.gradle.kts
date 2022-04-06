import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
    java
    kotlin("jvm")
    id("com.diffplug.spotless")
    id("org.jetbrains.qodana")
}

group = "eu.aylett.asmble"
version = "1.0." + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)

repositories {
    mavenCentral()
    google()
}

dependencyLocking {
    lockAllConfigurations()
}

var kotlinRequire="1.5.+"
var junitJupiterRequire="5.+"

dependencies {
    testImplementation("org.junit.jupiter","junit-jupiter")
    testCompileOnly("junit","junit")
    testRuntimeOnly("org.junit.vintage","junit-vintage-engine")

    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit")

    constraints {
        implementation("org.jetbrains.kotlin", "kotlin-stdlib") {
            version { require(kotlinRequire) }
        }
        implementation("org.jetbrains.kotlin", "kotlin-reflect") {
            version { require(kotlinRequire) }
        }

        testImplementation("org.junit.jupiter","junit-jupiter") {
            version { require(junitJupiterRequire) }
        }
        testRuntimeOnly("org.junit.vintage","junit-vintage-engine") {
            version { require(junitJupiterRequire) }
        }
        testImplementation("junit", "junit") {
            version { require("4.+") }
        }
        testImplementation("org.jetbrains.kotlin", "kotlin-test-junit") {
            version { require(kotlinRequire) }
        }
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
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
