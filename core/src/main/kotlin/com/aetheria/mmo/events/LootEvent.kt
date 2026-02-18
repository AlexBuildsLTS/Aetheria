package com.aetheria.mmo.events

import com.badlogic.ashley.core.Entity

/**
 * Loot Event
 * Fired when loot is generated or picked up
 */
data class LootEvent(
    val looter: Entity,
    val lootSource: Entity?,
    val itemId: String,
    val quantity: Int = 1,
    val rarity: LootRarity = LootRarity.COMMON
) : GameEvent

enum class LootRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY,
    MYTHIC
}
