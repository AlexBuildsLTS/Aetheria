package com.aetheria.mmo.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import java.util.Locale

/**
 * Skill/Ability Button Widget
 * Displays ability icon, cooldown, and keybinding
 */
class SkillButton(
    private val size: Float = 60f
) {
    private val shapeRenderer = ShapeRenderer()
    private val font = BitmapFont()
    private val largeFont = BitmapFont().apply {
        data.setScale(1.5f)
    }

    private val readyColor = Color(0.2f, 0.8f, 0.3f, 1f)
    private val cooldownColor = Color(0.3f, 0.3f, 0.3f, 0.8f)
    private val borderColor = Color(0.6f, 0.6f, 0.6f, 1f)
    private val overlayColor = Color(0f, 0f, 0f, 0.6f)

    /**
     * Render skill button
     * @param key Keybinding (e.g., "Q", "E", "R", "F")
     * @param name Ability name
     * @param currentCooldown Current cooldown time (0 = ready)
     * @param maxCooldown Maximum cooldown time
     * @param x X position (bottom-left corner)
     * @param y Y position (bottom-left corner)
     */
    fun render(
        batch: SpriteBatch,
        key: String,
        name: String,
        currentCooldown: Float,
        maxCooldown: Float,
        x: Float,
        y: Float
    ) {
        val isReady = currentCooldown <= 0f

        // End batch to render shapes
        batch.end()

        shapeRenderer.projectionMatrix = batch.projectionMatrix
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Background
        shapeRenderer.color = if (isReady) readyColor else cooldownColor
        shapeRenderer.rect(x, y, size, size)

        // Cooldown overlay (fills from bottom to top)
        if (!isReady) {
            val cooldownPercent = currentCooldown / maxCooldown
            shapeRenderer.color = overlayColor
            shapeRenderer.rect(x, y, size, size * cooldownPercent)
        }

        shapeRenderer.end()

        // Border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = borderColor
        shapeRenderer.rect(x, y, size, size)
        shapeRenderer.end()

        // Resume batch for text
        batch.begin()

        // Key binding (large, centered)
        largeFont.color = Color.WHITE
        largeFont.draw(batch, key, x + size / 2 - 10f, y + size - 10f)

        // Ability name (small, below button)
        font.color = Color.LIGHT_GRAY
        font.data.setScale(0.7f)
        font.draw(batch, name, x + 5f, y - 5f)
        font.data.setScale(1f)

        // Cooldown timer (if on cooldown)
        if (!isReady) {
            font.color = Color.WHITE
            font.data.setScale(1.2f)
            val cooldownText = String.format(Locale.US, "%.1f", currentCooldown)
            font.draw(batch, cooldownText, x + size / 2 - 15f, y + size / 2 + 5f)
            font.data.setScale(1f)
        }
    }

    fun dispose() {
        shapeRenderer.dispose()
        font.dispose()
        largeFont.dispose()
    }
}
