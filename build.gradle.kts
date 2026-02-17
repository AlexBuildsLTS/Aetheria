plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinJvm) apply false
}

allprojects {
    version = "1.0.0"
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}