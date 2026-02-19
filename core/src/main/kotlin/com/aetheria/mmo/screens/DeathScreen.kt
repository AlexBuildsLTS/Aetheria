package com.aetheria.mmo.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.aetheria.mmo.utils.ColorUtils
import com.aetheria.mmo.utils.Logger

/**
 * Death Screen
 * Displayed when player dies
 * Shows death statistics and respawn options
 */
class DeathScreen : ScreenAdapter() {

    private lateinit var stage: Stage
    private lateinit var skin: Skin
    private lateinit var table: Table

    // Death statistics
    private var killedBy: String = "Unknown Enemy"
    private var damageDealt: Int = 1250
    private var damageTaken: Int = 2500
    private var survivalTime: String = "5:32"

    private var respawnTimer = 10f
    private lateinit var respawnButton: TextButton
    private lateinit var timerLabel: Label

    override fun show() {
        stage = Stage(ScreenViewport())
        Gdx.input.inputProcessor = stage

        skin = Skin(Gdx.files.internal("ui/skin/metalui.json"))

        buildUI()

        Logger.info("DeathScreen", "Death screen loaded")
    }

    private fun buildUI() {
        table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        // Death title
        val deathLabel = Label("YOU DIED", skin).apply {
            setFontScale(3f)
            color = ColorUtils.MYTHIC
            setAlignment(Align.center)
        }

        // Killed by
        val killedByLabel = Label("Killed by: $killedBy", skin).apply {
            setFontScale(1.2f)
            color = ColorUtils.UI_TEXT
        }

        // Statistics table
        val statsTable = Table()
        statsTable.background = skin.getDrawable("default-rect")
        statsTable.pad(20f)

        val statsTitle = Label("Death Statistics", skin).apply {
            setFontScale(1.3f)
            color = ColorUtils.CYBER_BLUE
        }
        statsTable.add(statsTitle).colspan(2).padBottom(15f).row()

        // Add stats
        addStatRow(statsTable, "Damage Dealt:", damageDealt.toString())
        addStatRow(statsTable, "Damage Taken:", damageTaken.toString())
        addStatRow(statsTable, "Survival Time:", survivalTime)

        // Respawn timer
        timerLabel = Label("Respawn available in: ${respawnTimer.toInt()}s", skin).apply {
            color = ColorUtils.STAMINA_COLOR
        }

        // Respawn button
        respawnButton = TextButton("Respawn", skin).apply {
            isDisabled = true
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    respawn()
                }
            })
        }

        // Spectate button
        val spectateButton = TextButton("Spectate", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    spectate()
                }
            })
        }

        // Main menu button
        val mainMenuButton = TextButton("Main Menu", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    returnToMainMenu()
                }
            })
        }

        // Layout
        table.add(deathLabel).padBottom(30f).row()
        table.add(killedByLabel).padBottom(20f).row()
        table.add(statsTable).padBottom(30f).row()
        table.add(timerLabel).padBottom(20f).row()

        val buttonTable = Table()
        buttonTable.add(respawnButton).width(150f).padRight(10f)
        buttonTable.add(spectateButton).width(150f).padRight(10f)
        buttonTable.add(mainMenuButton).width(150f)

        table.add(buttonTable).row()
    }

    private fun addStatRow(table: Table, label: String, value: String) {
        val labelWidget = Label(label, skin)
        val valueWidget = Label(value, skin).apply {
            color = ColorUtils.LEGENDARY
        }
        table.add(labelWidget).left().padRight(20f)
        table.add(valueWidget).right().row()
    }

    private fun respawn() {
        Logger.info("DeathScreen", "Respawning player")
        // TODO: Respawn player and return to game
        // game.setScreen(GameWorldScreen())
    }

    private fun spectate() {
        Logger.info("DeathScreen", "Entering spectate mode")
        // TODO: Enter spectate mode
    }

    private fun returnToMainMenu() {
        Logger.info("DeathScreen", "Returning to main menu")
        // TODO: Return to main menu
        // game.setScreen(MainMenuScreen())
    }

    override fun render(delta: Float) {
        // Dark red tint for death screen
        Gdx.gl.glClearColor(0.1f, 0.0f, 0.0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update respawn timer
        if (respawnTimer > 0f) {
            respawnTimer -= delta
            timerLabel.setText("Respawn available in: ${respawnTimer.toInt()}s")

            if (respawnTimer <= 0f) {
                respawnTimer = 0f
                timerLabel.setText("Respawn available!")
                timerLabel.color = ColorUtils.HEALTH_COLOR
                respawnButton.isDisabled = false
            }
        }

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }

    /**
     * Set death information
     */
    fun setDeathInfo(killedBy: String, damageDealt: Int, damageTaken: Int, survivalTime: String) {
        this.killedBy = killedBy
        this.damageDealt = damageDealt
        this.damageTaken = damageTaken
        this.survivalTime = survivalTime
    }
}
