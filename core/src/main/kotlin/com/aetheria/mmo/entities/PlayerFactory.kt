package com.aetheria.mmo.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*

/**
 * Player Factory
 * Creates player entities with all necessary components
 */
object PlayerFactory {

    fun createPlayer(engine: PooledEngine, playerId: String, username: String, position: Vector3): Entity {
        return engine.createEntity().apply {
            // Transform
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
            })

            // Player marker
            add(engine.createComponent(PlayerComponent::class.java))

            // Health & Shield
            add(engine.createComponent(HealthComponent::class.java).apply {
                max = 100f
                current = max
                regen = 5f
            })

            add(engine.createComponent(ShieldComponent::class.java).apply {
                max = 50f
                current = max
                regenRate = 10f
                regenDelay = 3f
            })

            add(engine.createComponent(StaminaComponent::class.java).apply {
                max = 100f
                current = max
                regen = 20f
            })

            // Movement
            add(engine.createComponent(VelocityComponent::class.java))
            add(engine.createComponent(MoveEvtComponent::class.java).apply {
                moveSpeed = 5f
                sprintMultiplier = 1.5f
            })

            // Combat
            add(engine.createComponent(CombatComponent::class.java).apply {
                attackPower = 10f
                attackSpeed = 1f
                critChance = 0.1f
                critDamage = 2f
            })

            // Inventory
            add(engine.createComponent(InventoryComponent::class.java).apply {
                maxSlots = 30
                gold = 0
            })

            // Input
            add(engine.createComponent(InputComponent::class.java))

            // Camera
            add(engine.createComponent(CameraFollowComponent::class.java).apply {
                offset.set(0f, 5f, -10f)
                followSpeed = 5f
            })

            // Collision
            add(engine.createComponent(CollisionComponent::class.java).apply {
                collisionShape = CollisionShape.CAPSULE
                radius = 0.5f
                height = 2f
                collisionLayer = 1
            })

            // Network
            add(engine.createComponent(NetworkComponent::class.java).apply {
                networkId = playerId
                ownerId = playerId
                isLocalPlayer = true
                hasAuthority = true
            })

            // Visual
            add(engine.createComponent(ModelComponent::class.java))
            add(engine.createComponent(AnimationComponent::class.java))
            add(engine.createComponent(StateComponent::class.java))

            engine.addEntity(this)
        }
    }

    fun createRemotePlayer(engine: PooledEngine, playerId: String, username: String, position: Vector3): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
            })

            add(engine.createComponent(PlayerComponent::class.java))

            add(engine.createComponent(HealthComponent::class.java).apply {
                max = 100f
                current = max
            })

            add(engine.createComponent(VelocityComponent::class.java))

            add(engine.createComponent(NetworkComponent::class.java).apply {
                networkId = playerId
                ownerId = playerId
                isLocalPlayer = false
                hasAuthority = false
            })

            add(engine.createComponent(ModelComponent::class.java))
            add(engine.createComponent(AnimationComponent::class.java))
            add(engine.createComponent(StateComponent::class.java))

            engine.addEntity(this)
        }
    }
}
