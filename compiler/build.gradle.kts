plugins {
    id("asmble.java-conventions")
    id("java-library")
}

dependencies {
    implementation(project(":annotations"))
    testImplementation(project(":annotations"))
    api("org.jetbrains.kotlin", "kotlin-stdlib")
    implementation("org.jetbrains.kotlin", "kotlin-reflect")
    api("org.ow2.asm", "asm-tree", "6.+")
    implementation("org.ow2.asm", "asm-util", "6.+")
    implementation("org.ow2.asm", "asm-commons", "6.+")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testCompileOnly("junit:junit:4.13")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
}
