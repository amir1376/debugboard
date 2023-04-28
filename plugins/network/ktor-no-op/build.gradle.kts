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
    api(project(":core:core-no-op"))
    implementation(libs.ktor.client.core)
}