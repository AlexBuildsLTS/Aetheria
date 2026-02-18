package com.aetheria.mmo.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.OrthographicCamera

/**
 * HUDRenderSystem - Renders 2D UI elements like health bars, minimap, etc.
 * This is a placeholder for future UI rendering.
 */
class HUDRenderSystem(private val batch: SpriteBatch) : EntitySystem() {
    private val camera: OrthographicCamera = OrthographicCamera()

    init {
        // Setup 2D camera for UI
        camera.setToOrtho(false)
    }

    override fun update(deltaTime: Float) {
        // TODO: Render HUD elements here
        // batch.begin()
        // ... render UI ...
        // batch.end()
    }
}
