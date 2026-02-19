package com.aetheria.mmo

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.aetheria.mmo.managers.ResourceManager
import com.aetheria.mmo.screens.LoadingScreen

class AetheriaGame : com.badlogic.gdx.Game() {
    var batch: SpriteBatch? = null

    override fun create() {
        batch = SpriteBatch()
        setScreen(LoadingScreen(this))
    }

    override fun dispose() {
        super.dispose()
        batch?.dispose()
        ResourceManager.dispose()
    }
}
