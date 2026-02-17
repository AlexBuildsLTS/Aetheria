package com.aetheria.mmo


import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.aetheria.mmo.managers.ResourceManager






class AetheriaGame : Game() { // <--- MUST EXTEND GAME
    lateinit var batch: SpriteBatch
    lateinit var modelBatch: ModelBatch

    override fun create() {
        batch = SpriteBatch()
        modelBatch = ModelBatch()

        // Load Assets
        ResourceManager.loadAll()


    }


    override fun dispose() {
        super.dispose() // <--- Disposes the active screen


        // Check initialization before disposing to prevent crashes
        if (::batch.isInitialized) batch.dispose()
        if (::modelBatch.isInitialized) modelBatch.dispose()

        ResourceManager.dispose()
    }
}
