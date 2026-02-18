package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.aetheria.mmo.components.*
import com.aetheria.mmo.events.*

/**
 * Projectile System
 * Handles projectile movement and collision
 */
class ProjectileSystem : IteratingSystem(
    Family.all(
        VelocityComponent::class.java,
        TransformComponent::class.java,
        CollisionComponent::class.java
    ).exclude(PlayerComponent::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val velocity = entity.getComponent(VelocityComponent::class.java)
        val transform = entity.getComponent(TransformComponent::class.java)
        val collision = entity.getComponent(CollisionComponent::class.java)
        val timeDebt = entity.getComponent(TimeDebtComponent::class.java)

        // Move projectile
        transform.position.add(
            velocity.linear.x * deltaTime,
            velocity.linear.y * deltaTime,
            velocity.linear.z * deltaTime
        )

        // Check lifetime (using TimeDebtComponent as lifetime tracker)
        timeDebt?.let {
            it.addDebt(deltaTime)
            if (it.debtAmount >= it.maxDebt) {
                engine.removeEntity(entity)
                return
            }
        }

        // Check for collisions
        if (collision.collidingWith.isNotEmpty()) {
            // Hit something - create impact effect and remove projectile
            collision.collidingWith.forEach { target ->
                handleProjectileHit(entity, target)
            }
            engine.removeEntity(entity)
        }

        // Remove if out of bounds
        if (transform.position.len() > 200f) {
            engine.removeEntity(entity)
        }
    }

    private fun handleProjectileHit(projectile: Entity, target: Entity) {
        // Post damage event
        val damage = 25f // TODO: Get from projectile component
        EventQueue.post(
            DamageEvent(
                target = target,
                source = projectile,
                amount = damage,
                damageType = DamageType.ENERGY
            )
        )
    }
}
