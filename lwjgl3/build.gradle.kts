plugins {
    alias(libs.plugins.kotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.gdx.backend.lwjgl3)

    // Explicitly add the natives with the classifier
    runtimeOnly(libs.gdx.platform)
    runtimeOnly(libs.gdx.freetype.platform.v1140)
    runtimeOnly(libs.gdx.box2d.platform.v3110)
    runtimeOnly(libs.gdx.backend.lwjgl3.v1140)
    runtimeOnly(libs.gdx.platform.v1140)
    runtimeOnly(libs.gdx.freetype.platform)
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