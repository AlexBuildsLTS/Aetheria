package com.aetheria.mmo.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.aetheria.mmo.AetheriaGame

fun main(args: Array<String>) {
    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("Aetheria: Void Horizon")
    config.setWindowedMode(1280, 720)
    config.setForegroundFPS(60)
    config.useVsync(true)
    // config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4) // MSAA
    Lwjgl3Application(AetheriaGame(), config)
}
