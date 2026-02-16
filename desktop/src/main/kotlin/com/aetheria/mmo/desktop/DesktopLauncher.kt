package com.aetheria.mmo.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.aetheria.mmo.AetheriaGame

fun main() {
    val config = Lwjgl3ApplicationConfiguration().apply {
        setTitle("Aetheria: Void Horizon")
        setWindowedMode(1280, 720)
        setForegroundFPS(60)
        useVsync(true)
    }
    Lwjgl3Application(AetheriaGame(), config)
}