plugins {
    id("asmble.java-conventions")
    id("application")
    id("asmble.publish-conventions")
}

application {
    mainClass.set("asmble.cli.Main")
}

dependencies {
    compileOnly(project(":annotations"))
    testImplementation(project(":annotations"))
    implementation(project(":compiler"))
}
