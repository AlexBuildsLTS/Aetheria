package com.aetheria.mmo.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.aetheria.mmo.AetheriaGame

fun main() {
    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("Aetheria: Void Horizon")
    config.setWindowedMode(1280, 720) // Better default resolution
    config.setForegroundFPS(60)
    config.useVsync(true)
    config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4) // Enable MSAA (4 samples)
    Lwjgl3Application(AetheriaGame(), config)
}
