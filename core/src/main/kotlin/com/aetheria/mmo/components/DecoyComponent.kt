package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool

/**
 * Decoy Component
 * Marks entity as a decoy/hologram that attracts enemy attention
 * Used for tactical abilities and misdirection
 */
class DecoyComponent : Component, Pool.Poolable {
    var owner: Entity? = null
    var lifetime: Float = 10f // Seconds before decoy expires
    var timeAlive: Float = 0f
    var health: Float = 1f // Decoys can be destroyed
    var threatLevel: Float = 100f // How much enemies prioritize this target
    var isActive: Boolean = true
    var canTakeDamage: Boolean = true
    var explodeOnDeath: Boolean = false
    var explosionDamage: Float = 50f
    var explosionRadius: Float = 5f

    fun update(deltaTime: Float) {
        timeAlive += deltaTime
        if (timeAlive >= lifetime) {
            isActive = false
        }
    }

    fun takeDamage(amount: Float) {
        if (canTakeDamage) {
            health -= amount
            if (health <= 0f) {
                isActive = false
            }
        }
    }

    fun isExpired(): Boolean = !isActive || timeAlive >= lifetime

    override fun reset() {
        owner = null
        lifetime = 10f
        timeAlive = 0f
        health = 1f
        threatLevel = 100f
        isActive = true
        canTakeDamage = true
        explodeOnDeath = false
        explosionDamage = 50f
        explosionRadius = 5f
    }
}
