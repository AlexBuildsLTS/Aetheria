package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

/**
 * Item Component
 * Represents a physical item in the world that can be picked up
 */
class ItemComponent : Component, Pool.Poolable {
    var itemId: String = ""
    var itemName: String = ""
    var itemType: ItemType = ItemType.CONSUMABLE
    var rarity: ItemRarity = ItemRarity.COMMON
    var quantity: Int = 1
    var maxStack: Int = 99
    var isStackable: Boolean = true

    // Pickup settings
    var canPickup: Boolean = true
    var pickupRadius: Float = 2f
    var autoPickup: Boolean = false
    var despawnTime: Float = 300f // 5 minutes
    var timeAlive: Float = 0f

    // Visual
    var isGlowing: Boolean = true
    var glowColor: String = "white"
    var rotationSpeed: Float = 45f // Degrees per second

    // Ownership (for loot protection)
    var ownerId: String? = null
    var ownershipDuration: Float = 30f // Seconds before anyone can pick up
    var ownershipTimeRemaining: Float = 0f

    fun update(deltaTime: Float) {
        timeAlive += deltaTime
        if (ownershipTimeRemaining > 0f) {
            ownershipTimeRemaining -= deltaTime
        }
    }

    fun canBePickedUpBy(playerId: String): Boolean {
        if (!canPickup) return false
        if (ownerId == null) return true
        if (ownerId == playerId) return true
        return ownershipTimeRemaining <= 0f
    }

    fun shouldDespawn(): Boolean = timeAlive >= despawnTime

    override fun reset() {
        itemId = ""
        itemName = ""
        itemType = ItemType.CONSUMABLE
        rarity = ItemRarity.COMMON
        quantity = 1
        maxStack = 99
        isStackable = true
        canPickup = true
        pickupRadius = 2f
        autoPickup = false
        despawnTime = 300f
        timeAlive = 0f
        isGlowing = true
        glowColor = "white"
        rotationSpeed = 45f
        ownerId = null
        ownershipDuration = 30f
        ownershipTimeRemaining = 0f
    }
}

enum class ItemType {
    WEAPON,
    ARMOR,
    CONSUMABLE,
    MATERIAL,
    QUEST,
    CURRENCY,
    COSMETIC
}

enum class ItemRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY,
    MYTHIC
}
