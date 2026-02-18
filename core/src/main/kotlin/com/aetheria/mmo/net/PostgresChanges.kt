package com.aetheria.mmo.net

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

/**
 * Postgres Changes Listener
 * Listens to real-time database changes via Supabase Realtime
 * Used for chat, social features, and live data updates
 */
class PostgresChanges {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val changeScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val subscriptions = ConcurrentHashMap<String, ChangeSubscription>()

    /**
     * Subscribe to table changes
     */
    fun subscribeToTable(
        table: String,
        event: ChangeEvent = ChangeEvent.ALL,
        filter: Map<String, String>? = null,
        callback: (ChangePayload) -> Unit
    ): String {
        val subscriptionId = generateSubscriptionId()

        val subscription = ChangeSubscription(
            id = subscriptionId,
            table = table,
            event = event,
            filter = filter,
            callback = callback
        )

        subscriptions[subscriptionId] = subscription

        // Start listening to changes
        changeScope.launch {
            listenToChanges(subscription)
        }

        return subscriptionId
    }

    /**
     * Subscribe to chat messages
     */
    fun subscribeToChatMessages(
        channel: String,
        callback: (ChatMessage) -> Unit
    ): String {
        return subscribeToTable(
            table = "chat_messages",
            event = ChangeEvent.INSERT,
            filter = mapOf("channel" to channel)
        ) { payload ->
            try {
                val message = json.decodeFromString<ChatMessage>(payload.new)
                callback(message)
            } catch (e: Exception) {
                println("Error parsing chat message: ${e.message}")
            }
        }
    }

    /**
     * Subscribe to player presence (online/offline)
     */
    fun subscribeToPresence(
        callback: (PresenceUpdate) -> Unit
    ): String {
        return subscribeToTable(
            table = "player_presence",
            event = ChangeEvent.ALL
        ) { payload ->
            try {
                val presence = when (payload.eventType) {
                    "INSERT", "UPDATE" -> {
                        val data = json.decodeFromString<PresenceData>(payload.new)
                        PresenceUpdate(data.playerId, data.status, data.lastSeen)
                    }
                    "DELETE" -> {
                        val data = json.decodeFromString<PresenceData>(payload.old)
                        PresenceUpdate(data.playerId, "offline", System.currentTimeMillis())
                    }
                    else -> return@subscribeToTable
                }
                callback(presence)
            } catch (e: Exception) {
                println("Error parsing presence update: ${e.message}")
            }
        }
    }

    /**
     * Subscribe to guild/party updates
     */
    fun subscribeToGuild(
        guildId: String,
        callback: (GuildUpdate) -> Unit
    ): String {
        return subscribeToTable(
            table = "guilds",
            event = ChangeEvent.UPDATE,
            filter = mapOf("id" to guildId)
        ) { payload ->
            try {
                val update = json.decodeFromString<GuildUpdate>(payload.new)
                callback(update)
            } catch (e: Exception) {
                println("Error parsing guild update: ${e.message}")
            }
        }
    }

    /**
     * Subscribe to friend requests
     */
    fun subscribeToFriendRequests(
        playerId: String,
        callback: (FriendRequest) -> Unit
    ): String {
        return subscribeToTable(
            table = "friend_requests",
            event = ChangeEvent.INSERT,
            filter = mapOf("to_player_id" to playerId)
        ) { payload ->
            try {
                val request = json.decodeFromString<FriendRequest>(payload.new)
                callback(request)
            } catch (e: Exception) {
                println("Error parsing friend request: ${e.message}")
            }
        }
    }

    /**
     * Unsubscribe from changes
     */
    fun unsubscribe(subscriptionId: String) {
        subscriptions.remove(subscriptionId)
    }

    /**
     * Unsubscribe from all changes
     */
    fun unsubscribeAll() {
        subscriptions.clear()
    }

    /**
     * Listen to database changes
     */
    private suspend fun listenToChanges(subscription: ChangeSubscription) {
        // TODO: Integrate with Supabase Realtime
        // This would use: SupabaseClient.client.realtime.channel(...)
        // For now, this is a placeholder for the actual implementation
        while (subscriptions.containsKey(subscription.id)) {
            try {
                // Poll for changes or use WebSocket connection
                delay(1000)
            } catch (e: Exception) {
                println("Error listening to changes: ${e.message}")
            }
        }
    }

    /**
     * Generate unique subscription ID
     */
    private fun generateSubscriptionId(): String {
        return "sub_${System.currentTimeMillis()}_${(0..9999).random()}"
    }

    /**
     * Clean up resources
     */
    fun dispose() {
        changeScope.cancel()
        subscriptions.clear()
    }
}

/**
 * Change Subscription
 */
data class ChangeSubscription(
    val id: String,
    val table: String,
    val event: ChangeEvent,
    val filter: Map<String, String>?,
    val callback: (ChangePayload) -> Unit
)

/**
 * Change Event Types
 */
enum class ChangeEvent {
    INSERT,
    UPDATE,
    DELETE,
    ALL
}

/**
 * Change Payload
 */
data class ChangePayload(
    val eventType: String,
    val table: String,
    val schema: String = "public",
    val old: String = "{}",
    val new: String = "{}",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Data Models for Realtime Updates
 */
@Serializable
data class ChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val message: String,
    val channel: String,
    val timestamp: Long
)

@Serializable
data class PresenceData(
    val playerId: String,
    val status: String, // "online", "away", "busy", "offline"
    val lastSeen: Long
)

data class PresenceUpdate(
    val playerId: String,
    val status: String,
    val lastSeen: Long
)

@Serializable
data class GuildUpdate(
    val id: String,
    val name: String,
    val memberCount: Int,
    val level: Int,
    val experience: Int
)

@Serializable
data class FriendRequest(
    val id: String,
    val fromPlayerId: String,
    val fromPlayerName: String,
    val toPlayerId: String,
    val message: String? = null,
    val timestamp: Long
)
