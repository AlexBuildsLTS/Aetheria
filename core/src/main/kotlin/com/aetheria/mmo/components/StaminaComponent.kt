package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component

/**
 * Stamina Component
 * Tracks entity stamina for abilities, sprinting, dodging
 */
class StaminaComponent : Component {
    var current: Float = 100f
    var max: Float = 100f
    var regen: Float = 10f // Stamina per second
    var isExhausted: Boolean = false

    fun consume(amount: Float): Boolean {
        return if (current >= amount) {
            current -= amount
            if (current <= 0f) {
                isExhausted = true
            }
            true
        } else {
            false
        }
    }

    fun regenerate(deltaTime: Float) {
        current = (current + regen * deltaTime).coerceAtMost(max)
        if (current > max * 0.25f) {
            isExhausted = false
        }
    }

    fun getStaminaPercent(): Float = if (max > 0f) current / max else 0f
}
