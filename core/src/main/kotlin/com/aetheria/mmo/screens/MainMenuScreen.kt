package com.aetheria.mmo.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20

class MainMenuScreen : ScreenAdapter() {

    override fun show() {
        Gdx.app.log("MainMenu", "Main Menu Loaded!")
    }

    override fun render(delta: Float) {
        // Render a deep void blue for the menu background
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Logic to draw UI buttons will go here later
    }
}