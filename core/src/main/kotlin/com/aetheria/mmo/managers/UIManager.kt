package com.aetheria.mmo.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ScreenViewport

/**
 * UI Manager
 * Manages all UI elements and Scene2D stages
 * Handles HUD, menus, and overlays
 */
object UIManager : Disposable {

    lateinit var stage: Stage
        private set

    lateinit var hudStage: Stage
        private set

    lateinit var skin: Skin
        private set

    lateinit var font: BitmapFont
        private set

    private val batch = SpriteBatch()

    // UI state
    private var isInventoryOpen = false
    private var isMapOpen = false
    private var isSettingsOpen = false
    private var isChatFocused = false

    // UI scale
    var uiScale = 1f
        set(value) {
            field = value.coerceIn(0.5f, 2f)
            updateUIScale()
        }

    /**
     * Initializes the UI manager
     */
    fun initialize() {
        // Create stages
        stage = Stage(ScreenViewport(), batch)
        hudStage = Stage(ScreenViewport(), batch)

        // Load skin
        skin = Skin(Gdx.files.internal("ui/uiskin.json"))

        // Create font
        font = BitmapFont()

        Gdx.app.log("UIManager", "UI manager initialized")
    }

    /**
     * Updates UI
     */
    fun update(deltaTime: Float) {
        stage.act(deltaTime)
        hudStage.act(deltaTime)
    }

    /**
     * Renders UI
     */
    fun render() {
        hudStage.draw()
        stage.draw()
    }

    /**
     * Resizes UI
     */
    fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        hudStage.viewport.update(width, height, true)
    }

    /**
     * Opens inventory
     */
    fun openInventory() {
        isInventoryOpen = true
        Gdx.app.log("UIManager", "Inventory opened")
    }

    /**
     * Closes inventory
     */
    fun closeInventory() {
        isInventoryOpen = false
        Gdx.app.log("UIManager", "Inventory closed")
    }

    /**
     * Toggles inventory
     */
    fun toggleInventory() {
        if (isInventoryOpen) closeInventory() else openInventory()
    }

    /**
     * Opens map
     */
    fun openMap() {
        isMapOpen = true
        Gdx.app.log("UIManager", "Map opened")
    }

    /**
     * Closes map
     */
    fun closeMap() {
        isMapOpen = false
        Gdx.app.log("UIManager", "Map closed")
    }

    /**
     * Toggles map
     */
    fun toggleMap() {
        if (isMapOpen) closeMap() else openMap()
    }

    /**
     * Opens settings
     */
    fun openSettings() {
        isSettingsOpen = true
        Gdx.app.log("UIManager", "Settings opened")
    }

    /**
     * Closes settings
     */
    fun closeSettings() {
        isSettingsOpen = false
        Gdx.app.log("UIManager", "Settings closed")
    }

    /**
     * Toggles settings
     */
    fun toggleSettings() {
        if (isSettingsOpen) closeSettings() else openSettings()
    }

    /**
     * Focuses chat input
     */
    fun focusChat() {
        isChatFocused = true
    }

    /**
     * Unfocuses chat input
     */
    fun unfocusChat() {
        isChatFocused = false
    }

    /**
     * Closes all UI windows
     */
    fun closeAll() {
        closeInventory()
        closeMap()
        closeSettings()
        unfocusChat()
    }

    /**
     * Checks if any UI is blocking input
     */
    fun isUIBlocking(): Boolean {
        return isInventoryOpen || isMapOpen || isSettingsOpen || isChatFocused
    }

    /**
     * Updates UI scale
     */
    private fun updateUIScale() {
        stage.root.setScale(uiScale)
        hudStage.root.setScale(uiScale)
    }

    /**
     * Shows notification
     */
    fun showNotification(message: String, duration: Float = 3f) {
        Gdx.app.log("UIManager", "Notification: $message")
        // TODO: Implement notification system
    }

    /**
     * Shows damage number
     */
    fun showDamageNumber(x: Float, y: Float, damage: Int, isCritical: Boolean = false) {
        // TODO: Implement floating damage numbers
    }

    /**
     * Shows heal number
     */
    fun showHealNumber(x: Float, y: Float, amount: Int) {
        // TODO: Implement floating heal numbers
    }

    // Getters
    fun isInventoryOpen(): Boolean = isInventoryOpen
    fun isMapOpen(): Boolean = isMapOpen
    fun isSettingsOpen(): Boolean = isSettingsOpen
    fun isChatFocused(): Boolean = isChatFocused

    override fun dispose() {
        stage.dispose()
        hudStage.dispose()
        skin.dispose()
        font.dispose()
        batch.dispose()
        Gdx.app.log("UIManager", "UI manager disposed")
    }
}
