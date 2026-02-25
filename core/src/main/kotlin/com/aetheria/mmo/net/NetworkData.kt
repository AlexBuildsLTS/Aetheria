package com.aetheria.mmo.net

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val username: String,
    @SerialName("character_class")
    val characterClass: CharacterClass = CharacterClass.Vanguard,
    val level: Int = 1,
    val xp: Int = 0,
    val stats: PlayerStats = PlayerStats()
)

@Serializable
enum class CharacterClass {
    @SerialName("Vanguard") Vanguard,
    @SerialName("Weaver") Weaver,
    @SerialName("Strider") Strider,
    @SerialName("Medic") Medic
}

@Serializable
data class PlayerStats(
    val hp: Float = 100f,
    @SerialName("max_hp")
    val maxHp: Float = 100f,
    val stamina: Float = 100f,
    val mana: Float = 100f,
    // Old fields for compatibility
    val strength: Int = 10,
    val agility: Int = 10,
    val intelligence: Int = 10,
    val vitality: Int = 10,
    val luck: Int = 10
)

// COMPATIBILITY CLASSES
@Serializable
data class PlayerData(
    val id: String,
    val username: String,
    val level: Int = 1,
    val experience: Int = 0,
    val health: Float = 100f,
    val maxHealth: Float = 100f,
    val position: Position = Position(),
    val inventory: List<InventoryItem> = emptyList(),
    val equipment: Equipment = Equipment(),
    val stats: PlayerStats = PlayerStats()
)

@Serializable
data class Position(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
)

@Serializable
data class InventoryItem(
    val itemId: String,
    val quantity: Int = 1,
    val slot: Int = -1,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class Equipment(
    val weapon: String? = null,
    val helmet: String? = null,
    val chest: String? = null,
    val legs: String? = null,
    val boots: String? = null,
    val accessory1: String? = null,
    val accessory2: String? = null
)
