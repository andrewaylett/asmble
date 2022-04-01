plugins {
    id("asmble.java-conventions")
    id("java-library")
}

dependencies {
    implementation(project(":annotations"))
    testImplementation(project(":annotations"))
    implementation("org.jetbrains.kotlin", "kotlin-stdlib")
    implementation("org.jetbrains.kotlin", "kotlin-reflect")
    api("org.ow2.asm", "asm-tree", "6.+")
    implementation("org.ow2.asm", "asm-util", "6.+")
    implementation("org.ow2.asm", "asm-commons", "6.+")
}
