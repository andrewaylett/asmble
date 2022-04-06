plugins {
    `maven-publish`
}

configure<PublishingExtension> {
    repositories {
        maven {
            name = "gpr"
            url = uri("https://maven.pkg.github.com/andrewaylett/asmble")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
