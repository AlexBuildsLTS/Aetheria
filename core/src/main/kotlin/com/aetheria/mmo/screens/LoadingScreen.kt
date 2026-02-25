package com.aetheria.mmo.screens

import com.aetheria.mmo.AetheriaGame
import com.aetheria.mmo.managers.ResourceManager
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import kotlin.math.sin

class LoadingScreen(private val game: AetheriaGame) : ScreenAdapter() {
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private var progress = 0f
    private var time = 0f

    init {
        ResourceManager.loadAll()
        font.data.setScale(1.5f)
    }

    override fun render(delta: Float) {
        time += delta
        Gdx.gl.glClearColor(0.02f, 0.02f, 0.05f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (ResourceManager.update()) {
            game.screen = CharacterSelectScreen(game)
            return
        }

        progress = MathUtils.lerp(progress, ResourceManager.getProgress(), 0.1f)
        val barWidth = Gdx.graphics.width - 200f
        val barHeight = 30f
        val barX = 100f
        val barY = Gdx.graphics.height / 2f - barHeight / 2f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.DARK_GRAY
        shapeRenderer.rect(barX, barY, barWidth, barHeight)
        shapeRenderer.color = Color.CYAN
        shapeRenderer.rect(barX, barY, barWidth * progress, barHeight)
        shapeRenderer.end()

        batch.begin()
        font.draw(batch, "LOADING AETHERIA: ${(progress * 100).toInt()}%", barX, barY + 80f)
        batch.end()
    }

    override fun dispose() {
        shapeRenderer.dispose()
        batch.dispose()
        font.dispose()
    }
}
