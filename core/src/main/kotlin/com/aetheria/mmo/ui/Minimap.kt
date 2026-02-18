package com.aetheria.mmo.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

/**
 * Minimap Widget
 * Top-right corner radar showing player position and nearby entities
 */
class Minimap(
    private val size: Float = 150f
) {
    private val shapeRenderer = ShapeRenderer()
    private val bgColor = Color(0.1f, 0.1f, 0.15f, 0.8f)
    private val borderColor = Color(0.5f, 0.5f, 0.6f, 1f)
    private val playerColor = Color(0.2f, 0.8f, 1f, 1f)
    private val enemyColor = Color(1f, 0.2f, 0.2f, 1f)
    private val allyColor = Color(0.2f, 1f, 0.2f, 1f)

    private var x = 0f
    private var y = 0f

    fun render(playerPos: Vector3, enemies: List<Vector3> = emptyList(), allies: List<Vector3> = emptyList()) {
        // Position in top-right corner
        x = Gdx.graphics.width - size - 20f
        y = Gdx.graphics.height - size - 20f

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Background
        shapeRenderer.color = bgColor
        shapeRenderer.rect(x, y, size, size)

        // Player (center)
        shapeRenderer.color = playerColor
        shapeRenderer.circle(x + size / 2, y + size / 2, 5f)

        // Enemies (relative to player)
        shapeRenderer.color = enemyColor
        for (enemy in enemies) {
            val relX = (enemy.x - playerPos.x) * 2f
            val relZ = (enemy.z - playerPos.z) * 2f

            // Only show if within range
            if (Math.abs(relX) < size / 2 && Math.abs(relZ) < size / 2) {
                shapeRenderer.circle(
                    x + size / 2 + relX,
                    y + size / 2 + relZ,
                    3f
                )
            }
        }

        // Allies (relative to player)
        shapeRenderer.color = allyColor
        for (ally in allies) {
            val relX = (ally.x - playerPos.x) * 2f
            val relZ = (ally.z - playerPos.z) * 2f

            if (Math.abs(relX) < size / 2 && Math.abs(relZ) < size / 2) {
                shapeRenderer.circle(
                    x + size / 2 + relX,
                    y + size / 2 + relZ,
                    3f
                )
            }
        }

        shapeRenderer.end()

        // Border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = borderColor
        shapeRenderer.rect(x, y, size, size)
        shapeRenderer.end()
    }

    fun dispose() {
        shapeRenderer.dispose()
    }
}
