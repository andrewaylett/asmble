plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.5.31"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
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
    implementation("org.jetbrains.kotlin", "kotlin-gradle-plugin", "1.5.31")
    implementation("com.diffplug.spotless","spotless-plugin-gradle","6.25.0")
    implementation("org.jetbrains.qodana:gradle-qodana-plugin:0.1.13")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
    }
}
