package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Matrix4
import com.aetheria.mmo.components.*

/**
 * Particle System
 * Updates and manages particle effects
 */
class ParticleSystem : IteratingSystem(
    Family.all(ParticleComponent::class.java, TransformComponent::class.java).get()
) {
    private val transformMatrix = Matrix4()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val particle = entity.getComponent(ParticleComponent::class.java)
        val transform = entity.getComponent(TransformComponent::class.java)

        // Update particle effect
        particle.effect?.let { effect ->
            // Build transform matrix from position, rotation, and scale
            transformMatrix.idt()
            transformMatrix.translate(transform.position)
            transformMatrix.rotate(transform.rotation)
            transformMatrix.scale(particle.scale, particle.scale, particle.scale)

            effect.setTransform(transformMatrix)
            effect.update(deltaTime)

            // Update lifetime
            particle.timeAlive += deltaTime

            // Check if effect is complete
            if (!particle.isLooping && effect.isComplete) {
                if (particle.autoRemove) {
                    engine.removeEntity(entity)
                }
            }

            // Check max lifetime
            if (particle.maxLifetime > 0f && particle.timeAlive >= particle.maxLifetime) {
                if (particle.autoRemove) {
                    engine.removeEntity(entity)
                }
            }
        }
    }
}
