package com.aetheria.mmo.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

/**
 * Crosshair Widget
 * Center screen targeting reticle
 */
class Crosshair {
    private val shapeRenderer = ShapeRenderer()
    private val color = Color(1f, 1f, 1f, 0.8f)
    private val size = 10f
    private val thickness = 2f
    private val gap = 5f

    fun render() {
        val centerX = Gdx.graphics.width / 2f
        val centerY = Gdx.graphics.height / 2f

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = color

        // Top line
        shapeRenderer.rect(centerX - thickness / 2, centerY + gap, thickness, size)

        // Bottom line
        shapeRenderer.rect(centerX - thickness / 2, centerY - gap - size, thickness, size)

        // Left line
        shapeRenderer.rect(centerX - gap - size, centerY - thickness / 2, size, thickness)

        // Right line
        shapeRenderer.rect(centerX + gap, centerY - thickness / 2, size, thickness)

        // Center dot
        shapeRenderer.circle(centerX, centerY, 2f)

        shapeRenderer.end()
    }

    fun dispose() {
        shapeRenderer.dispose()
    }
}
