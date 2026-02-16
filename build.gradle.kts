// Root build.gradle.kts

allprojects {
    version = "1.0.0"

    // ERROR FIX: We removed the 'repositories { ... }' block from here.
    // They are already handled in settings.gradle.kts.
}

// Clean task for housekeeping
tasks.register<Delete>("clean") {
    // FIX: Updated to use the new Gradle API (fixes the 'buildDir' deprecation warning too)
    delete(rootProject.layout.buildDirectory)
}