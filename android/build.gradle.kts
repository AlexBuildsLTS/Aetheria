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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/**",
                "**/*.dylib",
                "**/*.so",
                "**/*.dll"
            )
        }
    }
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(project(":core")) {
        // Exclude gdx-gltf from Android to prevent duplicate class conflicts
        exclude(group = "com.github.mgsx-dev", module = "gdx-gltf")
        // Exclude LWJGL (desktop-only) from Android
        exclude(group = "org.lwjgl")
        exclude(group = "com.badlogicgames.gdx", module = "gdx-backend-lwjgl3")
    }
    implementation(libs.gdx.backend.android)
    implementation(libs.androidx.core.splashscreen)
    implementation("com.google.android.material:material:1.11.0")

    implementation(libs.gdx.core)
    implementation(libs.gdx.box2d)
    implementation(libs.gdx.freetype)
    implementation(libs.ashley)






    // FIX: Manually define natives for Android to avoid TOML errors
    implementation("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-x86")
    implementation("com.badlogicgames.gdx:gdx-platform:1.13.1:natives-x86_64")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:1.13.1:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:1.13.1:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:1.13.1:natives-x86")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:1.13.1:natives-x86_64")


    implementation("com.badlogicgames.gdx:gdx-freetype-platform:1.13.1:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:1.13.1:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:1.13.1:natives-x86")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:1.13.1:natives-x86_64")


}