package com.aetheria.mmo.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*

/**
 * Item Factory
 * Creates consumable and material items
 */
object ItemFactory {

    fun createHealthPotion(engine: PooledEngine, position: Vector3, quantity: Int = 1): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(0.3f, 0.3f, 0.3f)
            })

            add(engine.createComponent(ItemComponent::class.java).apply {
                itemId = "health_potion"
                itemName = "Health Potion"
                itemType = ItemType.CONSUMABLE
                rarity = ItemRarity.COMMON
                this.quantity = quantity
                maxStack = 99
                isStackable = true
                pickupRadius = 2f
                glowColor = "red"
            })

            add(engine.createComponent(ModelComponent::class.java))

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(1f, 0f, 0f, 1f)
                intensity = 0.8f
                radius = 2f
            })

            add(engine.createComponent(CollisionComponent::class.java).apply {
                collisionShape = CollisionShape.SPHERE
                radius = 0.3f
                isTrigger = true
                collisionLayer = 4
            })

            engine.addEntity(this)
        }
    }

    fun createAetherCrystal(engine: PooledEngine, position: Vector3, quantity: Int = 1): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(0.4f, 0.4f, 0.4f)
            })

            add(engine.createComponent(ItemComponent::class.java).apply {
                itemId = "aether_crystal"
                itemName = "Aether Crystal"
                itemType = ItemType.MATERIAL
                rarity = ItemRarity.RARE
                this.quantity = quantity
                maxStack = 50
                isStackable = true
                pickupRadius = 2.5f
                glowColor = "cyan"
            })

            add(engine.createComponent(ModelComponent::class.java))

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(0f, 1f, 1f, 1f)
                intensity = 1.2f
                radius = 3f
                isFlickering = true
            })

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "crystal_sparkle"
                isLooping = true
            })

            add(engine.createComponent(CollisionComponent::class.java).apply {
                collisionShape = CollisionShape.SPHERE
                radius = 0.4f
                isTrigger = true
                collisionLayer = 4
            })

            engine.addEntity(this)
        }
    }

    fun createGoldCoin(engine: PooledEngine, position: Vector3, amount: Int = 1): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
                this.scale.set(0.2f, 0.2f, 0.2f)
            })

            add(engine.createComponent(ItemComponent::class.java).apply {
                itemId = "gold"
                itemName = "Gold"
                itemType = ItemType.CURRENCY
                rarity = ItemRarity.COMMON
                quantity = amount
                maxStack = 999999
                isStackable = true
                pickupRadius = 3f
                autoPickup = true
                glowColor = "gold"
            })

            add(engine.createComponent(ModelComponent::class.java))

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(1f, 0.84f, 0f, 1f)
                intensity = 1f
                radius = 2.5f
            })

            add(engine.createComponent(CollisionComponent::class.java).apply {
                collisionShape = CollisionShape.SPHERE
                radius = 0.2f
                isTrigger = true
                collisionLayer = 4
            })

            engine.addEntity(this)
        }
    }
}
