package com.aetheria.mmo.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.aetheria.mmo.managers.AudioManager
import com.aetheria.mmo.utils.ColorUtils
import com.aetheria.mmo.utils.Logger

/**
 * Settings Menu Widget
 * Provides UI for configuring game settings (audio, graphics, controls, etc.)
 */
class SettingsMenu(private val skin: Skin) : Window("Settings", skin) {

    private val contentTable = Table()
    private val tabTable = Table()
    private val settingsTable = Table()

    // Tabs
    private var currentTab = SettingsTab.AUDIO

    // Audio settings
    private val masterVolumeSlider: Slider
    private val musicVolumeSlider: Slider
    private val sfxVolumeSlider: Slider

    // Graphics settings
    private val vsyncCheckbox: CheckBox
    private val fullscreenCheckbox: CheckBox
    private val fpsLimitSelect: SelectBox<String>
    private val graphicsQualitySelect: SelectBox<String>

    // Gameplay settings
    private val autoLootCheckbox: CheckBox
    private val showDamageNumbersCheckbox: CheckBox
    private val cameraShakeCheckbox: CheckBox

    init {
        isMovable = true
        isModal = true
        setKeepWithinStage(true)

        // Initialize audio sliders
        masterVolumeSlider = Slider(0f, 1f, 0.01f, false, skin).apply {
            value = AudioManager.masterVolume
        }
        musicVolumeSlider = Slider(0f, 1f, 0.01f, false, skin).apply {
            value = AudioManager.musicVolume
        }
        sfxVolumeSlider = Slider(0f, 1f, 0.01f, false, skin).apply {
            value = AudioManager.sfxVolume
        }

        // Initialize graphics settings
        vsyncCheckbox = CheckBox("", skin)
        fullscreenCheckbox = CheckBox("", skin)
        fpsLimitSelect = SelectBox<String>(skin).apply {
            setItems("30", "60", "120", "144", "Unlimited")
            selected = "60"
        }
        graphicsQualitySelect = SelectBox<String>(skin).apply {
            setItems("Low", "Medium", "High", "Ultra")
            selected = "High"
        }

        // Initialize gameplay settings
        autoLootCheckbox = CheckBox("", skin)
        showDamageNumbersCheckbox = CheckBox("", skin).apply {
            isChecked = true
        }
        cameraShakeCheckbox = CheckBox("", skin).apply {
            isChecked = true
        }

        // Setup listeners
        setupListeners()

        // Build UI
        buildUI()

        setSize(600f, 500f)
        centerWindow()
    }

