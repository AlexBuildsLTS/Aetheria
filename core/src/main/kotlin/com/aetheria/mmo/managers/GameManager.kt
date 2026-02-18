package com.aetheria.mmo.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable

/**
 * Game Manager
 * Central game state manager
 * Coordinates all game systems and managers
 */
object GameManager : Disposable {

    enum class GameState {
        INITIALIZING,
        MAIN_MENU,
        LOADING,
        IN_GAME,
        PAUSED,
        GAME_OVER,
        DISCONNECTED
    }

    // Current state
    var currentState = GameState.INITIALIZING
        private set

    // ECS Engine
    lateinit var engine: Engine
        private set

    // Game session data
    var playerId: String? = null
    var playerName: String? = null
    var sessionStartTime: Long = 0
    var isMultiplayer: Boolean = true

    // Performance tracking
    private var frameCount = 0
    private var fpsAccumulator = 0f
    private var averageFps = 60f

    /**
     * Initializes the game manager
     */
    fun initialize() {
        Gdx.app.log("GameManager", "Initializing game manager...")

        // Create ECS engine
        engine = Engine()

        // Initialize other managers
        ResourceManager.loadAll()
        AudioManager.preloadCommonAudio()

        currentState = GameState.MAIN_MENU
        Gdx.app.log("GameManager", "Game manager initialized")
    }

    /**
     * Updates the game manager
     */
    fun update(deltaTime: Float) {
        // Update ECS
        engine.update(deltaTime)

        // Update performance metrics
        updatePerformanceMetrics(deltaTime)

        // State-specific updates
        when (currentState) {
            GameState.IN_GAME -> updateInGame(deltaTime)
            GameState.PAUSED -> updatePaused(deltaTime)
            GameState.LOADING -> updateLoading(deltaTime)
            else -> {}
        }
    }

    /**
     * Updates in-game state
     */
    private fun updateInGame(deltaTime: Float) {
        // Game logic updates happen in ECS systems
    }

    /**
     * Updates paused state
     */
    private fun updatePaused(deltaTime: Float) {
        // Minimal updates while paused
    }

    /**
     * Updates loading state
     */
    private fun updateLoading(deltaTime: Float) {
        if (ResourceManager.update()) {
            // Loading complete
            changeState(GameState.IN_GAME)
        }
    }

    /**
     * Changes game state
     */
    fun changeState(newState: GameState) {
        val oldState = currentState
        currentState = newState

        Gdx.app.log("GameManager", "State changed: $oldState -> $newState")

        // Handle state transitions
        when (newState) {
            GameState.IN_GAME -> onEnterGame()
            GameState.PAUSED -> onPause()
            GameState.MAIN_MENU -> onReturnToMenu()
            GameState.GAME_OVER -> onGameOver()
            else -> {}
        }
    }

    /**
     * Called when entering game
     */
    private fun onEnterGame() {
        sessionStartTime = System.currentTimeMillis()
        AudioManager.playMusic("combat_theme", loop = true)
    }

    /**
     * Called when pausing
     */
    private fun onPause() {
        AudioManager.pauseMusic()
    }

    /**
     * Called when returning to menu
     */
    private fun onReturnToMenu() {
        AudioManager.stopMusic()
        AudioManager.playMusic("menu_theme", loop = true)
        cleanup()
    }

    /**
     * Called on game over
     */
    private fun onGameOver() {
        AudioManager.stopMusic()
    }

    /**
     * Starts a new game session
     */
    fun startNewGame(playerName: String, isMultiplayer: Boolean = true) {
        this.playerName = playerName
        this.isMultiplayer = isMultiplayer
        changeState(GameState.LOADING)
    }

    /**
     * Pauses the game
     */
    fun pause() {
        if (currentState == GameState.IN_GAME) {
            changeState(GameState.PAUSED)
        }
    }

    /**
     * Resumes the game
     */
    fun resume() {
        if (currentState == GameState.PAUSED) {
            changeState(GameState.IN_GAME)
            AudioManager.resumeMusic()
        }
    }

    /**
     * Quits to main menu
     */
    fun quitToMenu() {
        changeState(GameState.MAIN_MENU)
    }

    /**
     * Updates performance metrics
     */
    private fun updatePerformanceMetrics(deltaTime: Float) {
        frameCount++
        fpsAccumulator += deltaTime

        if (fpsAccumulator >= 1f) {
            averageFps = frameCount / fpsAccumulator
            frameCount = 0
            fpsAccumulator = 0f
        }
    }

    /**
     * Gets average FPS
     */
    fun getAverageFps(): Float = averageFps

    /**
     * Gets session duration in seconds
     */
    fun getSessionDuration(): Long {
        return if (sessionStartTime > 0) {
            (System.currentTimeMillis() - sessionStartTime) / 1000
        } else {
            0
        }
    }

    /**
     * Checks if game is paused
     */
    fun isPaused(): Boolean = currentState == GameState.PAUSED

    /**
     * Checks if game is in progress
     */
    fun isInGame(): Boolean = currentState == GameState.IN_GAME

    /**
     * Cleans up game session
     */
    private fun cleanup() {
        engine.removeAllEntities()
        playerId = null
        sessionStartTime = 0
    }

    override fun dispose() {
        cleanup()
        ResourceManager.dispose()
        AudioManager.dispose()
        Gdx.app.log("GameManager", "Game manager disposed")
    }
}
