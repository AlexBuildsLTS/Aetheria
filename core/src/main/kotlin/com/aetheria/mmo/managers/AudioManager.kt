package com.aetheria.mmo.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Disposable

/**
 * Audio Manager
 * Manages music and sound effects with volume control
 * Supports 3D positional audio and audio ducking
 */
object AudioManager : Disposable {

    private val music = mutableMapOf<String, Music>()
    private val sounds = mutableMapOf<String, Sound>()
    private var currentMusic: Music? = null
    private var currentMusicName: String? = null

    // Volume settings
    var masterVolume = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
            updateMusicVolume()
        }

    var musicVolume = 0.7f
        set(value) {
            field = value.coerceIn(0f, 1f)
            updateMusicVolume()
        }

    var sfxVolume = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
        }

    private var isMuted = false

    /**
     * Loads a music track
     */
    fun loadMusic(name: String, path: String) {
        if (!music.containsKey(name)) {
            try {
                val musicTrack = Gdx.audio.newMusic(Gdx.files.internal(path))
                music[name] = musicTrack
                Gdx.app.log("AudioManager", "Loaded music: $name")
            } catch (e: Exception) {
                Gdx.app.error("AudioManager", "Failed to load music: $path", e)
            }
        }
    }

    /**
     * Loads a sound effect
     */
    fun loadSound(name: String, path: String) {
        if (!sounds.containsKey(name)) {
            try {
                val sound = Gdx.audio.newSound(Gdx.files.internal(path))
                sounds[name] = sound
                Gdx.app.log("AudioManager", "Loaded sound: $name")
            } catch (e: Exception) {
                Gdx.app.error("AudioManager", "Failed to load sound: $path", e)
            }
        }
    }

    /**
     * Plays a music track
     */
    fun playMusic(name: String, loop: Boolean = true, fadeIn: Boolean = false) {
        val musicTrack = music[name]
        if (musicTrack == null) {
            Gdx.app.error("AudioManager", "Music not found: $name")
            return
        }

        // Stop current music
        currentMusic?.stop()

        // Play new music
        musicTrack.isLooping = loop
        musicTrack.volume = if (fadeIn) 0f else getEffectiveMusicVolume()
        musicTrack.play()

        currentMusic = musicTrack
        currentMusicName = name

        // TODO: Implement fade-in
    }

    /**
     * Plays a sound effect
     */
    fun playSound(name: String, volume: Float = 1f, pitch: Float = 1f, pan: Float = 0f): Long {
        val sound = sounds[name]
        if (sound == null) {
            Gdx.app.error("AudioManager", "Sound not found: $name")
            return -1
        }

        val effectiveVolume = if (isMuted) 0f else volume * sfxVolume * masterVolume
        return sound.play(effectiveVolume, pitch, pan)
    }

    /**
     * Plays a 3D positional sound
     */
    fun playSound3D(name: String, listenerX: Float, listenerZ: Float,
                    sourceX: Float, sourceZ: Float, maxDistance: Float = 50f): Long {
        val dx = sourceX - listenerX
        val dz = sourceZ - listenerZ
        val distance = kotlin.math.sqrt(dx * dx + dz * dz)

        if (distance > maxDistance) return -1

        // Calculate volume based on distance
        val volume = 1f - (distance / maxDistance).coerceIn(0f, 1f)

        // Calculate pan based on position
        val pan = (dx / maxDistance).coerceIn(-1f, 1f)

        return playSound(name, volume, 1f, pan)
    }

    /**
     * Stops the current music
     */
    fun stopMusic() {
        currentMusic?.stop()
        currentMusic = null
        currentMusicName = null
    }

    /**
     * Pauses the current music
     */
    fun pauseMusic() {
        currentMusic?.pause()
    }

    /**
     * Resumes the current music
     */
    fun resumeMusic() {
        currentMusic?.play()
    }

    /**
     * Stops a specific sound instance
     */
    fun stopSound(name: String, soundId: Long) {
        sounds[name]?.stop(soundId)
    }

    /**
     * Stops all sounds
     */
    fun stopAllSounds() {
        sounds.values.forEach { it.stop() }
    }

    /**
     * Mutes all audio
     */
    fun mute() {
        isMuted = true
        updateMusicVolume()
    }

    /**
     * Unmutes all audio
     */
    fun unmute() {
        isMuted = false
        updateMusicVolume()
    }

    /**
     * Toggles mute
     */
    fun toggleMute() {
        if (isMuted) unmute() else mute()
    }

    /**
     * Updates music volume
     */
    private fun updateMusicVolume() {
        currentMusic?.volume = getEffectiveMusicVolume()
    }

    /**
     * Gets the effective music volume
     */
    private fun getEffectiveMusicVolume(): Float {
        return if (isMuted) 0f else musicVolume * masterVolume
    }

    /**
     * Checks if music is playing
     */
    fun isMusicPlaying(): Boolean = currentMusic?.isPlaying ?: false

    /**
     * Gets the current music name
     */
    fun getCurrentMusicName(): String? = currentMusicName

    /**
     * Preloads common audio files
     */
    fun preloadCommonAudio() {
        // Music
        loadMusic("menu_theme", "audio/music/menu_theme.ogg")
        loadMusic("combat_theme", "audio/music/combat_theme.ogg")
        loadMusic("ambient_rustlands", "audio/music/ambient_rustlands.ogg")

        // UI Sounds
        loadSound("ui_click", "audio/sfx/ui_click.ogg")
        loadSound("ui_hover", "audio/sfx/ui_hover.ogg")
        loadSound("ui_error", "audio/sfx/ui_error.ogg")

        // Combat Sounds
        loadSound("sword_swing", "audio/sfx/sword_swing.ogg")
        loadSound("hit_impact", "audio/sfx/hit_impact.ogg")
        loadSound("ability_cast", "audio/sfx/ability_cast.ogg")
        loadSound("footstep", "audio/sfx/footstep.ogg")
    }

    override fun dispose() {
        music.values.forEach { it.dispose() }
        sounds.values.forEach { it.dispose() }
        music.clear()
        sounds.clear()
        Gdx.app.log("AudioManager", "Audio resources disposed")
    }
}
