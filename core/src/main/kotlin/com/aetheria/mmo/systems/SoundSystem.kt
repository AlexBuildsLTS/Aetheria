package com.aetheria.mmo.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*
import com.aetheria.mmo.managers.AudioManager
import com.aetheria.mmo.utils.Constants
import com.aetheria.mmo.utils.Logger

/**
 * Sound System
 * Handles 3D positional audio and sound effect playback
 * Manages sound attenuation based on distance from listener
 */
class SoundSystem(private val listenerEntity: Entity? = null) : IteratingSystem(
    Family.all(TransformComponent::class.java).get()
) {

    private val transformMapper = ComponentMapper.getFor(TransformComponent::class.java)
    private val stateMapper = ComponentMapper.getFor(StateComponent::class.java)
    private val velocityMapper = ComponentMapper.getFor(VelocityComponent::class.java)

    private val listenerPosition = Vector3()
    private val soundQueue = mutableListOf<SoundEvent>()

    override fun update(deltaTime: Float) {
        // Update listener position
        listenerEntity?.let {
            val transform = transformMapper.get(it)
            listenerPosition.set(transform.position)
        }

        // Process sound queue
        processSoundQueue()

        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val state = stateMapper.get(entity) ?: return
        val transform = transformMapper.get(entity)
        val velocity = velocityMapper.get(entity)

        // Play footstep sounds based on state
        val currentState = state.currentState
        if (currentState == EntityState.WALKING || currentState == EntityState.RUNNING) {
            playFootsteps(entity, transform.position, currentState)
        }
    }

    /**
     * Play sound at position
     */
    fun playSound(soundName: String, position: Vector3, volume: Float = 1f, pitch: Float = 1f) {
        val distance = position.dst(listenerPosition)
        val attenuatedVolume = calculateAttenuation(distance, volume)

        if (attenuatedVolume > 0.01f) {
            soundQueue.add(SoundEvent(soundName, position.cpy(), attenuatedVolume, pitch))
        }
    }

    /**
     * Play sound at entity position
     */
    fun playSound(entity: Entity, soundName: String, volume: Float = 1f, pitch: Float = 1f) {
        val transform = transformMapper.get(entity) ?: return
        playSound(soundName, transform.position, volume, pitch)
    }

    /**
     * Play 2D sound (no attenuation)
     */
    fun playSound2D(soundName: String, volume: Float = 1f, pitch: Float = 1f) {
        AudioManager.playSound(soundName, volume, pitch)
    }

    /**
     * Play footstep sounds
     */
    private fun playFootsteps(entity: Entity, position: Vector3, state: EntityState) {
        // Simple footstep timing based on state time
        val stateComp = stateMapper.get(entity) ?: return
        val interval = if (state == EntityState.RUNNING) 0.3f else 0.5f

        // Check if it's time to play footstep sound
        val timeInCycle = stateComp.stateTime % interval
        if (timeInCycle < 0.016f) { // Approximately one frame at 60fps
            playSound("footstep", position, 0.3f, 1f + (Math.random().toFloat() - 0.5f) * 0.2f)
        }
    }

    /**
     * Calculate volume attenuation based on distance
     */
    private fun calculateAttenuation(distance: Float, baseVolume: Float): Float {
        if (distance <= 0f) return baseVolume

        val falloffDistance = Constants.AUDIO_FALLOFF_DISTANCE
        val attenuation = 1f - (distance / falloffDistance).coerceIn(0f, 1f)

        return baseVolume * attenuation
    }

    /**
     * Process queued sounds
     */
    private fun processSoundQueue() {
        soundQueue.forEach { event ->
            AudioManager.playSound(event.soundName, event.volume, event.pitch)
        }
        soundQueue.clear()
    }

    /**
     * Play combat sound
     */
    fun playCombatSound(type: CombatSoundType, position: Vector3) {
        val soundName = when (type) {
            CombatSoundType.SWORD_SWING -> "sword_swing"
            CombatSoundType.SWORD_HIT -> "sword_hit"
            CombatSoundType.ARROW_SHOOT -> "arrow_shoot"
            CombatSoundType.ARROW_HIT -> "arrow_hit"
            CombatSoundType.SPELL_CAST -> "spell_cast"
            CombatSoundType.SPELL_IMPACT -> "spell_impact"
            CombatSoundType.SHIELD_BLOCK -> "shield_block"
            CombatSoundType.DODGE -> "dodge"
        }
        playSound(soundName, position, 0.7f)
    }

    /**
     * Play UI sound
     */
    fun playUISound(type: UISoundType) {
        val soundName = when (type) {
            UISoundType.BUTTON_CLICK -> "ui_click"
            UISoundType.BUTTON_HOVER -> "ui_hover"
            UISoundType.MENU_OPEN -> "ui_menu_open"
            UISoundType.MENU_CLOSE -> "ui_menu_close"
            UISoundType.ITEM_PICKUP -> "ui_item_pickup"
            UISoundType.ITEM_DROP -> "ui_item_drop"
            UISoundType.NOTIFICATION -> "ui_notification"
            UISoundType.ERROR -> "ui_error"
        }
        playSound2D(soundName, 0.5f)
    }

    /**
     * Set listener entity
     */
    fun setListener(entity: Entity) {
        val transform = transformMapper.get(entity) ?: return
        listenerPosition.set(transform.position)
    }

    /**
     * Sound Event
     */
    private data class SoundEvent(
        val soundName: String,
        val position: Vector3,
        val volume: Float,
        val pitch: Float
    )
}

/**
 * Combat Sound Types
 */
enum class CombatSoundType {
    SWORD_SWING,
    SWORD_HIT,
    ARROW_SHOOT,
    ARROW_HIT,
    SPELL_CAST,
    SPELL_IMPACT,
    SHIELD_BLOCK,
    DODGE
}

/**
 * UI Sound Types
 */
enum class UISoundType {
    BUTTON_CLICK,
    BUTTON_HOVER,
    MENU_OPEN,
    MENU_CLOSE,
    ITEM_PICKUP,
    ITEM_DROP,
    NOTIFICATION,
    ERROR
}
