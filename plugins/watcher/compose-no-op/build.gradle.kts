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
    api(project(":core:core-no-op"))
    implementation(compose.runtime)
}