package com.aetheria.mmo.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*
import com.aetheria.mmo.managers.ResourceManager
import com.badlogic.gdx.graphics.g3d.ModelInstance

/**
 * Enemy Factory
 * Creates various enemy types using procedural primitives.
 */
object EnemyFactory {

    fun createVoidDrone(engine: PooledEngine, position: Vector3): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(0.8f, 0.8f, 0.8f)
            })

            add(engine.createComponent(HealthComponent::class.java).apply {
                max = 50f
                current = max
                regen = 1f
            })

            add(engine.createComponent(CombatComponent::class.java).apply {
                attackPower = 15f
                attackSpeed = 1.2f
            })

            add(engine.createComponent(VelocityComponent::class.java))
            add(engine.createComponent(MoveEvtComponent::class.java).apply {
                moveSpeed = 3f
            })

            add(engine.createComponent(SteeringComponent::class.java).apply {
                steeringBehavior = SteeringBehaviorType.SEEK
                maxLinearSpeed = 3f
            })

            add(engine.createComponent(CollisionComponent::class.java).apply {
                collisionShape = CollisionShape.SPHERE
                radius = 0.8f
                collisionLayer = 2
            })

            add(engine.createComponent(NetworkComponent::class.java).apply {
                isNetworked = true
            })

            add(engine.createComponent(ModelComponent::class.java).apply {
                // Procedural Drone (Red Sphere)
                val model = ResourceManager.createPlaceholderModel(Color.RED, 1f, 1f)
                modelInstance = ModelInstance(model)
            })
            add(engine.createComponent(AnimationComponent::class.java))
            add(engine.createComponent(StateComponent::class.java))

            engine.addEntity(this)
        }
    }

    fun createAetherGuardian(engine: PooledEngine, position: Vector3): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(1.5f, 1.5f, 1.5f)
            })

            add(engine.createComponent(HealthComponent::class.java).apply {
                max = 200f
                current = max
                regen = 5f
            })

            add(engine.createComponent(ShieldComponent::class.java).apply {
                max = 100f
                current = max
                regenRate = 15f
            })

            add(engine.createComponent(CombatComponent::class.java).apply {
                attackPower = 30f
                attackSpeed = 0.8f
            })

            add(engine.createComponent(VelocityComponent::class.java))
            add(engine.createComponent(MoveEvtComponent::class.java).apply {
                moveSpeed = 2.5f
            })

            add(engine.createComponent(SteeringComponent::class.java).apply {
                steeringBehavior = SteeringBehaviorType.PURSUE
                maxLinearSpeed = 2.5f
            })

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(Color.CYAN)
                intensity = 1.5f
                radius = 8f
            })

            add(engine.createComponent(CollisionComponent::class.java).apply {
                collisionShape = CollisionShape.CAPSULE
                radius = 1f
                height = 3f
                collisionLayer = 2
            })

            add(engine.createComponent(NetworkComponent::class.java).apply {
                isNetworked = true
            })

            add(engine.createComponent(ModelComponent::class.java).apply {
                // Procedural Guardian (Blue Capsule)
                val model = ResourceManager.createPlaceholderModel(Color.BLUE, 1.5f, 3f)
                modelInstance = ModelInstance(model)
            })
            add(engine.createComponent(AnimationComponent::class.java))
            add(engine.createComponent(StateComponent::class.java))

            engine.addEntity(this)
        }
    }

    fun createTemporalStalker(engine: PooledEngine, position: Vector3): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
            })

            add(engine.createComponent(HealthComponent::class.java).apply {
                max = 120f
                current = max
            })

            add(engine.createComponent(TimeDebtComponent::class.java).apply {
                maxDebt = 15f
                repaymentRate = 2f
            })

            add(engine.createComponent(CombatComponent::class.java).apply {
                attackPower = 25f
                attackSpeed = 1.5f
            })

            add(engine.createComponent(VelocityComponent::class.java))
            add(engine.createComponent(MoveEvtComponent::class.java).apply {
                moveSpeed = 4f
            })

            add(engine.createComponent(SteeringComponent::class.java).apply {
                steeringBehavior = SteeringBehaviorType.EVADE
                maxLinearSpeed = 4f
            })

            add(engine.createComponent(CollisionComponent::class.java).apply {
                collisionShape = CollisionShape.SPHERE
                radius = 0.7f
                collisionLayer = 2
            })

            add(engine.createComponent(NetworkComponent::class.java).apply {
                isNetworked = true
            })

            add(engine.createComponent(ModelComponent::class.java).apply {
                // Procedural Stalker (Purple Capsule)
                val model = ResourceManager.createPlaceholderModel(Color.PURPLE, 0.8f, 2f)
                modelInstance = ModelInstance(model)
            })
            add(engine.createComponent(AnimationComponent::class.java))
            add(engine.createComponent(StateComponent::class.java))

            engine.addEntity(this)
        }
    }
}
