package com.aetheria.mmo.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.aetheria.mmo.components.HealthComponent
import com.aetheria.mmo.components.StaminaComponent
import com.aetheria.mmo.components.TransformComponent
import com.aetheria.mmo.components.ShieldComponent
import com.aetheria.mmo.events.DamageEvent
import com.aetheria.mmo.events.DamageType
import com.aetheria.mmo.events.DeathEvent
import com.aetheria.mmo.events.EventQueue

/**
 * Health System
 * Handles health and stamina regeneration, damage processing, and death
 */
class HealthSystem : IteratingSystem(
    Family.all(HealthComponent::class.java).get()
) {
    private val healthMapper = ComponentMapper.getFor(HealthComponent::class.java)
    private val transformMapper = ComponentMapper.getFor(TransformComponent::class.java)
    private val shieldMapper = ComponentMapper.getFor(ShieldComponent::class.java)

    init {
        // Subscribe to damage events
        EventQueue.subscribe<DamageEvent> { event ->
            processDamage(event)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val health = healthMapper.get(entity)
        val stamina = entity.getComponent(StaminaComponent::class.java)

        // Regenerate health
        if (!health.isDead && health.current < health.max) {
            health.heal(health.regen * deltaTime)
        }

        // Regenerate stamina
        stamina?.regenerate(deltaTime)

        // Check for death
        if (health.current <= 0f && !health.isDead) {
            health.isDead = true
            handleDeath(entity)
        }
    }

    /**
     * Process damage event
     */
    private fun processDamage(event: DamageEvent) {
        val health = healthMapper.get(event.target) ?: return
        val shield = shieldMapper.get(event.target)
        var remainingDamage = event.amount

        // Apply damage to shield first
        if (shield != null && shield.current > 0f && event.damageType != DamageType.TRUE_DAMAGE) {
            val shieldDamage = remainingDamage.coerceAtMost(shield.current)
            shield.current -= shieldDamage
            remainingDamage -= shieldDamage

            Gdx.app.log("HealthSystem", "Shield absorbed $shieldDamage damage. Shield: ${shield.current}/${shield.max}")
        }

        // Apply remaining damage to health
        if (remainingDamage > 0f) {
            health.takeDamage(remainingDamage)

            Gdx.app.log(
                "HealthSystem",
                "Entity took $remainingDamage damage ${if (event.isCritical) "CRITICAL!" else ""}. HP: ${health.current}/${health.max}"
            )
        }

        // TODO: Spawn damage number at position
        // This will be handled by a DamageNumberSystem or HUD
    }

    /**
     * Handle entity death
     */
    private fun handleDeath(entity: Entity) {
        Gdx.app.log("HealthSystem", "Entity died!")

        // Post death event
        EventQueue.post(DeathEvent(entity))

        // Remove entity from engine after a delay (for death animation)
        // For now, remove immediately
        engine.removeEntity(entity)
    }
}
