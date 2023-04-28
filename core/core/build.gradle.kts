plugins {
    kotlin("jvm")
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

kotlin {
    jvmToolchain(8)
}
dependencies {
    api(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.reflect)
    api(project(":core:core-common"))
}
