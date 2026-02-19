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

    // Force LWJGL 3.3.5 for compatibility with LibGDX 1.13.1
    implementation(platform("org.lwjgl:lwjgl-bom:3.3.5"))
    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")

    // LibGDX Natives
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:${libs.versions.gdx.get()}:natives-desktop")
    runtimeOnly("com.badlogicgames.gdx:gdx-freetype-platform:${libs.versions.gdx.get()}:natives-desktop")
    runtimeOnly("com.badlogicgames.gdx:gdx-box2d-platform:${libs.versions.gdx.get()}:natives-desktop")
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
    
    // Add JVM arguments for better debugging if needed
    jvmArgs("-Xmx1G", "-noverify")
}
