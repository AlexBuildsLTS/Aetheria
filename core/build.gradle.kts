plugins {
    alias(libs.plugins.kotlinJvm)
}

java {
    // This sets Java to 1.8
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    // FIX: This forces Kotlin to match Java 1.8
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }
}

dependencies {
    // Core Game Engine
    api(libs.gdx.core)
    api(libs.ashley)
    api(libs.gdx.freetype)

    // Kotlin & Coroutines
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines)

    // Networking
    implementation(libs.ktor.client.core)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)
}