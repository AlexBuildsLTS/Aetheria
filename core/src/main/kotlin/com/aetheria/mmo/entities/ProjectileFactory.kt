package com.aetheria.mmo.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*

/**
 * Projectile Factory
 * Creates projectile entities for ranged combat
 */
object ProjectileFactory {

    fun createPlasmaBolt(
        engine: PooledEngine,
        position: Vector3,
        direction: Vector3,
        owner: Entity,
        damage: Float = 25f
    ): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(0.2f, 0.2f, 0.5f)
            })

            add(engine.createComponent(VelocityComponent::class.java).apply {
                linear.set(direction).nor().scl(30f) // 30 units/sec
            })

            add(engine.createComponent(CollisionComponent::class.java).apply {
                collisionShape = CollisionShape.SPHERE
                radius = 0.2f
                isTrigger = true
                collisionLayer = 64
            })

            add(engine.createComponent(ModelComponent::class.java))

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(0f, 0.8f, 1f, 1f)
                intensity = 1.5f
                radius = 3f
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "plasma_trail"
                isLooping = true
            })

            // Lifetime component (reuse TimeDebtComponent for simplicity)
            add(engine.createComponent(TimeDebtComponent::class.java).apply {
                maxDebt = 5f // 5 second lifetime
                debtAmount = 0f
            })

            engine.addEntity(this)
        }
    }

    fun createVoidMissile(
        engine: PooledEngine,
        position: Vector3,
        direction: Vector3,
        owner: Entity,
        damage: Float = 50f
    ): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(0.3f, 0.3f, 0.8f)
            })

            add(engine.createComponent(VelocityComponent::class.java).apply {
                linear.set(direction).nor().scl(40f)
            })

            add(engine.createComponent(CollisionComponent::class.java).apply {
                collisionShape = CollisionShape.SPHERE
                radius = 0.3f
                isTrigger = true
                collisionLayer = 64
            })

            add(engine.createComponent(ModelComponent::class.java))

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(Color.PURPLE)
                intensity = 2f
                radius = 4f
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "void_trail"
                isLooping = true
            })

            add(engine.createComponent(TimeDebtComponent::class.java).apply {
                maxDebt = 10f
            })

            engine.addEntity(this)
        }
    }

    fun createFireball(
        engine: PooledEngine,
        position: Vector3,
        direction: Vector3,
        owner: Entity,
        damage: Float = 40f
    ): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(0.5f, 0.5f, 0.5f)
            })

            add(engine.createComponent(VelocityComponent::class.java).apply {
                linear.set(direction).nor().scl(25f)
            })

            add(engine.createComponent(CollisionComponent::class.java).apply {
                collisionShape = CollisionShape.SPHERE
                radius = 0.5f
                isTrigger = true
                collisionLayer = 64
            })

            add(engine.createComponent(ModelComponent::class.java))

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(1f, 0.5f, 0f, 1f)
                intensity = 2.5f
                radius = 5f
                isFlickering = true
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "fire_trail"
                isLooping = true
            })

            add(engine.createComponent(TimeDebtComponent::class.java).apply {
                maxDebt = 8f
            })

            engine.addEntity(this)
        }
    }

    fun createLightningBolt(
        engine: PooledEngine,
        position: Vector3,
        direction: Vector3,
        owner: Entity,
        damage: Float = 60f
    ): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(0.1f, 0.1f, 2f)
            })

            add(engine.createComponent(VelocityComponent::class.java).apply {
                linear.set(direction).nor().scl(60f) // Very fast
            })

            add(engine.createComponent(CollisionComponent::class.java).apply {
                collisionShape = CollisionShape.SPHERE
                radius = 0.1f
                isTrigger = true
                collisionLayer = 64
            })

            add(engine.createComponent(ModelComponent::class.java))

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(0.5f, 0.5f, 1f, 1f)
                intensity = 3f
                radius = 6f
                isFlickering = true
                flickerSpeed = 20f
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "lightning_arc"
                isLooping = true
            })

            add(engine.createComponent(TimeDebtComponent::class.java).apply {
                maxDebt = 3f // Short lifetime
            })

            engine.addEntity(this)
        }
    }
}
