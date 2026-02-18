package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.aetheria.mmo.components.*
import com.aetheria.mmo.events.*

/**
 * Inventory System
 * Handles item pickup and inventory management
 */
class InventorySystem : IteratingSystem(
    Family.all(InventoryComponent::class.java, TransformComponent::class.java).get()
) {
    private val itemEntities = mutableListOf<Entity>()

    // Convert ItemRarity to LootRarity
    private fun convertRarity(rarity: ItemRarity): LootRarity {
        return when (rarity) {
            ItemRarity.COMMON -> LootRarity.COMMON
            ItemRarity.UNCOMMON -> LootRarity.UNCOMMON
            ItemRarity.RARE -> LootRarity.RARE
            ItemRarity.EPIC -> LootRarity.EPIC
            ItemRarity.LEGENDARY -> LootRarity.LEGENDARY
            ItemRarity.MYTHIC -> LootRarity.MYTHIC
        }
    }

    override fun update(deltaTime: Float) {
        // Collect all item entities
        itemEntities.clear()
        engine.getEntitiesFor(
            Family.all(ItemComponent::class.java, TransformComponent::class.java).get()
        ).forEach { itemEntities.add(it) }

        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val inventory = entity.getComponent(InventoryComponent::class.java)
        val transform = entity.getComponent(TransformComponent::class.java)
        val network = entity.getComponent(NetworkComponent::class.java)

        // Only process local player
        if (network != null && !network.isLocalPlayer) return

        // Check for nearby items
        itemEntities.forEach { itemEntity ->
            val itemComp = itemEntity.getComponent(ItemComponent::class.java)
            val itemTransform = itemEntity.getComponent(TransformComponent::class.java)

            if (itemComp == null || itemTransform == null) return@forEach

            // Update item lifetime
            itemComp.update(deltaTime)

            // Check if item should despawn
            if (itemComp.shouldDespawn()) {
                engine.removeEntity(itemEntity)
                return@forEach
            }

            // Check pickup distance
            val distance = transform.position.dst(itemTransform.position)
            if (distance <= itemComp.pickupRadius) {
                // Check if player can pick up
                val playerId = network?.networkId ?: "local"
                if (itemComp.canBePickedUpBy(playerId)) {
                    // Auto-pickup or manual pickup
                    if (itemComp.autoPickup || shouldPickup()) {
                        tryPickupItem(inventory, itemComp, itemEntity, playerId)
                    }
                }
            }
        }
    }

    private fun shouldPickup(): Boolean {
        // TODO: Check for pickup input (E key)
        return false
    }

    private fun tryPickupItem(
        inventory: InventoryComponent,
        item: ItemComponent,
        itemEntity: Entity,
        playerId: String
    ) {
        // Handle currency separately
        if (item.itemType == ItemType.CURRENCY) {
            inventory.gold += item.quantity
            engine.removeEntity(itemEntity)

            EventQueue.post(
                LootEvent(
                    looter = itemEntity,
                    lootSource = null,
                    itemId = item.itemId,
                    quantity = item.quantity
                )
            )
            return
        }

        // Try to add to inventory
        if (inventory.addItem(item.itemId, item.quantity)) {
            engine.removeEntity(itemEntity)

            EventQueue.post(
                LootEvent(
                    looter = itemEntity,
                    lootSource = null,
                    itemId = item.itemId,
                    quantity = item.quantity,
                    rarity = convertRarity(item.rarity)
                )
            )
        }
    }
}