    private fun buildUI() {
        contentTable.clear()

        // Build tab buttons
        buildTabButtons()

        // Build settings content
        buildSettingsContent()

        // Add to window
        contentTable.add(tabTable).growX().row()
        contentTable.add(settingsTable).grow().pad(20f).row()

        // Add buttons
        val buttonTable = Table()
        val applyButton = TextButton("Apply", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    applySettings()
                }
            })
        }
        val closeButton = TextButton("Close", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    hide()
                }
            })
        }

        buttonTable.add(applyButton).width(100f).pad(5f)
        buttonTable.add(closeButton).width(100f).pad(5f)
        contentTable.add(buttonTable).row()

        add(contentTable).grow()
    }

    private fun buildTabButtons() {
        tabTable.clear()

        val audioTab = TextButton("Audio", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    switchTab(SettingsTab.AUDIO)
                }
            })
        }

        val graphicsTab = TextButton("Graphics", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    switchTab(SettingsTab.GRAPHICS)
                }
            })
        }

        val gameplayTab = TextButton("Gameplay", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    switchTab(SettingsTab.GAMEPLAY)
                }
            })
        }

        val controlsTab = TextButton("Controls", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    switchTab(SettingsTab.CONTROLS)
                }
            })
        }

        tabTable.add(audioTab).width(120f).pad(5f)
        tabTable.add(graphicsTab).width(120f).pad(5f)
        tabTable.add(gameplayTab).width(120f).pad(5f)
        tabTable.add(controlsTab).width(120f).pad(5f)
    }

    private fun buildSettingsContent() {
        settingsTable.clear()
        settingsTable.align(Align.topLeft)

        when (currentTab) {
            SettingsTab.AUDIO -> buildAudioSettings()
            SettingsTab.GRAPHICS -> buildGraphicsSettings()
            SettingsTab.GAMEPLAY -> buildGameplaySettings()
            SettingsTab.CONTROLS -> buildControlsSettings()
        }
    }

    private fun buildAudioSettings() {
        settingsTable.add(Label("Master Volume", skin)).left().padBottom(5f)
        settingsTable.add(masterVolumeSlider).width(200f).padBottom(5f)
        settingsTable.add(Label("${(masterVolumeSlider.value * 100).toInt()}%", skin)).padBottom(5f).row()

        settingsTable.add(Label("Music Volume", skin)).left().padBottom(5f)
        settingsTable.add(musicVolumeSlider).width(200f).padBottom(5f)
        settingsTable.add(Label("${(musicVolumeSlider.value * 100).toInt()}%", skin)).padBottom(5f).row()

        settingsTable.add(Label("SFX Volume", skin)).left().padBottom(5f)
        settingsTable.add(sfxVolumeSlider).width(200f).padBottom(5f)
        settingsTable.add(Label("${(sfxVolumeSlider.value * 100).toInt()}%", skin)).padBottom(5f).row()
    }

    private fun buildGraphicsSettings() {
        settingsTable.add(Label("VSync", skin)).left().padBottom(10f)
        settingsTable.add(vsyncCheckbox).left().padBottom(10f).row()

        settingsTable.add(Label("Fullscreen", skin)).left().padBottom(10f)
        settingsTable.add(fullscreenCheckbox).left().padBottom(10f).row()

        settingsTable.add(Label("FPS Limit", skin)).left().padBottom(10f)
        settingsTable.add(fpsLimitSelect).width(150f).padBottom(10f).row()

        settingsTable.add(Label("Graphics Quality", skin)).left().padBottom(10f)
        settingsTable.add(graphicsQualitySelect).width(150f).padBottom(10f).row()
    }

    private fun buildGameplaySettings() {
        settingsTable.add(Label("Auto Loot", skin)).left().padBottom(10f)
        settingsTable.add(autoLootCheckbox).left().padBottom(10f).row()

        settingsTable.add(Label("Show Damage Numbers", skin)).left().padBottom(10f)
        settingsTable.add(showDamageNumbersCheckbox).left().padBottom(10f).row()

        settingsTable.add(Label("Camera Shake", skin)).left().padBottom(10f)
        settingsTable.add(cameraShakeCheckbox).left().padBottom(10f).row()
    }

    private fun buildControlsSettings() {
        settingsTable.add(Label("Controls settings coming soon...", skin)).left().row()
        settingsTable.add(Label("WASD - Movement", skin)).left().padTop(10f).row()
        settingsTable.add(Label("Space - Jump", skin)).left().row()
        settingsTable.add(Label("Shift - Sprint", skin)).left().row()
        settingsTable.add(Label("Q, E, R, F - Abilities", skin)).left().row()
    }

    private fun switchTab(tab: SettingsTab) {
        currentTab = tab
        buildSettingsContent()
    }

    private fun setupListeners() {
        // Audio sliders
        masterVolumeSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                AudioManager.masterVolume = masterVolumeSlider.value
            }
        })

        musicVolumeSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                AudioManager.musicVolume = musicVolumeSlider.value
            }
        })

        sfxVolumeSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                AudioManager.sfxVolume = sfxVolumeSlider.value
            }
        })
    }

    private fun applySettings() {
        Logger.info("Settings", "Settings applied")
        // Settings are applied in real-time via listeners
        // This could trigger a save to preferences
    }

    private fun centerWindow() {
        setPosition(
            (stage?.width ?: 800f) / 2f - width / 2f,
            (stage?.height ?: 600f) / 2f - height / 2f
        )
    }

    fun show() {
        isVisible = true
        centerWindow()
    }

    fun hide() {
        isVisible = false
    }

    fun toggle() {
        if (isVisible) hide() else show()
    }

    enum class SettingsTab {
        AUDIO,
        GRAPHICS,
        GAMEPLAY,
        CONTROLS
    }
}
