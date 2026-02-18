package com.aetheria.mmo.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*

/**
 * Weapon Factory
 * Creates weapon entities with stats and effects
 */
object WeaponFactory {

    fun createPlasmaSword(engine: PooledEngine, position: Vector3): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
            })

            add(engine.createComponent(WeaponComponent::class.java).apply {
                weaponId = "plasma_sword"
                damage = 50f
                attackSpeed = 1.5f
                range = 2.5f
                critChance = 0.15f
                critMultiplier = 2.5f
                weaponType = WeaponType.SWORD
                durability = 100f
                maxDurability = 100f
            })

            add(engine.createComponent(ItemComponent::class.java).apply {
                itemId = "plasma_sword"
                itemName = "Plasma Sword"
                itemType = ItemType.WEAPON
                rarity = ItemRarity.RARE
                isStackable = false
            })

            add(engine.createComponent(ModelComponent::class.java))

            add(engine.createComponent(LightComponent::class.java).apply {
                color.set(0f, 0.8f, 1f, 1f) // Cyan glow
                intensity = 1.2f
                radius = 3f
            })

            engine.addEntity(this)
        }
    }

    fun createVoidRifle(engine: PooledEngine, position: Vector3): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
            })

            add(engine.createComponent(WeaponComponent::class.java).apply {
                weaponId = "void_rifle"
                damage = 35f
                attackSpeed = 3f
                range = 50f
                critChance = 0.2f
                critMultiplier = 3f
                weaponType = WeaponType.RIFLE
                durability = 100f
                maxDurability = 100f
            })

            add(engine.createComponent(HeatComponent::class.java).apply {
                maxHeat = 100f
                heatPerShot = 15f
                cooldownRate = 25f
            })

            add(engine.createComponent(ItemComponent::class.java).apply {
                itemId = "void_rifle"
                itemName = "Void Rifle"
                itemType = ItemType.WEAPON
                rarity = ItemRarity.EPIC
                isStackable = false
            })

            add(engine.createComponent(ModelComponent::class.java))

            engine.addEntity(this)
        }
    }

    fun createAetherStaff(engine: PooledEngine, position: Vector3): Entity {
        return engine.createEntity().apply {
            add(engine.createComponent(TransformComponent::class.java).apply {
                this.position.set(position)
            })

            add(engine.createComponent(WeaponComponent::class.java).apply {
                weaponId = "aether_staff"
                damage = 60f
                attackSpeed = 0.8f
                range = 30f
                critChance = 0.25f
                critMultiplier = 2f
                weaponType = WeaponType.STAFF
                durability = 100f
                maxDurability = 100f
            })

            add(engine.createComponent(ItemComponent::class.java).apply {
                itemId = "aether_staff"
                itemName = "Aether Staff"
                itemType = ItemType.WEAPON
                rarity = ItemRarity.LEGENDARY
                isStackable = false
            })

            add(engine.createComponent(ModelComponent::class.java))

            add(engine.createComponent(ParticleComponent::class.java).apply {
                effectName = "aether_particles"
                isLooping = true
            })

            engine.addEntity(this)
        }
    }
}
