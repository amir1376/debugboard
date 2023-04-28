plugins {
    kotlin("jvm")
    `maven-publish`
}
dependencies{
    api(project(":core:core-no-op"))
    api(project(":backend:backend-common"))
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