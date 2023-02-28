plugins{
//    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}
kotlin{
    jvm()
    sourceSets{
        commonMain{
            dependencies {
                implementation(project(":core"))
                api(compose.runtime)
//                api(compose.foundation)
            }
        }
    }

}