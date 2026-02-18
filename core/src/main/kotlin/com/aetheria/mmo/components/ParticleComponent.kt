package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.utils.Pool

/**
 * Particle Component
 * Manages particle effects attached to entities
 */
class ParticleComponent : Component, Pool.Poolable {
    var effect: ParticleEffect? = null
    var effectName: String = ""
    var isLooping: Boolean = false
    var autoRemove: Boolean = true // Remove component when effect finishes
    var scale: Float = 1f
    var timeAlive: Float = 0f
    var maxLifetime: Float = -1f // -1 = infinite

    override fun reset() {
        effect?.dispose()
        effect = null
        effectName = ""
        isLooping = false
        autoRemove = true
        scale = 1f
        timeAlive = 0f
        maxLifetime = -1f
    }
}
