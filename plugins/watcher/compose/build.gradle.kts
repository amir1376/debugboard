plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    `maven-publish`
}
java {
    withJavadocJar()
    withSourcesJar()
}
publishing {
    publications {
        create("maven", MavenPublication::class) {
            afterEvaluate {
                from(components["java"])
            }
        }
    }
}
dependencies {
    implementation(project(":core"))
    implementation(compose.runtime)
}