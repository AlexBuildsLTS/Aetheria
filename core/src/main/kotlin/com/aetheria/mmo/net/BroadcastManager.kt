package com.aetheria.mmo.net

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Broadcast Manager
 * Manages broadcasting packets to multiple clients
 * Implements area-of-interest (AOI) filtering for efficient network usage
 */
class BroadcastManager {
    private val subscribers = ConcurrentHashMap<String, MutableSet<String>>()
    private val outgoingPackets = ConcurrentLinkedQueue<BroadcastPacket>()

    /**
     * Subscribe a player to a broadcast channel
     * Channels can be used for zones, parties, guilds, etc.
     */
    fun subscribe(playerId: String, channel: String) {
        subscribers.getOrPut(channel) { mutableSetOf() }.add(playerId)
    }

    /**
     * Unsubscribe a player from a channel
     */
    fun unsubscribe(playerId: String, channel: String) {
        subscribers[channel]?.remove(playerId)
    }

    /**
     * Unsubscribe a player from all channels
     */
    fun unsubscribeAll(playerId: String) {
        subscribers.values.forEach { it.remove(playerId) }
    }

    /**
     * Broadcast a packet to all subscribers of a channel
     */
    fun broadcast(channel: String, packet: Packet, excludePlayer: String? = null) {
        val recipients = subscribers[channel]?.filter { it != excludePlayer } ?: return

        recipients.forEach { playerId ->
            outgoingPackets.offer(BroadcastPacket(playerId, packet))
        }
    }

    /**
     * Broadcast to all players except one
     */
    fun broadcastToAll(packet: Packet, excludePlayer: String? = null) {
        val allPlayers = subscribers.values.flatten().toSet()

        allPlayers.filter { it != excludePlayer }.forEach { playerId ->
            outgoingPackets.offer(BroadcastPacket(playerId, packet))
        }
    }

    /**
     * Broadcast to specific players
     */
    fun broadcastToPlayers(playerIds: List<String>, packet: Packet) {
        playerIds.forEach { playerId ->
            outgoingPackets.offer(BroadcastPacket(playerId, packet))
        }
    }

    /**
     * Send packet to a single player
     */
    fun sendToPlayer(playerId: String, packet: Packet) {
        outgoingPackets.offer(BroadcastPacket(playerId, packet))
    }

    /**
     * Get all pending outgoing packets
     */
    fun getPendingPackets(): List<BroadcastPacket> {
        val packets = mutableListOf<BroadcastPacket>()
        while (outgoingPackets.isNotEmpty()) {
            outgoingPackets.poll()?.let { packets.add(it) }
        }
        return packets
    }

    /**
     * Get subscribers for a channel
     */
    fun getSubscribers(channel: String): Set<String> {
        return subscribers[channel]?.toSet() ?: emptySet()
    }

    /**
     * Get all channels a player is subscribed to
     */
    fun getPlayerChannels(playerId: String): List<String> {
        return subscribers.entries
            .filter { playerId in it.value }
            .map { it.key }
    }

    /**
     * Clear all subscriptions and pending packets
     */
    fun clear() {
        subscribers.clear()
        outgoingPackets.clear()
    }

    /**
     * Get statistics
     */
    fun getStats(): BroadcastStats {
        return BroadcastStats(
            channelCount = subscribers.size,
            totalSubscribers = subscribers.values.sumOf { it.size },
            pendingPackets = outgoingPackets.size
        )
    }
}

/**
 * Broadcast Packet
 * Represents a packet to be sent to a specific player
 */
data class BroadcastPacket(
    val recipientId: String,
    val packet: Packet,
    val priority: Priority = Priority.forPacket(packet),
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Broadcast Statistics
 */
data class BroadcastStats(
    val channelCount: Int,
    val totalSubscribers: Int,
    val pendingPackets: Int
)

/**
 * Area of Interest (AOI) Manager
 * Manages which players can see which entities based on distance
 */
class AOIManager(
    private val cellSize: Float = 50f // Size of each grid cell
) {
    private val grid = ConcurrentHashMap<GridCell, MutableSet<String>>()

    data class GridCell(val x: Int, val y: Int)

    /**
     * Update player position in the grid
     */
    fun updatePosition(playerId: String, x: Float, z: Float) {
        val cell = getCell(x, z)

        // Remove from old cells
        grid.values.forEach { it.remove(playerId) }

        // Add to new cell
        grid.getOrPut(cell) { mutableSetOf() }.add(playerId)
    }

    /**
     * Get nearby players within a certain radius
     */
    fun getNearbyPlayers(x: Float, z: Float, radius: Float): Set<String> {
        val cellRadius = (radius / cellSize).toInt() + 1
        val centerCell = getCell(x, z)
        val nearbyPlayers = mutableSetOf<String>()

        for (dx in -cellRadius..cellRadius) {
            for (dy in -cellRadius..cellRadius) {
                val cell = GridCell(centerCell.x + dx, centerCell.y + dy)
                grid[cell]?.let { nearbyPlayers.addAll(it) }
            }
        }

        return nearbyPlayers
    }

    /**
     * Remove player from grid
     */
    fun removePlayer(playerId: String) {
        grid.values.forEach { it.remove(playerId) }
    }

    /**
     * Clear the grid
     */
    fun clear() {
        grid.clear()
    }

    private fun getCell(x: Float, z: Float): GridCell {
        return GridCell(
            (x / cellSize).toInt(),
            (z / cellSize).toInt()
        )
    }
}
