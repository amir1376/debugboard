import org.jetbrains.changelog.markdownToHTML

plugins {
    id("java")
    kotlin("jvm")
    id("org.jetbrains.changelog") version "2.0.0"
    id("org.jetbrains.intellij") version "1.13.2"
}

repositories {
    mavenCentral()
}

configurations.all {
    //fix conflict with IDE and ktor with same dependency
    exclude("org.slf4j", "slf4j-api")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    pluginName.set("DebugBoard")
    version.set("2022.1.4")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}
dependencies {
    implementation(project(":backend:shared-api-models"))
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.websockets)
    implementation("com.jgoodies:jgoodies-binding:2.13.0")
}
tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
    val createDescription by registering {
        val inputFile = project.projectDir.resolve("README.md")
        inputs.file(inputFile)
        val outputFile = project.buildDir.resolve("generatedPluginInfo/description.html")
        outputs.file(outputFile)
        doLast {
            val html = createHtmlFromMarkdown(
                inputFile,
                "<!-- Plugin description -->",
                "<!-- Plugin description end -->",
            )
            outputFile.writeText(html)
        }
    }

    patchPluginXml {
        dependsOn(createDescription)
        sinceBuild.set("221")
        untilBuild.set("231.*")
        pluginId.set("ir.amirab.debugboard.idea-plugin")
        version.set(project.version.toString())
        pluginDescription.set(
            provider {
                project.buildDir.resolve(
                    "generatedPluginInfo/description.html"
                ).readText()
            }
        )
    }

    signPlugin {
        fun String.environmentVariableAsFileProvider(): Provider<RegularFile> = provider {
            val fileString = System.getenv(this) ?: error("$this key is not available in environment variables!")
            with(File(fileString)) {
                if (exists() && isFile) RegularFile { this }
                else error("$this is not file or not exists")
            }
        }

        val certChain = "CERTIFICATE_CHAIN_FILE".environmentVariableAsFileProvider()
        val pk = "PRIVATE_KEY_FILE".environmentVariableAsFileProvider()
        val pkp = provider {
            System.getenv("PRIVATE_KEY_PASSWORD")!!
        }
        certificateChainFile.set(certChain)
        privateKeyFile.set(pk)
        password.set(pkp)
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

fun createHtmlFromMarkdown(
    input: File,
    start: String,
    end: String,
): String {
    val lines = input.readLines()
    val startLine = lines.indexOf(start)
    val endLine = lines.indexOf(end)
    if (startLine == -1 || endLine == -1) {
        throw GradleException("can't find description section!")
    }
    val markdown = lines.subList(startLine + 1, endLine).joinToString("\n")
    return markdownToHTML(markdown)
}
