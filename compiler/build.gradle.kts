plugins {
    id("asmble.java-conventions")
    id("java-library")
    id("asmble.publish-conventions")
}

dependencies {
    implementation(project(":annotations"))
    testImplementation(project(":annotations"))
    api("org.jetbrains.kotlin", "kotlin-stdlib")
    implementation("org.jetbrains.kotlin", "kotlin-reflect")
    api("org.ow2.asm", "asm-tree", "6.+")
    implementation("org.ow2.asm", "asm-util", "6.+")
    implementation("org.ow2.asm", "asm-commons", "6.+")
}
