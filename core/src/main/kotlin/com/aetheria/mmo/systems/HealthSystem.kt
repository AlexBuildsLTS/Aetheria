package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.aetheria.mmo.components.HealthComponent
import com.aetheria.mmo.components.StaminaComponent

/**
 * Health System
 * Handles health and stamina regeneration
 */
class HealthSystem : IteratingSystem(
    Family.all(HealthComponent::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val health = entity.getComponent(HealthComponent::class.java)
        val stamina = entity.getComponent(StaminaComponent::class.java)

        // Regenerate health
        if (!health.isDead && health.current < health.max) {
            health.heal(health.regen * deltaTime)
        }

        // Regenerate stamina
        stamina?.regenerate(deltaTime)
    }
}
