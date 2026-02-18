package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

/**
 * Shield Component
 * Energy shield that absorbs damage before health
 */
class ShieldComponent : Component, Pool.Poolable {
    var current: Float = 0f
    var max: Float = 100f
    var regenRate: Float = 5f // Shield per second
    var regenDelay: Float = 3f // Seconds before regen starts
    var timeSinceLastHit: Float = 0f
    var isActive: Boolean = true

    fun absorbDamage(amount: Float): Float {
        if (!isActive || current <= 0f) return amount

        val absorbed = minOf(amount, current)
        current -= absorbed
        timeSinceLastHit = 0f

        return amount - absorbed // Return remaining damage
    }

    fun update(deltaTime: Float) {
        if (!isActive) return

        timeSinceLastHit += deltaTime

        // Start regenerating after delay
        if (timeSinceLastHit >= regenDelay && current < max) {
            current = minOf(current + regenRate * deltaTime, max)
        }
    }

    fun getShieldPercent(): Float = if (max > 0f) current / max else 0f

    override fun reset() {
        current = 0f
        max = 100f
        regenRate = 5f
        regenDelay = 3f
        timeSinceLastHit = 0f
        isActive = true
    }
}
