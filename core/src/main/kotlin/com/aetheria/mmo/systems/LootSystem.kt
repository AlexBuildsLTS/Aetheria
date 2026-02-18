package com.aetheria.mmo.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*
import com.aetheria.mmo.entities.ItemFactory
import com.aetheria.mmo.events.*
import kotlin.random.Random

/**
 * Loot System
 * Handles loot drops from enemies
 */
class LootSystem(private val pooledEngine: PooledEngine) : EntitySystem() {

    init {
        // Subscribe to death events
        EventQueue.subscribe<DeathEvent> { event ->
            handleDeath(event)
        }
    }

    override fun update(deltaTime: Float) {
        // Event-driven system, no per-frame updates needed
    }

    private fun handleDeath(event: DeathEvent) {
        val entity = event.entity
        val transform = entity.getComponent(TransformComponent::class.java) ?: return

        // Determine loot based on entity type
        val health = entity.getComponent(HealthComponent::class.java)
        val isPlayer = entity.getComponent(PlayerComponent::class.java) != null

        if (isPlayer) {
            // Players don't drop loot (or drop equipped items)
            return
        }

        // Drop loot based on max health (rough enemy tier estimation)
        val maxHealth = health?.max ?: 100f
        dropLoot(transform.position, maxHealth)
    }

    private fun dropLoot(position: Vector3, enemyTier: Float) {
        val dropPosition = position.cpy().add(
            Random.nextFloat() * 2f - 1f,
            0.5f,
            Random.nextFloat() * 2f - 1f
        )

        // Drop gold
        val goldAmount = (enemyTier * 0.5f).toInt() + Random.nextInt(10)
        if (goldAmount > 0) {
            ItemFactory.createGoldCoin(pooledEngine, dropPosition, goldAmount)
        }

        // Chance to drop health potion
        if (Random.nextFloat() < 0.3f) {
            ItemFactory.createHealthPotion(pooledEngine, dropPosition.cpy().add(1f, 0f, 0f))
        }

        // Chance to drop rare material
        if (Random.nextFloat() < 0.1f) {
            ItemFactory.createAetherCrystal(pooledEngine, dropPosition.cpy().add(-1f, 0f, 0f))
        }
    }
}
