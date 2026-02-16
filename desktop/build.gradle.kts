plugins {
    alias(libs.plugins.kotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    // FIX: Force Kotlin 1.8 here too
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.gdx.backend.lwjgl3)
    // Add natives for desktop
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-desktop")
    runtimeOnly("com.badlogicgames.gdx:gdx-freetype-platform:1.13.1:natives-desktop")


}

tasks.register<JavaExec>("run") {
    group = "application"
    description = "Runs the desktop application"
    mainClass.set("com.aetheria.mmo.desktop.DesktopLauncherKt")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`
    workingDir = File(project.rootDir, "assets") // This fixes the asset loading
}