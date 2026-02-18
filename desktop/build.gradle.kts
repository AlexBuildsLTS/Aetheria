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
    implementation(libs.ashley)
    implementation(libs.gdx.core)

    // Ktor Server Dependencies
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.call.id)
    implementation(libs.ktor.server.metrics)
    implementation(libs.ktor.server.partial.content)
    implementation(libs.ktor.server.forwarded.header)
    implementation(libs.ktor.server.conditional.headers)

    // Ktor Client Dependencies
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.encoding)

    // Database
    implementation(libs.postgresql)
    implementation(libs.h2.database)

    // Monitoring
    implementation(libs.cohort.ktor)

    // LibGDX Natives
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:${libs.versions.gdx.get()}:natives-desktop")
    runtimeOnly("com.badlogicgames.gdx:gdx-freetype-platform:${libs.versions.gdx.get()}:natives-desktop")
    runtimeOnly("com.badlogicgames.gdx:gdx-box2d-platform:${libs.versions.gdx.get()}:natives-desktop")
}

sourceSets {
    getByName("main") {
        resources.srcDirs("${rootProject.projectDir}/assets")
    }
}

tasks.register<JavaExec>("run") {
    group = "application"
    description = "Runs the desktop application"
    workingDir = rootProject.projectDir.resolve("assets")

    // Detect OS and set JVM args accordingly
    val osName = System.getProperty("os.name").lowercase()
    jvmArgs = if (osName.contains("mac")) {
        listOf(
            "--add-opens", "java.base/java.lang=ALL-UNNAMED",
            "--add-opens", "java.desktop/sun.awt=ALL-UNNAMED",
            "-XstartOnFirstThread"  // Required for macOS only
        )
    } else {
        listOf(
            "--add-opens", "java.base/java.lang=ALL-UNNAMED",
            "--add-opens", "java.desktop/sun.awt=ALL-UNNAMED"
        )
    }

    mainClass.set("com.aetheria.mmo.desktop.DesktopLauncherKt")
    classpath = sourceSets.main.get().runtimeClasspath
    standardInput = System.`in`
    // Use Java 21 toolchain
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(21))
    })
}

tasks.withType<Jar> {
    archiveFileName.set("${rootProject.name}-desktop.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "com.aetheria.mmo.desktop.DesktopLauncherKt"
    }
    val runtimeClasspath = configurations.runtimeClasspath.get()
    from({
        runtimeClasspath.filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    from("${rootProject.projectDir}/assets")
}

