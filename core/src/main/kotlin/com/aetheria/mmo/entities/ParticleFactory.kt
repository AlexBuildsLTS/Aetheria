package com.aetheria.mmo.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*

/**
 * Particle Factory
 * Creates particle effect entities
 */
object ParticleFactory {

    fun createExplosion(engine: PooledEngine, position: Vector3, scale: Float = 1f): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(scale, scale, scale)
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "explosion"
                isLooping = false
                autoRemove = true
                this.scale = scale
                maxLifetime = 2f
            })

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(1f, 0.5f, 0f, 1f)
                intensity = 3f
                radius = 10f * scale
            })

            engine.addEntity(this)
        }
    }

    fun createHealEffect(engine: PooledEngine, position: Vector3): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "heal_particles"
                isLooping = false
                autoRemove = true
                maxLifetime = 1.5f
            })

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(0f, 1f, 0f, 1f)
                intensity = 1.5f
                radius = 5f
            })

            engine.addEntity(this)
        }
    }

    fun createTeleportEffect(engine: PooledEngine, position: Vector3): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "teleport_swirl"
                isLooping = false
                autoRemove = true
                maxLifetime = 1f
            })

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(0.5f, 0f, 1f, 1f)
                intensity = 2f
                radius = 6f
                isFlickering = true
                flickerSpeed = 10f
            })

            engine.addEntity(this)
        }
    }

    fun createBloodSplatter(engine: PooledEngine, position: Vector3): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "blood_splatter"
                isLooping = false
                autoRemove = true
                maxLifetime = 0.5f
            })

            engine.addEntity(this)
        }
    }

    fun createMuzzleFlash(engine: PooledEngine, position: Vector3, direction: Vector3): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "muzzle_flash"
                isLooping = false
                autoRemove = true
                maxLifetime = 0.1f
            })

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(1f, 0.9f, 0.5f, 1f)
                intensity = 5f
                radius = 3f
            })

            engine.addEntity(this)
        }
    }
}
