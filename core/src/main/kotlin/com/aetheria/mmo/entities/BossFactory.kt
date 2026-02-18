package com.aetheria.mmo.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*

/**
 * Boss Factory
 * Creates boss entities with enhanced stats and abilities
 */
object BossFactory {

    fun createVoidTitan(engine: PooledEngine, position: Vector3): Entity {
        return engine.createEntity().apply {
            // Transform
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(3f, 3f, 3f) // Larger than normal enemies
            })

            // Health - Boss has massive HP
            add(engine.createComponent(HealthComponent::class.java).apply {
                max = 10000f
                current = max
                regen = 50f
            })

            // Shield - Regenerating shield
            add(engine.createComponent(ShieldComponent::class.java).apply {
                max = 5000f
                current = max
                regenRate = 100f
                regenDelay = 5f
            })

            // Combat
            add(engine.createComponent(CombatComponent::class.java).apply {
                attackPower = 150f
            })

            // Movement
            add(engine.createComponent(VelocityComponent::class.java))
            add(engine.createComponent(MoveEvtComponent::class.java).apply {
                moveSpeed = 3f
            })

            // AI Steering
            add(engine.createComponent(SteeringComponent::class.java).apply {
                maxLinearSpeed = 3f
            })

            // Visual effects
            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(Color.PURPLE)
                intensity = 2f
                radius = 20f
                isFlickering = true
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "void_aura"
                isLooping = true
            })

            // Network sync
            add(engine.createComponent(NetworkComponent::class.java).apply {
                isNetworked = true
            })

            // Model
            add(engine.createComponent(ModelComponent::class.java))

            // Animation
            add(engine.createComponent(AnimationComponent::class.java))
            add(engine.createComponent(StateComponent::class.java))

            engine.addEntity(this)
        }
    }

    fun createAetherLord(engine: PooledEngine, position: Vector3): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(2.5f, 2.5f, 2.5f)
            })

            add(engine.createComponent(HealthComponent::class.java).apply {
                max = 8000f
                current = max
                regen = 40f
            })

            add(engine.createComponent(ShieldComponent::class.java).apply {
                max = 4000f
                current = max
                regenRate = 80f
            })

            add(engine.createComponent(CombatComponent::class.java).apply {
                attackPower = 120f
            })

            add(engine.createComponent(VelocityComponent::class.java))
            add(engine.createComponent(MoveEvtComponent::class.java).apply {
                moveSpeed = 4f
            })

            add(engine.createComponent(SteeringComponent::class.java))

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(Color.CYAN)
                intensity = 1.5f
                radius = 15f
            })

            add(engine.createComponent(NetworkComponent::class.java).apply {
                isNetworked = true
            })

            add(engine.createComponent(ModelComponent::class.java))

            add(engine.createComponent(AnimationComponent::class.java))
            add(engine.createComponent(StateComponent::class.java))

            engine.addEntity(this)
        }
    }

    fun createTemporalWarden(engine: PooledEngine, position: Vector3): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(2f, 2f, 2f)
            })

            add(engine.createComponent(HealthComponent::class.java).apply {
                max = 7000f
                current = max
                regen = 35f
            })

            add(engine.createComponent(ShieldComponent::class.java).apply {
                max = 3500f
                current = max
            })

            // Time manipulation boss
            add(engine.createComponent(TimeDebtComponent::class.java).apply {
                maxDebt = 30f
                repaymentRate = 2f
            })

            add(engine.createComponent(CombatComponent::class.java).apply {
                attackPower = 100f
            })

            add(engine.createComponent(VelocityComponent::class.java))
            add(engine.createComponent(MoveEvtComponent::class.java).apply {
                moveSpeed = 5f
            })

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(Color.GOLD)
                intensity = 1.8f
                radius = 18f
                isFlickering = true
                flickerSpeed = 10f
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "temporal_distortion"
                isLooping = true
            })

            add(engine.createComponent(NetworkComponent::class.java).apply {
                isNetworked = true
            })

            add(engine.createComponent(ModelComponent::class.java))

            add(engine.createComponent(AnimationComponent::class.java))
            add(engine.createComponent(StateComponent::class.java))

            engine.addEntity(this)
        }
    }
}
