import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    `maven-publish`
}
sourceSets {
    all {
        resources {
            srcDir(buildDir.resolve("npmOutput"))
        }
    }
}
tasks {
    val copyFrontendAssets by registering(Copy::class) {
        from(project(":ui:web").tasks["npmRunBuild"])
        val myDestinationPath = buildDir.resolve("npmOutput/web")
        into(myDestinationPath)
        doFirst {
            myDestinationPath.deleteRecursively()
        }
    }
    compileJava.dependsOn(copyFrontendAssets)
}

dependencies {
    api(project(":core:core"))
    api(project(":backend:backend-common"))
    implementation(project(":backend:shared-api-models"))

    implementation(libs.kotlin.serialization.core)
    implementation(libs.kotlin.serialization.json)

    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.serialization.kotlinxJson)

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