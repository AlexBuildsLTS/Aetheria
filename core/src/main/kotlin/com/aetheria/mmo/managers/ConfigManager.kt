package com.aetheria.mmo.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.Disposable

/**
 * Config Manager
 * Manages game settings and preferences
 * Handles saving/loading configuration
 */
object ConfigManager : Disposable {

    private lateinit var prefs: Preferences

    // Graphics settings
    var fullscreen = false
    var vsync = true
    var targetFps = 60
    var renderDistance = 100f
    var shadowQuality = ShadowQuality.MEDIUM
    var textureQuality = TextureQuality.HIGH
    var antiAliasing = true

    // Audio settings
    var masterVolume = 1f
    var musicVolume = 0.7f
    var sfxVolume = 1f
    var voiceVolume = 1f

    // Gameplay settings
    var mouseSensitivity = 0.5f
    var invertY = false
    var autoRun = false
    var showDamageNumbers = true
    var showPlayerNames = true
    var cameraShake = true

    // Network settings
    var autoConnect = true
    var showPing = true
    var interpolation = true

    // UI settings
    var uiScale = 1f
    var showMinimap = true
    var showFps = false
    var chatOpacity = 0.8f

    // Keybindings
    val keyBindings = mutableMapOf<String, Int>()

    enum class ShadowQuality { OFF, LOW, MEDIUM, HIGH, ULTRA }
    enum class TextureQuality { LOW, MEDIUM, HIGH, ULTRA }

    /**
     * Initializes the config manager
     */
    fun initialize() {
        prefs = Gdx.app.getPreferences("AetheriaConfig")
        loadSettings()
        Gdx.app.log("ConfigManager", "Config manager initialized")
    }

    /**
     * Loads settings from preferences
     */
    fun loadSettings() {
        // Graphics
        fullscreen = prefs.getBoolean("fullscreen", false)
        vsync = prefs.getBoolean("vsync", true)
        targetFps = prefs.getInteger("targetFps", 60)
        renderDistance = prefs.getFloat("renderDistance", 100f)
        shadowQuality = ShadowQuality.valueOf(prefs.getString("shadowQuality", "MEDIUM"))
        textureQuality = TextureQuality.valueOf(prefs.getString("textureQuality", "HIGH"))
        antiAliasing = prefs.getBoolean("antiAliasing", true)

        // Audio
        masterVolume = prefs.getFloat("masterVolume", 1f)
        musicVolume = prefs.getFloat("musicVolume", 0.7f)
        sfxVolume = prefs.getFloat("sfxVolume", 1f)
        voiceVolume = prefs.getFloat("voiceVolume", 1f)

        // Gameplay
        mouseSensitivity = prefs.getFloat("mouseSensitivity", 0.5f)
        invertY = prefs.getBoolean("invertY", false)
        autoRun = prefs.getBoolean("autoRun", false)
        showDamageNumbers = prefs.getBoolean("showDamageNumbers", true)
        showPlayerNames = prefs.getBoolean("showPlayerNames", true)
        cameraShake = prefs.getBoolean("cameraShake", true)

        // Network
        autoConnect = prefs.getBoolean("autoConnect", true)
        showPing = prefs.getBoolean("showPing", true)
        interpolation = prefs.getBoolean("interpolation", true)

        // UI
        uiScale = prefs.getFloat("uiScale", 1f)
        showMinimap = prefs.getBoolean("showMinimap", true)
        showFps = prefs.getBoolean("showFps", false)
        chatOpacity = prefs.getFloat("chatOpacity", 0.8f)

        Gdx.app.log("ConfigManager", "Settings loaded")
    }

    /**
     * Saves settings to preferences
     */
    fun saveSettings() {
        // Graphics
        prefs.putBoolean("fullscreen", fullscreen)
        prefs.putBoolean("vsync", vsync)
        prefs.putInteger("targetFps", targetFps)
        prefs.putFloat("renderDistance", renderDistance)
        prefs.putString("shadowQuality", shadowQuality.name)
        prefs.putString("textureQuality", textureQuality.name)
        prefs.putBoolean("antiAliasing", antiAliasing)

        // Audio
        prefs.putFloat("masterVolume", masterVolume)
        prefs.putFloat("musicVolume", musicVolume)
        prefs.putFloat("sfxVolume", sfxVolume)
        prefs.putFloat("voiceVolume", voiceVolume)

        // Gameplay
        prefs.putFloat("mouseSensitivity", mouseSensitivity)
        prefs.putBoolean("invertY", invertY)
        prefs.putBoolean("autoRun", autoRun)
        prefs.putBoolean("showDamageNumbers", showDamageNumbers)
        prefs.putBoolean("showPlayerNames", showPlayerNames)
        prefs.putBoolean("cameraShake", cameraShake)

        // Network
        prefs.putBoolean("autoConnect", autoConnect)
        prefs.putBoolean("showPing", showPing)
        prefs.putBoolean("interpolation", interpolation)

        // UI
        prefs.putFloat("uiScale", uiScale)
        prefs.putBoolean("showMinimap", showMinimap)
        prefs.putBoolean("showFps", showFps)
        prefs.putFloat("chatOpacity", chatOpacity)

        prefs.flush()
        Gdx.app.log("ConfigManager", "Settings saved")
    }

    /**
     * Resets all settings to defaults
     */
    fun resetToDefaults() {
        fullscreen = false
        vsync = true
        targetFps = 60
        renderDistance = 100f
        shadowQuality = ShadowQuality.MEDIUM
        textureQuality = TextureQuality.HIGH
        antiAliasing = true

        masterVolume = 1f
        musicVolume = 0.7f
        sfxVolume = 1f
        voiceVolume = 1f

        mouseSensitivity = 0.5f
        invertY = false
        autoRun = false
        showDamageNumbers = true
        showPlayerNames = true
        cameraShake = true

        autoConnect = true
        showPing = true
        interpolation = true

        uiScale = 1f
        showMinimap = true
        showFps = false
        chatOpacity = 0.8f

        saveSettings()
    }

    /**
     * Applies graphics settings
     */
    fun applyGraphicsSettings() {
        if (fullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
        } else {
            Gdx.graphics.setWindowedMode(1280, 720)
        }

        Gdx.graphics.setVSync(vsync)
        Gdx.graphics.setForegroundFPS(targetFps)
    }

    /**
     * Applies audio settings
     */
    fun applyAudioSettings() {
        AudioManager.masterVolume = masterVolume
        AudioManager.musicVolume = musicVolume
        AudioManager.sfxVolume = sfxVolume
    }

    override fun dispose() {
        saveSettings()
        Gdx.app.log("ConfigManager", "Config manager disposed")
    }
}
