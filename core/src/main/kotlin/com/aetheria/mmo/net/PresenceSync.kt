package com.aetheria.mmo.net

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Presence Sync
 * Manages player online/offline status and activity tracking
 * Syncs with Supabase Realtime for social features
 */
class PresenceSync {
    private val presenceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val onlinePlayers = ConcurrentHashMap<String, PlayerPresence>()
    private val presenceListeners = mutableListOf<(PresenceEvent) -> Unit>()

    private var heartbeatJob: Job? = null
    private var localPlayerId: String? = null

    /**
     * Start presence tracking for local player
     */
    fun startTracking(playerId: String) {
        localPlayerId = playerId

        // Add self to online players
        updatePresence(playerId, PresenceStatus.ONLINE)

        // Start heartbeat to keep presence alive
        heartbeatJob = presenceScope.launch {
            while (isActive) {
                sendHeartbeat(playerId)
                delay(30000) // Every 30 seconds
            }
        }
    }

    /**
     * Stop presence tracking
     */
    fun stopTracking() {
        heartbeatJob?.cancel()
        localPlayerId?.let { playerId ->
            updatePresence(playerId, PresenceStatus.OFFLINE)
        }
    }

    /**
     * Update player presence status
     */
    fun updatePresence(playerId: String, status: PresenceStatus) {
        val presence = onlinePlayers.getOrPut(playerId) {
            PlayerPresence(playerId, status)
        }

        val oldStatus = presence.status
        presence.status = status
        presence.lastSeen = System.currentTimeMillis()

        // Notify listeners
        if (oldStatus != status) {
            notifyListeners(PresenceEvent.StatusChanged(playerId, oldStatus, status))
        }
    }

    /**
     * Update player activity
     */
    fun updateActivity(playerId: String, activity: String) {
        onlinePlayers[playerId]?.let { presence ->
            presence.currentActivity = activity
            presence.lastSeen = System.currentTimeMillis()

            notifyListeners(PresenceEvent.ActivityChanged(playerId, activity))
        }
    }

    /**
     * Get player presence
     */
    fun getPresence(playerId: String): PlayerPresence? {
        return onlinePlayers[playerId]
    }

    /**
     * Get all online players
     */
    fun getOnlinePlayers(): List<PlayerPresence> {
        return onlinePlayers.values
            .filter { it.status == PresenceStatus.ONLINE }
            .toList()
    }

    /**
     * Get friends online count
     */
    fun getOnlineCount(): Int {
        return onlinePlayers.values.count { it.status == PresenceStatus.ONLINE }
    }

    /**
     * Subscribe to presence events
     */
    fun subscribe(listener: (PresenceEvent) -> Unit) {
        presenceListeners.add(listener)
    }

    /**
     * Unsubscribe from presence events
     */
    fun unsubscribe(listener: (PresenceEvent) -> Unit) {
        presenceListeners.remove(listener)
    }

    /**
     * Send heartbeat to keep presence alive
     */
    private suspend fun sendHeartbeat(playerId: String) {
        withContext(Dispatchers.IO) {
            try {
                // TODO: Send heartbeat to Supabase
                // SupabaseClient.client.postgrest["player_presence"].update(...)

                onlinePlayers[playerId]?.lastSeen = System.currentTimeMillis()
            } catch (e: Exception) {
                println("Heartbeat error: ${e.message}")
            }
        }
    }

    /**
     * Notify all listeners
     */
    private fun notifyListeners(event: PresenceEvent) {
        presenceListeners.forEach { listener ->
            try {
                listener(event)
            } catch (e: Exception) {
                println("Presence listener error: ${e.message}")
            }
        }
    }

    /**
     * Clean up stale presences (offline for > 5 minutes)
     */
    fun cleanupStale() {
        val now = System.currentTimeMillis()
        val staleThreshold = 5 * 60 * 1000L // 5 minutes

        onlinePlayers.entries.removeIf { (playerId, presence) ->
            val isStale = (now - presence.lastSeen) > staleThreshold
            if (isStale) {
                notifyListeners(PresenceEvent.PlayerLeft(playerId))
            }
            isStale
        }
    }

    /**
     * Dispose resources
     */
    fun dispose() {
        stopTracking()
        presenceScope.cancel()
        onlinePlayers.clear()
        presenceListeners.clear()
    }
}

/**
 * Player Presence
 */
data class PlayerPresence(
    val playerId: String,
    var status: PresenceStatus,
    var lastSeen: Long = System.currentTimeMillis(),
    var currentActivity: String = "In Game"
)

/**
 * Presence Status
 */
enum class PresenceStatus {
    ONLINE,
    AWAY,
    BUSY,
    OFFLINE
}

/**
 * Presence Events
 */
sealed class PresenceEvent {
    data class StatusChanged(
        val playerId: String,
        val oldStatus: PresenceStatus,
        val newStatus: PresenceStatus
    ) : PresenceEvent()

    data class ActivityChanged(
        val playerId: String,
        val activity: String
    ) : PresenceEvent()

    data class PlayerJoined(val playerId: String) : PresenceEvent()
    data class PlayerLeft(val playerId: String) : PresenceEvent()
}
