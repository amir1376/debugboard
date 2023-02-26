import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version "3.5.1"
}

node{
    download.set(true)
}

val npmRunBuild by tasks.registering(NpmTask::class) {
    dependsOn(tasks.npmInstall)
    npmCommand.set(listOf("run", "build"))
    inputs.files(
        "index.html",
        "package.json",
        "package-lock.json",
        "postcss.config.js",
        "tailwind.config.js",
        "tsconfig.json",
        "tsconfig.node.json",
        "vite.config.ts",
    )
    inputs.dir("src")
    inputs.dir(
        fileTree(".")
            .exclude("dist")
            .exclude()
    )
    inputs.dir(fileTree("node_modules").exclude(".cache"))
    outputs.dir("dist")
}
val archivedAssets by tasks.registering(Zip::class) {
    dependsOn(npmRunBuild)
    from("dist")
    val myDestinationPath = buildDir.resolve(name)
    destinationDirectory.set(myDestinationPath)
    archiveFileName.set("assets.zip")

    doFirst {
        myDestinationPath.deleteRecursively()
    }
}