plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
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
    implementation(libs.ktor.client.core)
}