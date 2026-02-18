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
    // Core Engine
    api(libs.gdx.core)
    api(libs.gdx.box2d)
    api(libs.ashley)
    api(libs.gdx.freetype)
    api(libs.gdx.gltf) // 3D model loading (GLTF/GLB)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines)

    // Networking (Supabase & Ktor)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.supabase.gotrue)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.realtime)
}