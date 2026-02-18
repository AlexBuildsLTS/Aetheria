package com.aetheria.mmo.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

/**
 * Standalone Health Bar Widget
 * Can be used for player, enemies, NPCs, etc.
 */
class HealthBar(
    private val width: Float = 200f,
    private val height: Float = 20f
) {
    private val shapeRenderer = ShapeRenderer()
    private val font = BitmapFont()

    private val healthColor = Color(0.8f, 0.1f, 0.1f, 1f)
    private val healthBgColor = Color(0.2f, 0.05f, 0.05f, 0.8f)
    private val borderColor = Color.WHITE

    /**
     * Render health bar at specified position
     * @param current Current health value
     * @param max Maximum health value
     * @param x X position (bottom-left corner)
     * @param y Y position (bottom-left corner)
     * @param showText Whether to display numeric health text
     */
    fun render(
        batch: SpriteBatch,
        current: Float,
        max: Float,
        x: Float,
        y: Float,
        showText: Boolean = true
    ) {
        // End batch to render shapes
        batch.end()

        shapeRenderer.projectionMatrix = batch.projectionMatrix
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Background
        shapeRenderer.color = healthBgColor
        shapeRenderer.rect(x, y, width, height)

        // Foreground (current health)
        val healthPercent = (current / max).coerceIn(0f, 1f)
        shapeRenderer.color = healthColor
        shapeRenderer.rect(x, y, width * healthPercent, height)

        shapeRenderer.end()

        // Border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = borderColor
        shapeRenderer.rect(x, y, width, height)
        shapeRenderer.end()

        // Resume batch for text
        batch.begin()

        if (showText) {
            font.color = Color.WHITE
            val healthText = "${current.toInt()} / ${max.toInt()}"
            val textWidth = font.data.getGlyph('0').width * healthText.length
            font.draw(batch, healthText, x + (width - textWidth) / 2, y + height / 2 + 5f)
        }
    }

    fun dispose() {
        shapeRenderer.dispose()
        font.dispose()
    }
}
