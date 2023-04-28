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
dependencies {
    implementation(libs.okhttp.okhttp)
    implementation(libs.okhttp.loggingInterceptor)
    api(project(":core:core"))
}