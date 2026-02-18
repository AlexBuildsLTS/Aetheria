package com.aetheria.mmo.net

/**
 * Network Channel Definitions
 * Defines different communication channels with varying reliability/ordering
 */
enum class Channel(val id: Int, val reliable: Boolean, val ordered: Boolean) {
    // Reliable, ordered - for critical game state
    RELIABLE_ORDERED(0, reliable = true, ordered = true),

    // Reliable, unordered - for events that must arrive but order doesn't matter
    RELIABLE_UNORDERED(1, reliable = true, ordered = false),

    // Unreliable, ordered - for frequent updates where latest is most important
    UNRELIABLE_ORDERED(2, reliable = false, ordered = true),

    // Unreliable, unordered - for high-frequency data like position updates
    UNRELIABLE_UNORDERED(3, reliable = false, ordered = false);

    companion object {
        // Channel assignments for different packet types
        fun forPacket(packet: Packet): Channel {
            return when (packet) {
                // Movement - unreliable, latest position is what matters
                is Packet.PlayerMove -> UNRELIABLE_ORDERED

                // Combat - must be reliable
                is Packet.Attack -> RELIABLE_ORDERED
                is Packet.CastSkill -> RELIABLE_ORDERED

                // Entity lifecycle - must be reliable
                is Packet.EntitySpawn -> RELIABLE_ORDERED
                is Packet.EntityDespawn -> RELIABLE_ORDERED

                // Chat - must be reliable and ordered
                is Packet.ChatMessage -> RELIABLE_ORDERED

                // Latency - unreliable is fine
                is Packet.Ping -> UNRELIABLE_UNORDERED
                is Packet.Pong -> UNRELIABLE_UNORDERED

                // World state - unreliable, latest snapshot is what matters
                is Packet.WorldSnapshot -> UNRELIABLE_ORDERED

                // Player join/leave - reliable
                is Packet.PlayerJoin -> RELIABLE_ORDERED
                is Packet.PlayerLeave -> RELIABLE_ORDERED

                // Inventory - must be reliable
                is Packet.ItemPickup -> RELIABLE_ORDERED
                is Packet.ItemDrop -> RELIABLE_ORDERED
            }
        }
    }
}

/**
 * Channel Priority
 * Higher priority packets are sent first
 */
enum class Priority(val value: Int) {
    LOW(0),
    NORMAL(1),
    HIGH(2),
    CRITICAL(3);

    companion object {
        fun forPacket(packet: Packet): Priority {
            return when (packet) {
                is Packet.Attack, is Packet.CastSkill -> HIGH
                is Packet.EntitySpawn, is Packet.EntityDespawn -> HIGH
                is Packet.PlayerJoin, is Packet.PlayerLeave -> NORMAL
                is Packet.ChatMessage -> NORMAL
                is Packet.PlayerMove -> LOW
                is Packet.WorldSnapshot -> LOW
                is Packet.Ping, is Packet.Pong -> CRITICAL
                is Packet.ItemPickup, is Packet.ItemDrop -> HIGH
            }
        }
    }
}
