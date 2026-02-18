package com.aetheria.mmo

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider
import com.aetheria.mmo.managers.ResourceManager
import com.aetheria.mmo.screens.LoadingScreen

class AetheriaGame : com.badlogic.gdx.Game() {
    lateinit var batch: SpriteBatch
    lateinit var modelBatch: ModelBatch

    override fun create() {
        batch = SpriteBatch()

        // Configure shader to support up to 64 bones (your models have 24)
        val config = DefaultShader.Config()
        config.numBones = 64  // Increase from default 12 to 64
        val shaderProvider = DefaultShaderProvider(config)
        modelBatch = ModelBatch(shaderProvider)

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