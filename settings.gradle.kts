// settings.gradle.kts
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        maven {
            url = uri("https://jitpack.io")
        }
    }


}

rootProject.name = "Aetheria"

include(":core")
include(":android")
include(":desktop")
include(":lwjgl3")
