package com.aetheria.mmo.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.Disposable

/**
 * Screen Manager
 * Manages screen transitions and lifecycle
 * Handles screen stack for overlays
 */
object ScreenManager : Disposable {

    private var currentScreen: Screen? = null
    private val screenStack = mutableListOf<Screen>()
    private val screenCache = mutableMapOf<String, Screen>()

    /**
     * Sets the current screen
     */
    fun setScreen(screen: Screen) {
        currentScreen?.hide()
        currentScreen = screen
        currentScreen?.show()
        currentScreen?.resize(Gdx.graphics.width, Gdx.graphics.height)
        Gdx.app.log("ScreenManager", "Screen changed to: ${screen.javaClass.simpleName}")
    }

    /**
     * Sets screen by name from cache
     */
    fun setScreen(screenName: String) {
        val screen = screenCache[screenName]
        if (screen != null) {
            setScreen(screen)
        } else {
            Gdx.app.error("ScreenManager", "Screen not found in cache: $screenName")
        }
    }

    /**
     * Pushes a screen onto the stack (for overlays)
     */
    fun pushScreen(screen: Screen) {
        currentScreen?.let { screenStack.add(it) }
        setScreen(screen)
    }

    /**
     * Pops the top screen from the stack
     */
    fun popScreen() {
        if (screenStack.isNotEmpty()) {
            val previousScreen = screenStack.removeAt(screenStack.size - 1)
            setScreen(previousScreen)
        } else {
            Gdx.app.log("ScreenManager", "No screen to pop")
        }
    }

    /**
     * Caches a screen for quick access
     */
    fun cacheScreen(name: String, screen: Screen) {
        screenCache[name] = screen
        Gdx.app.log("ScreenManager", "Screen cached: $name")
    }

    /**
     * Removes a screen from cache
     */
    fun uncacheScreen(name: String) {
        val screen = screenCache.remove(name)
        if (screen is Disposable) {
            screen.dispose()
        }
        Gdx.app.log("ScreenManager", "Screen uncached: $name")
    }

    /**
     * Gets the current screen
     */
    fun getCurrentScreen(): Screen? = currentScreen

    /**
     * Renders the current screen
     */
    fun render(deltaTime: Float) {
        currentScreen?.render(deltaTime)
    }

    /**
     * Resizes the current screen
     */
    fun resize(width: Int, height: Int) {
        currentScreen?.resize(width, height)
    }

    /**
     * Pauses the current screen
     */
    fun pause() {
        currentScreen?.pause()
    }

    /**
     * Resumes the current screen
     */
    fun resume() {
        currentScreen?.resume()
    }

    /**
     * Clears the screen stack
     */
    fun clearStack() {
        screenStack.clear()
    }

    override fun dispose() {
        currentScreen?.hide()
        if (currentScreen is Disposable) {
            (currentScreen as Disposable).dispose()
        }

        screenStack.forEach { screen ->
            if (screen is Disposable) {
                screen.dispose()
            }
        }
        screenStack.clear()

        screenCache.values.forEach { screen ->
            if (screen is Disposable) {
                screen.dispose()
            }
        }
        screenCache.clear()

        Gdx.app.log("ScreenManager", "Screen manager disposed")
    }
}
