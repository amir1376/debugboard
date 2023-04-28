import com.android.SdkConstants
import com.google.common.base.Charsets
import org.gradle.configurationcache.extensions.capitalized
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization")
}

val localProperties=loadLocalProperties(project.rootDir)

// We add something like android debug and release build variants but in a desktop application
val debug by sourceSets.registering
val release by sourceSets.registering

val debugImplementation by configurations.getting
val releaseImplementation by configurations.getting

configurations.implementation{
    // debug | release
    val sourceSetName=localProperties.getOrElse("samples.buildType"){"debug"}
    extendsFrom(configurations.named("$sourceSetName${name.capitalized()}").get())
}

// add run[variantName] tasks
listOf(
    debug, release
).forEach {
    val mySourceSet = it.get()
    tasks.register("run" + mySourceSet.name.capitalized(), JavaExec::class) {
        dependsOn(
            tasks.classes,
            mySourceSet.name + "Classes",
        )
        val output = mySourceSet.runtimeClasspath + sourceSets.main.get().runtimeClasspath
        classpath += output
        mainClass.set("MainKt")
    }
}


dependencies {
    implementation(libs.kotlin.coroutines.core)

    debugImplementation(project(":core:core"))
    releaseImplementation(project(":core:core-no-op"))

    debugImplementation(project(":backend:backend"))
    releaseImplementation(project(":backend:backend-no-op"))

    debugImplementation(project(":plugins:network:ktor"))
    releaseImplementation(project(":plugins:network:ktor-no-op"))

    debugImplementation(project(":plugins:network:okhttp"))
    releaseImplementation(project(":plugins:network:okhttp-no-op"))

    debugImplementation(project(":plugins:watcher:compose"))
    releaseImplementation(project(":plugins:watcher:compose-no-op"))

    implementation(compose.desktop.currentOs)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.client.loging)
    implementation(libs.ktor.serialization.kotlinxJson)

    implementation(libs.retrofit.retrofit)
    implementation(libs.retrofit.gsonConverter)

    val fakerVersion = "1.14.0-rc.0"
    implementation("io.github.serpro69:kotlin-faker:$fakerVersion")
}

// load local.properties helper function
fun loadLocalProperties(projectRootDir : File) : Properties {
    val properties = Properties()
    val localProperties = File(projectRootDir, SdkConstants.FN_LOCAL_PROPERTIES)

    if (localProperties.isFile) {
        InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    }
    return properties
}