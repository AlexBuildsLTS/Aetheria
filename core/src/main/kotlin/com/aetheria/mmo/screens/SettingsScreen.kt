package com.aetheria.mmo.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.aetheria.mmo.managers.AudioManager
import com.aetheria.mmo.ui.SettingsMenu
import com.aetheria.mmo.utils.Logger

/**
 * Settings Screen
 * Full-screen settings interface
 * Uses SettingsMenu widget for the actual settings UI
 */
class SettingsScreen : ScreenAdapter() {

    private lateinit var stage: Stage
    private lateinit var skin: Skin
    private lateinit var settingsMenu: SettingsMenu

    override fun show() {
        stage = Stage(ScreenViewport())
        Gdx.input.inputProcessor = stage

        // Load skin
        skin = Skin(Gdx.files.internal("ui/uiskin.json"))

        // Create settings menu
        settingsMenu = SettingsMenu(skin)
        settingsMenu.setFillParent(true)
        stage.addActor(settingsMenu)

        settingsMenu.show()

        Logger.info("SettingsScreen", "Settings screen loaded")
    }

    override fun render(delta: Float) {
        // Clear screen
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update and draw stage
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
}
