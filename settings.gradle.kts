pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = java.net.URI("https://jitpack.io") }
        maven { url = java.net.URI("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = java.net.URI("https://s01.oss.sonatype.org/content/repositories/releases/") }
    }
}

rootProject.name = "Aetheria"

include(":core")
include(":android")
include(":desktop")