package com.aetheria.mmo.events

/**
 * Network Event
 * Fired for network-related events (connection, disconnection, sync)
 */
sealed class NetworkEvent : GameEvent {
    data class Connected(val sessionId: String) : NetworkEvent()
    data class Disconnected(val reason: String) : NetworkEvent()
    data class PlayerJoined(val playerId: String, val username: String) : NetworkEvent()
    data class PlayerLeft(val playerId: String) : NetworkEvent()
    data class SyncReceived(val data: ByteArray) : NetworkEvent() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as SyncReceived
            return data.contentEquals(other.data)
        }

        override fun hashCode(): Int {
            return data.contentHashCode()
        }
    }
    data class LatencyUpdate(val pingMs: Int) : NetworkEvent()
    data class Error(val message: String, val code: Int = -1) : NetworkEvent()
}
