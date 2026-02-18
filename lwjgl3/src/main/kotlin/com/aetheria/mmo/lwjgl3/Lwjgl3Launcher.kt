package com.aetheria.mmo.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.aetheria.mmo.AetheriaGame

fun main() {
    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("Aetheria")
    config.setWindowedMode(800, 600)
    config.setForegroundFPS(60)
    config.useVsync(true)
    Lwjgl3Application(AetheriaGame(), config)
}
