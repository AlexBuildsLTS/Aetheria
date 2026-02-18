package com.aetheria.mmo.net

import com.badlogic.gdx.math.Vector3
import kotlinx.serialization.Serializable

/**
 * Network Packet Definitions
 * Binary-serializable packets for efficient network transmission
 */

@Serializable
sealed class Packet {
    abstract val timestamp: Long

    // Player Movement
    @Serializable
    data class PlayerMove(
        val playerId: String,
        val x: Float,
        val y: Float,
        val z: Float,
        val rotX: Float = 0f,
        val rotY: Float = 0f,
        val rotZ: Float = 0f,
        val rotW: Float = 1f,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Packet()

    // Combat
    @Serializable
    data class Attack(
        val attackerId: String,
        val targetId: String?,
        val damage: Float,
        val damageType: String,
        val isCritical: Boolean = false,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Packet()

    // Skill/Ability
    @Serializable
    data class CastSkill(
        val casterId: String,
        val skillId: String,
        val targetId: String? = null,
        val targetX: Float? = null,
        val targetY: Float? = null,
        val targetZ: Float? = null,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Packet()

    // Entity Spawn
    @Serializable
    data class EntitySpawn(
        val entityId: String,
        val entityType: String,
        val x: Float,
        val y: Float,
        val z: Float,
        val ownerId: String? = null,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Packet()

    // Entity Despawn
    @Serializable
    data class EntityDespawn(
        val entityId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Packet()

    // Chat Message
    @Serializable
    data class ChatMessage(
        val senderId: String,
        val senderName: String,
        val message: String,
        val channel: String = "global",
        override val timestamp: Long = System.currentTimeMillis()
    ) : Packet()

    // Ping/Pong for latency
    @Serializable
    data class Ping(
        val clientTime: Long,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Packet()

    @Serializable
    data class Pong(
        val clientTime: Long,
        val serverTime: Long,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Packet()

    // World State Snapshot
    @Serializable
    data class WorldSnapshot(
        val entities: List<EntityState>,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Packet()

    // Player Join/Leave
    @Serializable
    data class PlayerJoin(
        val playerId: String,
        val username: String,
        val x: Float,
        val y: Float,
        val z: Float,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Packet()

    @Serializable
    data class PlayerLeave(
        val playerId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Packet()

    // Inventory
    @Serializable
    data class ItemPickup(
        val playerId: String,
        val itemId: String,
        val quantity: Int = 1,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Packet()

    @Serializable
    data class ItemDrop(
        val playerId: String,
        val itemId: String,
        val quantity: Int = 1,
        val x: Float,
        val y: Float,
        val z: Float,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Packet()
}

@Serializable
data class EntityState(
    val id: String,
    val type: String,
    val x: Float,
    val y: Float,
    val z: Float,
    val rotX: Float = 0f,
    val rotY: Float = 0f,
    val rotZ: Float = 0f,
    val rotW: Float = 1f,
    val health: Float? = null,
    val maxHealth: Float? = null,
    val state: String? = null
)
