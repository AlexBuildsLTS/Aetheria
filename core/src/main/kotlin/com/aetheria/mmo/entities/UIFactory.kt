package com.aetheria.mmo.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*

/**
 * UI Factory
 * Creates UI-related entities (damage numbers, markers, etc.)
 */
object UIFactory {

    fun createDamageNumber(
        engine: PooledEngine,
        position: Vector3,
        damage: Float,
        isCritical: Boolean = false
    ): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
            })

            // Use a custom component for damage numbers if needed
            // For now, just mark it with a particle effect
            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = if (isCritical) "damage_critical" else "damage_normal"
                isLooping = false
                autoRemove = true
                maxLifetime = 1.5f
            })

            engine.addEntity(this)
        }
    }

    fun createWaypoint(engine: PooledEngine, position: Vector3, label: String): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
            })

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(1f, 1f, 0f, 1f)
                intensity = 2f
                radius = 5f
                isFlickering = true
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "waypoint_beam"
                isLooping = true
            })

            engine.addEntity(this)
        }
    }

    fun createAreaMarker(engine: PooledEngine, position: Vector3, radius: Float): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(radius, 1f, radius)
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "area_circle"
                isLooping = true
            })

            engine.addEntity(this)
        }
    }
}
