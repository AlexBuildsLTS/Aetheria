package com.aetheria.mmo

import com.aetheria.mmo.managers.ResourceManager
import com.aetheria.mmo.screens.LoadingScreen
import com.badlogic.gdx.Game

class AetheriaGame : Game() {

    override fun create() {
        // Start with the Loading Screen
        setScreen(LoadingScreen(this))
    }

    override fun dispose() {
        super.dispose()
        ResourceManager.dispose() // Clean up global assets
    }
}