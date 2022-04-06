rootProject.name = "asmble"

includeBuild("conventions")

include("annotations")
include("compiler")
include("cli")
include("gradle-plugin")


val processExamples: String = System.getProperty("eu.aylett.asmble.processExamples", "no")

if (processExamples == "yes") {
    includeBuild("gradle-plugin/examples")
}

plugins {
    id("com.gradle.enterprise") version ("3.9")
}

buildscript {
    configurations.classpath {
        resolutionStrategy.activateDependencyLocking()
    }
}

gradleEnterprise {
    buildScan {
        publishAlways()

        if (System.getProperty("enterprise.tos.agree", "no") == "yes") {
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}
