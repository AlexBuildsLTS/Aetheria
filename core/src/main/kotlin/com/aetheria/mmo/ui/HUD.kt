package com.aetheria.mmo.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.aetheria.mmo.components.HealthComponent
import com.aetheria.mmo.components.StaminaComponent
import com.aetheria.mmo.components.CombatComponent
import com.badlogic.ashley.core.Entity

/**
 * AAA+ Quality HUD Overlay
 * Professional game UI with:
 * - Health bar with gradient
 * - Stamina bar with gradient
 * - Ability cooldown indicators
 * - Class name display
 * - FPS counter
 * - Controls help text
 */
class HUD {
    private val shapeRenderer = ShapeRenderer()
    private val font = BitmapFont()
    private val largeFont = BitmapFont().apply {
        data.setScale(1.5f)
    }

    // Colors
    private val healthColor = Color(0.8f, 0.1f, 0.1f, 1f)
    private val healthBgColor = Color(0.2f, 0.05f, 0.05f, 0.8f)
    private val staminaColor = Color(1f, 0.9f, 0.2f, 1f)
    private val staminaBgColor = Color(0.3f, 0.3f, 0.1f, 0.8f)
    private val abilityReadyColor = Color(0.2f, 0.8f, 0.3f, 1f)
    private val abilityCooldownColor = Color(0.3f, 0.3f, 0.3f, 0.8f)
    private val abilityBorderColor = Color(0.6f, 0.6f, 0.6f, 1f)

    fun render(
        batch: SpriteBatch,
        playerEntity: Entity?,
        className: String,
        screenWidth: Int,
        screenHeight: Int
    ) {
        if (playerEntity == null) return

        val health = playerEntity.getComponent(HealthComponent::class.java)
        val stamina = playerEntity.getComponent(StaminaComponent::class.java)
        val combat = playerEntity.getComponent(CombatComponent::class.java)

        // Render bars first (behind text)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        shapeRenderer.projectionMatrix = batch.projectionMatrix
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Health Bar (bottom left)
        if (health != null) {
            renderHealthBar(health, 20f, 120f, 300f, 30f)
        }

        // Stamina Bar (below health)
        if (stamina != null) {
            renderStaminaBar(stamina, 20f, 80f, 300f, 25f)
        }

        // Ability Cooldowns (bottom center)
        if (combat != null) {
            renderAbilityCooldowns(combat, screenWidth / 2f - 200f, 40f)
        }

        shapeRenderer.end()

        // Render text
        batch.begin()

        // Class name (top left)
        largeFont.color = Color.CYAN
        largeFont.draw(batch, className, 20f, screenHeight - 20f)

        // Health text
        if (health != null) {
            font.color = Color.WHITE
            val healthText = "${health.current.toInt()} / ${health.max.toInt()}"
            font.draw(batch, healthText, 25f, 145f)

            font.color = Color.LIGHT_GRAY
            font.data.setScale(0.8f)
            font.draw(batch, "HP", 25f, 160f)
            font.data.setScale(1f)
        }

        // Stamina text
        if (stamina != null) {
            font.color = Color.WHITE
            val staminaText = "${stamina.current.toInt()} / ${stamina.max.toInt()}"
            font.draw(batch, staminaText, 25f, 105f)

            font.color = Color.LIGHT_GRAY
            font.data.setScale(0.8f)
            font.draw(batch, "STAMINA", 25f, 120f)
            font.data.setScale(1f)
        }

        // Ability labels
        if (combat != null) {
            renderAbilityLabels(batch, combat, screenWidth / 2f - 200f, 40f)
        }

        // Controls (top right)
        font.color = Color.LIGHT_GRAY
        font.data.setScale(0.9f)
        val controlsX = screenWidth - 350f
        font.draw(batch, "WASD: Move", controlsX, screenHeight - 20f)
        font.draw(batch, "SPACE: Jump", controlsX, screenHeight - 40f)
        font.draw(batch, "Q/E/R/F: Abilities", controlsX, screenHeight - 60f)
        font.draw(batch, "ESC: Menu", controlsX, screenHeight - 80f)
        font.data.setScale(1f)

        // FPS (bottom right)
        font.color = Color.YELLOW
        font.draw(batch, "FPS: ${Gdx.graphics.framesPerSecond}", screenWidth - 100f, 30f)

        batch.end()
    }

