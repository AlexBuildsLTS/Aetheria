package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.aetheria.mmo.components.*

/**
 * Collision System
 * Handles collision detection and response
 */
class CollisionSystem : IteratingSystem(
    Family.all(CollisionComponent::class.java, TransformComponent::class.java).get()
) {
    private val allEntities = mutableListOf<Entity>()

    override fun update(deltaTime: Float) {
        // Collect all entities
        allEntities.clear()
        entities.forEach { allEntities.add(it) }

        // Check collisions between all pairs
        for (i in 0 until allEntities.size) {
            for (j in i + 1 until allEntities.size) {
                checkCollision(allEntities[i], allEntities[j])
            }
        }

        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val collision = entity.getComponent(CollisionComponent::class.java)

        // Clean up old collisions
        val iterator = collision.collidingWith.iterator()
        while (iterator.hasNext()) {
            val other = iterator.next()
            if (!isColliding(entity, other)) {
                collision.onCollisionExit?.invoke(other)
                iterator.remove()
            }
        }
    }

    private fun checkCollision(entityA: Entity, entityB: Entity) {
        val collisionA = entityA.getComponent(CollisionComponent::class.java)
        val collisionB = entityB.getComponent(CollisionComponent::class.java)
        val transformA = entityA.getComponent(TransformComponent::class.java)
        val transformB = entityB.getComponent(TransformComponent::class.java)

        if (collisionA == null || collisionB == null) return
        if (transformA == null || transformB == null) return

        // Check layer masks
        if (!collisionA.isCollidingWith(collisionB.collisionLayer)) return
        if (!collisionB.isCollidingWith(collisionA.collisionLayer)) return

        // Simple sphere collision for now
        val distance = transformA.position.dst(transformB.position)
        val combinedRadius = collisionA.radius + collisionB.radius

        if (distance < combinedRadius) {
            // Collision detected
            if (!collisionA.collidingWith.contains(entityB)) {
                collisionA.collidingWith.add(entityB)
                collisionA.onCollisionEnter?.invoke(entityB)
            } else {
                collisionA.onCollisionStay?.invoke(entityB)
            }

            if (!collisionB.collidingWith.contains(entityA)) {
                collisionB.collidingWith.add(entityA)
                collisionB.onCollisionEnter?.invoke(entityA)
            } else {
                collisionB.onCollisionStay?.invoke(entityA)
            }

            // Resolve collision if both are solid
            if (collisionA.isSolid && collisionB.isSolid && !collisionA.isTrigger && !collisionB.isTrigger) {
                resolveCollision(entityA, entityB, transformA, transformB, distance, combinedRadius)
            }
        }
    }

    private fun isColliding(entityA: Entity, entityB: Entity): Boolean {
        val transformA = entityA.getComponent(TransformComponent::class.java) ?: return false
        val transformB = entityB.getComponent(TransformComponent::class.java) ?: return false
        val collisionA = entityA.getComponent(CollisionComponent::class.java) ?: return false
        val collisionB = entityB.getComponent(CollisionComponent::class.java) ?: return false

        val distance = transformA.position.dst(transformB.position)
        return distance < (collisionA.radius + collisionB.radius)
    }

    private fun resolveCollision(
        entityA: Entity,
        entityB: Entity,
        transformA: TransformComponent,
        transformB: TransformComponent,
        distance: Float,
        combinedRadius: Float
    ) {
        val collisionA = entityA.getComponent(CollisionComponent::class.java)
        val collisionB = entityB.getComponent(CollisionComponent::class.java)

        // Calculate overlap
        val overlap = combinedRadius - distance
        if (overlap <= 0f) return

        // Calculate separation vector
        val direction = transformA.position.cpy().sub(transformB.position).nor()

        // Move entities apart based on mass
        val totalMass = collisionA.mass + collisionB.mass
        val ratioA = collisionB.mass / totalMass
        val ratioB = collisionA.mass / totalMass

        if (!collisionA.isStatic) {
            transformA.position.add(direction.cpy().scl(overlap * ratioA))
        }
        if (!collisionB.isStatic) {
            transformB.position.sub(direction.cpy().scl(overlap * ratioB))
        }
    }
}
