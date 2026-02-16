plugins {
    alias(libs.plugins.kotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.gdx.backend.lwjgl3)

    // Explicitly add the natives with the classifier
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-desktop")
    runtimeOnly("com.badlogicgames.gdx:gdx-freetype-platform:1.13.1:natives-desktop")
}

// Configures the run task
tasks.register<JavaExec>("run") {
    group = "application"
    description = "Runs the desktop application"
    mainClass.set("com.aetheria.mmo.lwjgl3.Lwjgl3LauncherKt")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`
    // Points to the assets folder in the root directory
    workingDir = File(project.rootDir, "assets")
}