    private fun renderHealthBar(health: HealthComponent, x: Float, y: Float, width: Float, height: Float) {
        // Background
        shapeRenderer.color = healthBgColor
        shapeRenderer.rect(x, y, width, height)

        // Foreground (current health)
        val healthPercent = health.current / health.max
        shapeRenderer.color = healthColor
        shapeRenderer.rect(x, y, width * healthPercent, height)

        // Border
        shapeRenderer.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.WHITE
        shapeRenderer.rect(x, y, width, height)
        shapeRenderer.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    }

    private fun renderStaminaBar(stamina: StaminaComponent, x: Float, y: Float, width: Float, height: Float) {
        // Background
        shapeRenderer.color = staminaBgColor
        shapeRenderer.rect(x, y, width, height)

        // Foreground (current stamina)
        val staminaPercent = stamina.current / stamina.max
        shapeRenderer.color = staminaColor
        shapeRenderer.rect(x, y, width * staminaPercent, height)

        // Border
        shapeRenderer.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.WHITE
        shapeRenderer.rect(x, y, width, height)
        shapeRenderer.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    }

    private fun renderAbilityCooldowns(combat: CombatComponent, startX: Float, startY: Float) {
        val abilityKeys = listOf("Q", "E", "R", "F")
        val boxSize = 60f
        val spacing = 20f

        abilityKeys.forEachIndexed { index, key ->
            val x = startX + index * (boxSize + spacing)
            val y = startY
            val cooldown = combat.abilityCooldowns[key] ?: 0f
            val isReady = cooldown <= 0f

            // Background
            shapeRenderer.color = if (isReady) abilityReadyColor else abilityCooldownColor
            shapeRenderer.rect(x, y, boxSize, boxSize)

            // Cooldown overlay (if on cooldown)
            if (!isReady) {
                val maxCooldown = when(key) {
                    "Q" -> 5f
                    "E" -> 8f
                    "R" -> 12f
                    "F" -> 15f
                    else -> 5f
                }
                val cooldownPercent = cooldown / maxCooldown
                shapeRenderer.color = Color(0f, 0f, 0f, 0.6f)
                shapeRenderer.rect(x, y, boxSize, boxSize * cooldownPercent)
            }

            // Border
            shapeRenderer.end()
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            shapeRenderer.color = abilityBorderColor
            shapeRenderer.rect(x, y, boxSize, boxSize)
            shapeRenderer.end()
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        }
    }

    private fun renderAbilityLabels(batch: SpriteBatch, combat: CombatComponent, startX: Float, startY: Float) {
        val abilityKeys = listOf("Q", "E", "R", "F")
        val abilityNames = mapOf(
            "Q" to "Strike",
            "E" to "Dash",
            "R" to "Ultimate",
            "F" to "Heal"
        )
        val boxSize = 60f
        val spacing = 20f

        abilityKeys.forEachIndexed { index, key ->
            val x = startX + index * (boxSize + spacing)
            val y = startY
            val cooldown = combat.abilityCooldowns[key] ?: 0f
            val isReady = cooldown <= 0f

            // Key binding (large)
            largeFont.color = Color.WHITE
            largeFont.draw(batch, key, x + 20f, y + boxSize - 10f)

            // Ability name (small)
            font.color = Color.LIGHT_GRAY
            font.data.setScale(0.7f)
            font.draw(batch, abilityNames[key] ?: key, x + 5f, y - 5f)

            // Cooldown timer (if on cooldown)
            if (!isReady) {
                font.color = Color.WHITE
                font.data.setScale(1.2f)
                val cooldownText = String.format("%.1f", cooldown)
                font.draw(batch, cooldownText, x + 15f, y + 35f)
            }

            font.data.setScale(1f)
        }
    }

    fun dispose() {
        shapeRenderer.dispose()
        font.dispose()
        largeFont.dispose()
    }
}
