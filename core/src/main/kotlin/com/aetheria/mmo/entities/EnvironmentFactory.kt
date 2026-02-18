package com.aetheria.mmo.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*

/**
 * Environment Factory
 * Creates environmental objects (lights, props, hazards)
 */
object EnvironmentFactory {

    fun createStreetLight(engine: PooledEngine, position: Vector3): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(1f, 3f, 1f)
            })

            add(engine.createComponent(ModelComponent::class.java))

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(1f, 0.9f, 0.7f, 1f)
                intensity = 2f
                radius = 15f
                castsShadows = true
                lightType = LightType.POINT
            })

            add(engine.createComponent(CollisionComponent::class.java).apply {
                collisionShape = CollisionShape.CAPSULE
                radius = 0.3f
                height = 3f
                isStatic = true
                collisionLayer = 8
            })

            engine.addEntity(this)
        }
    }

    fun createCrystalFormation(engine: PooledEngine, position: Vector3): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(2f, 2f, 2f)
            })

            add(engine.createComponent(ModelComponent::class.java))

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(Color.CYAN)
                intensity = 1.5f
                radius = 10f
                isFlickering = true
                flickerSpeed = 2f
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "crystal_glow"
                isLooping = true
            })

            add(engine.createComponent(CollisionComponent::class.java).apply {
                collisionShape = CollisionShape.BOX
                width = 2f
                height = 2f
                depth = 2f
                isStatic = true
                collisionLayer = 8
            })

            engine.addEntity(this)
        }
    }

    fun createHazardZone(engine: PooledEngine, position: Vector3, radius: Float, damagePerSecond: Float): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(radius, 1f, radius)
            })

            add(engine.createComponent(CollisionComponent::class.java).apply {
                collisionShape = CollisionShape.SPHERE
                this.radius = radius
                isTrigger = true
                isStatic = true
                collisionLayer = 16
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "hazard_zone"
                isLooping = true
            })

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(1f, 0f, 0f, 1f)
                intensity = 1f
                this.radius = radius
                isFlickering = true
            })

            engine.addEntity(this)
        }
    }

    fun createTeleporter(engine: PooledEngine, position: Vector3, destinationId: String): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
            })

            add(engine.createComponent(ModelComponent::class.java))

            add(engine.createComponent(CollisionComponent::class.java).apply {
                collisionShape = CollisionShape.CAPSULE
                radius = 2f
                height = 0.5f
                isTrigger = true
                isStatic = true
                collisionLayer = 32
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "teleporter_active"
                isLooping = true
            })

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(0.5f, 0f, 1f, 1f)
                intensity = 2f
                radius = 8f
                isFlickering = true
                flickerSpeed = 5f
            })

            engine.addEntity(this)
        }
    }
}
