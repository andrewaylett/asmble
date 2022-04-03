rootProject.name = "asmble"
include("annotations")
include("compiler")
include("cli")

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
