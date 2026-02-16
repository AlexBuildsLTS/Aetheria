package com.aetheria.mmo.screens

import com.aetheria.mmo.AetheriaGame
import com.aetheria.mmo.managers.ResourceManager
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils

class LoadingScreen(private val game: AetheriaGame) : ScreenAdapter() {

    private val shapeRenderer = ShapeRenderer()
    private var progress = 0f

    init {
        // Start loading assets immediately
        ResourceManager.loadAll()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // update() returns true when loading is finished
        if (ResourceManager.assets.update()) {
            // Loading Complete -> Switch to Main Menu
            game.screen = GameWorldScreen()
        } else {
            // Still loading -> Update progress bar
            progress = MathUtils.lerp(progress, ResourceManager.assets.progress, 0.1f)
        }

        // Draw the Progress Bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Background bar (Dark Grey)
        shapeRenderer.color = Color.DARK_GRAY
        shapeRenderer.rect(100f, Gdx.graphics.height / 2f - 10, Gdx.graphics.width - 200f, 20f)

        // Filled bar (Neon Cyan)
        shapeRenderer.color = Color.CYAN
        shapeRenderer.rect(100f, Gdx.graphics.height / 2f - 10, (Gdx.graphics.width - 200f) * progress, 20f)

        shapeRenderer.end()
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }
}