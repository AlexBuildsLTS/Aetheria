package com.aetheria.mmo.net

import kotlinx.serialization.Serializable

/**
 * Network Data Transfer Objects
 * Serializable data structures for network communication
 */

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
data class Rotation(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val w: Float = 1f
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

@Serializable
data class PlayerStats(
    val strength: Int = 10,
    val agility: Int = 10,
    val intelligence: Int = 10,
    val vitality: Int = 10,
    val luck: Int = 10
)

@Serializable
data class SessionInfo(
    val sessionId: String,
    val playerId: String,
    val serverRegion: String,
    val serverTime: Long,
    val maxPlayers: Int = 100,
    val currentPlayers: Int = 0
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val clientVersion: String = "1.0.0"
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val token: String? = null,
    val playerId: String? = null,
    val message: String? = null,
    val playerData: PlayerData? = null
)

@Serializable
data class ServerInfo(
    val serverId: String,
    val name: String,
    val region: String,
    val playerCount: Int,
    val maxPlayers: Int,
    val status: String, // "online", "full", "maintenance"
    val ping: Int = -1
)
