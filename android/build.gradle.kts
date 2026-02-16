plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.aetheria.mmo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.aetheria.mmo"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
            assets.srcDirs("../assets")
        }
    }

    // FIX: Use modern compiler options for Kotlin 2.0
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
        }
    }
}

dependencies {
    implementation(project(":core"))
    // This now works because we added it back to TOML
    implementation(libs.gdx.backend.android)

    // FIX: Manually define natives for Android to avoid TOML errors
    implementation("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-x86")
    implementation("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-x86_64")

    implementation("com.badlogicgames.gdx:gdx-freetype-platform:1.13.1:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:1.13.1:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:1.13.1:natives-x86")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:1.13.1:natives-x86_64")
}