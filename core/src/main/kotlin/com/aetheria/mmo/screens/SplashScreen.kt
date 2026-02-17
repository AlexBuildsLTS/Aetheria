package com.aetheria.mmo.screens

import com.aetheria.mmo.AetheriaGame
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter // Using ScreenAdapter is cleaner
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera

// Make sure your SplashScreen class implements Screen or extends ScreenAdapter
class SplashScreen(private val game: AetheriaGame) : ScreenAdapter() {

    private val camera = OrthographicCamera().apply {
        setToOrtho(false, 1280f, 720f)
    }

    // This is the most important method to fix the black screen
    override fun render(delta: Float) {
        // --- START OF FIX ---

        // 1. Clear the screen
        // Sets the background color (e.g., a dark gray) and clears the buffer.
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // 2. Update the camera
        camera.update()
        game.batch.projectionMatrix = camera.combined

        // 3. Begin drawing
        game.batch.begin()

        // 4. Draw something! (e.g., text, a logo from ResourceManager)
        // If you haven't loaded fonts, this will also fail. For now, a simple clear is enough.
        // Example: game.font.draw(game.batch, "Loading...", 100f, 100f)

        // 5. End drawing
        game.batch.end()

        // --- END OF FIX ---

        // You would also add logic here to check if assets are loaded
        // and then switch to the main menu screen:
        // if (ResourceManager.isLoaded()) {
        //     game.setScreen(MainMenuScreen(game))
        // }
    }

    // It's good practice to implement other Screen methods too
    override fun show() {
        // This is called when the screen becomes the active screen.
    }

    override fun hide() {
        // This is called when the screen is no longer the active screen.
    }

    override fun dispose() {
        // Dispose of resources specific to this screen if necessary.
        // Don't dispose of the shared 'game.batch' here!
    }
}
