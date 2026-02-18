package com.aetheria.mmo

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.aetheria.mmo.managers.ResourceManager
import com.aetheria.mmo.screens.LoadingScreen

class AetheriaGame : com.badlogic.gdx.Game() {
    lateinit var batch: SpriteBatch
    lateinit var modelBatch: ModelBatch

    override fun create() {
        batch = SpriteBatch()
        modelBatch = ModelBatch()

        // 1. Load Assets
        ResourceManager.loadAll()

        // 2. Switch to the Loading Screen to show progress
        setScreen(LoadingScreen(this))
    }

    override fun dispose() {

        super.dispose()
        batch.dispose()
        modelBatch.dispose()
        ResourceManager.dispose()
    }
}