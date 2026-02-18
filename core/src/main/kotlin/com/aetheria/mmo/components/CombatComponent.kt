package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component

/**
 * Combat Component
 * Tracks abilities, cooldowns, and combat stats
 */
class CombatComponent : Component {
    // Ability cooldowns (in seconds)
    val abilityCooldowns = mutableMapOf(
        "Q" to 0f,
        "E" to 0f,
        "R" to 0f,
        "F" to 0f
    )

    // Max cooldowns for each ability
    val maxCooldowns = mutableMapOf(
        "Q" to 5f,   // Basic attack - 5 sec
        "E" to 8f,   // Special ability - 8 sec
        "R" to 15f,  // Ultimate - 15 sec
        "F" to 10f   // Utility - 10 sec
    )

    // Combat stats
    var attackPower: Float = 10f
    var attackSpeed: Float = 1f
    var critChance: Float = 0.1f
    var critDamage: Float = 2f
    var isAttacking: Boolean = false

    fun canUseAbility(key: String): Boolean {
        return (abilityCooldowns[key] ?: Float.MAX_VALUE) <= 0f
    }

    fun useAbility(key: String) {
        abilityCooldowns[key] = maxCooldowns[key] ?: 0f
    }

    fun updateCooldowns(deltaTime: Float) {
        for (key in abilityCooldowns.keys) {
            val current = abilityCooldowns[key] ?: 0f
            if (current > 0f) {
                abilityCooldowns[key] = (current - deltaTime).coerceAtLeast(0f)
            }
        }
    }

    fun getCooldownPercent(key: String): Float {
        val current = abilityCooldowns[key] ?: 0f
        val max = maxCooldowns[key] ?: 1f
        return if (max > 0f) current / max else 0f
    }
}
