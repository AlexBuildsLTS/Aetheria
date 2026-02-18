package com.aetheria.mmo.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.utils.Array

/**
 * Floating Damage Numbers System
 * Shows damage/healing numbers that float up and fade out
 */
class DamageNumbers {
    private val font = BitmapFont().apply {
        data.setScale(1.5f)
    }

    private val activeNumbers = Array<DamageNumber>()

    /**
     * Spawn a damage number at world position
     */
    fun spawn(damage: Float, worldX: Float, worldY: Float, worldZ: Float, isCritical: Boolean = false, isHealing: Boolean = false) {
        activeNumbers.add(DamageNumber(damage, worldX, worldY, worldZ, isCritical, isHealing))
    }

    /**
     * Update and render all active damage numbers
     */
    fun render(batch: SpriteBatch, camera: PerspectiveCamera, deltaTime: Float) {
        batch.begin()

        val iterator = activeNumbers.iterator()
        while (iterator.hasNext()) {
            val number = iterator.next()
            number.update(deltaTime)

            if (number.isExpired()) {
                iterator.remove()
            } else {
                number.render(batch, camera, font)
            }
        }

        batch.end()
    }

    fun dispose() {
        font.dispose()
    }

    private class DamageNumber(
        val damage: Float,
        var worldX: Float,
        var worldY: Float,
        var worldZ: Float,
        val isCritical: Boolean,
        val isHealing: Boolean
    ) {
        private var lifetime = 0f
        private val maxLifetime = 1.5f
        private var velocityY = 2f

        fun update(deltaTime: Float) {
            lifetime += deltaTime
            worldY += velocityY * deltaTime
            velocityY -= 3f * deltaTime // Gravity
        }

        fun isExpired(): Boolean = lifetime >= maxLifetime

        fun render(batch: SpriteBatch, camera: PerspectiveCamera, font: BitmapFont) {
            // Project world position to screen
            val screenPos = Vector3(worldX, worldY, worldZ)
            camera.project(screenPos)

            // Calculate alpha (fade out)
            val alpha = 1f - (lifetime / maxLifetime)

            // Set color based on type
            font.color = when {
                isHealing -> Color(0.2f, 1f, 0.2f, alpha)
                isCritical -> Color(1f, 0.8f, 0f, alpha)
                else -> Color(1f, 0.2f, 0.2f, alpha)
            }

            // Scale for critical hits
            if (isCritical) {
                font.data.setScale(2f)
            }

            val text = if (isHealing) "+${damage.toInt()}" else "${damage.toInt()}"
            font.draw(batch, text, screenPos.x, screenPos.y)

            // Reset scale
            if (isCritical) {
                font.data.setScale(1.5f)
            }
        }
    }
}
