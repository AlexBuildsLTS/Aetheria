plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.gdx.backend.lwjgl3)
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:${libs.versions.gdx.get()}:natives-desktop")
    runtimeOnly("com.badlogicgames.gdx:gdx-box2d-platform:${libs.versions.gdx.get()}:natives-desktop")
    runtimeOnly("com.badlogicgames.gdx:gdx-freetype-platform:${libs.versions.gdx.get()}:natives-desktop")
}

sourceSets {
    getByName("main") {
        resources.srcDirs("${rootProject.projectDir}/assets")
    }
}

tasks.register<JavaExec>("run") {
    // This tells the game to look for models in the 'assets' folder
    workingDir = File("${rootProject.projectDir}/assets")

    // Standard JVM args for LibGDX on Mac/Linux/Windows
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
    jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
    mainClass.set("com.aetheria.mmo.desktop.DesktopLauncherKt")
    classpath = sourceSets.main.get().runtimeClasspath
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "com.aetheria.mmo.desktop.DesktopLauncherKt"
    }
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    from("${rootProject.projectDir}/assets")
}