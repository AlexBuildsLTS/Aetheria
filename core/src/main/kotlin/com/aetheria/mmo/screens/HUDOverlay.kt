package com.aetheria.mmo.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.aetheria.mmo.utils.ColorUtils
import com.aetheria.mmo.utils.StringHelpers

/**
 * HUD Overlay
 * Displays in-game HUD elements (health, stamina, abilities, minimap, etc.)
 * Rendered on top of the game world
 */
class HUDOverlay : Disposable {

    private val stage: Stage = Stage(ScreenViewport())
    private val skin: Skin = Skin(Gdx.files.internal("ui/skin/metalui.json"))
    private val shapeRenderer = ShapeRenderer()

    // HUD Elements
    private val healthLabel: Label
    private val shieldLabel: Label
    private val staminaLabel: Label
    private val fpsLabel: Label
    private val pingLabel: Label

    // Player stats (mock data)
    private var health = 100f
    private var maxHealth = 100f
    private var shield = 50f
    private var maxShield = 50f
    private var stamina = 100f
    private var maxStamina = 100f
    private var ping = 45

    init {
        // Create HUD table
        val hudTable = Table()
        hudTable.setFillParent(true)
        hudTable.top().left()
        hudTable.pad(20f)

        // Health label
        healthLabel = Label("HP: 100/100", skin).apply {
            color = ColorUtils.HEALTH_COLOR
        }

        // Shield label
        shieldLabel = Label("Shield: 50/50", skin).apply {
            color = ColorUtils.SHIELD_COLOR
        }

        // Stamina label
        staminaLabel = Label("Stamina: 100/100", skin).apply {
            color = ColorUtils.STAMINA_COLOR
        }

        // FPS label
        fpsLabel = Label("FPS: 60", skin).apply {
            color = Color.WHITE
            setFontScale(0.8f)
        }

        // Ping label
        pingLabel = Label("Ping: 45ms", skin).apply {
            color = Color.GREEN
            setFontScale(0.8f)
        }

        // Layout
        hudTable.add(healthLabel).left().row()
        hudTable.add(shieldLabel).left().row()
        hudTable.add(staminaLabel).left().row()
        hudTable.add(fpsLabel).left().padTop(20f).row()
        hudTable.add(pingLabel).left().row()

        stage.addActor(hudTable)

        // Create ability bar at bottom
        createAbilityBar()
    }

    private fun createAbilityBar() {
        val abilityTable = Table()
        abilityTable.setFillParent(true)
        abilityTable.bottom()
        abilityTable.pad(20f)

        // Ability slots (Q, E, R, F)
        val abilities = listOf("Q", "E", "R", "F")
        abilities.forEach { key ->
            val abilityLabel = Label(key, skin).apply {
                color = ColorUtils.CYBER_BLUE
                setFontScale(1.2f)
            }
            abilityTable.add(abilityLabel).size(60f).pad(5f)
        }

        stage.addActor(abilityTable)
    }

    /**
     * Update HUD
     */
    fun update(deltaTime: Float) {
        // Update labels
        healthLabel.setText("HP: ${health.toInt()}/${maxHealth.toInt()}")
        shieldLabel.setText("Shield: ${shield.toInt()}/${maxShield.toInt()}")
        staminaLabel.setText("Stamina: ${stamina.toInt()}/${maxStamina.toInt()}")
        fpsLabel.setText("FPS: ${Gdx.graphics.framesPerSecond}")
        pingLabel.setText("Ping: ${ping}ms")

        // Update ping color based on value
        pingLabel.color = when {
            ping < 50 -> Color.GREEN
            ping < 100 -> Color.YELLOW
            else -> Color.RED
        }

        stage.act(deltaTime)
    }

    /**
     * Render HUD
     */
    fun render() {
        // Render health/shield/stamina bars
        renderBars()

        // Render UI
        stage.draw()
    }

    /**
     * Render status bars
     */
    private fun renderBars() {
        shapeRenderer.projectionMatrix = stage.viewport.camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        val barWidth = 200f
        val barHeight = 20f
        val x = 20f
        var y = stage.viewport.worldHeight - 40f

        // Health bar
        renderBar(x, y, barWidth, barHeight, health, maxHealth, ColorUtils.HEALTH_COLOR)
        y -= barHeight + 5f

        // Shield bar
        renderBar(x, y, barWidth, barHeight, shield, maxShield, ColorUtils.SHIELD_COLOR)
        y -= barHeight + 5f

        // Stamina bar
        renderBar(x, y, barWidth, barHeight, stamina, maxStamina, ColorUtils.STAMINA_COLOR)

        shapeRenderer.end()
    }

    /**
     * Render individual bar
     */
    private fun renderBar(x: Float, y: Float, width: Float, height: Float, current: Float, max: Float, color: Color) {
        // Background
        shapeRenderer.color = Color(0.2f, 0.2f, 0.2f, 0.8f)
        shapeRenderer.rect(x, y, width, height)

        // Foreground
        val fillWidth = (current / max) * width
        shapeRenderer.color = color
        shapeRenderer.rect(x, y, fillWidth, height)

        // Border
        shapeRenderer.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.WHITE
        shapeRenderer.rect(x, y, width, height)
        shapeRenderer.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    }

    /**
     * Update player stats
     */
    fun updateStats(health: Float, maxHealth: Float, shield: Float, maxShield: Float, stamina: Float, maxStamina: Float) {
        this.health = health
        this.maxHealth = maxHealth
        this.shield = shield
        this.maxShield = maxShield
        this.stamina = stamina
        this.maxStamina = maxStamina
    }

    /**
     * Update ping
     */
    fun updatePing(ping: Int) {
        this.ping = ping
    }

    /**
     * Resize viewport
     */
    fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
        shapeRenderer.dispose()
    }
}
