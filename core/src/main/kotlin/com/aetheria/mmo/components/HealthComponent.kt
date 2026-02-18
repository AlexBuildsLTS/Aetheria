package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component

/**
 * Health Component
 * Tracks entity health, max health, and regeneration
 */
class HealthComponent : Component {
    var current: Float = 100f
    var max: Float = 100f
    var regen: Float = 1f // HP per second
    var isDead: Boolean = false

    fun takeDamage(amount: Float) {
        current = (current - amount).coerceAtLeast(0f)
        if (current <= 0f) {
            isDead = true
        }
    }

    fun heal(amount: Float) {
        current = (current + amount).coerceAtMost(max)
        if (current > 0f) {
            isDead = false
        }
    }

    fun getHealthPercent(): Float = if (max > 0f) current / max else 0f
}
