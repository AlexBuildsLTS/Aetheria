package com.aetheria.mmo.net

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Matchmaking System
 * Handles player matchmaking for PvP, dungeons, raids, etc.
 */
class Matchmaking(
    private val supabaseClient: SupabaseClient
) {
    private val matchmakingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val activeQueues = ConcurrentHashMap<String, MatchmakingQueue>()
    private var currentQueue: MatchmakingQueue? = null

    /**
     * Join a matchmaking queue
     */
    suspend fun joinQueue(
        playerId: String,
        queueType: QueueType,
        rating: Int = 1000,
        partyMembers: List<String> = emptyList()
    ): MatchmakingResult {
        return withContext(Dispatchers.IO) {
            try {
                val queue = activeQueues.getOrPut(queueType.name) {
                    MatchmakingQueue(queueType)
                }

                val player = MatchmakingPlayer(
                    playerId = playerId,
                    rating = rating,
                    partyMembers = partyMembers,
                    queueTime = System.currentTimeMillis()
                )

                queue.addPlayer(player)
                currentQueue = queue

                MatchmakingResult.Queued(queueType, queue.getQueueSize())
            } catch (e: Exception) {
                MatchmakingResult.Error("Failed to join queue: ${e.message}")
            }
        }
    }

    /**
     * Leave current queue
     */
    fun leaveQueue(playerId: String) {
        currentQueue?.removePlayer(playerId)
        currentQueue = null
    }

    /**
     * Find match for player
     */
    suspend fun findMatch(playerId: String): MatchmakingResult {
        return withContext(Dispatchers.IO) {
            try {
                val queue = currentQueue ?: return@withContext MatchmakingResult.Error("Not in queue")

                val match = queue.findMatch(playerId)

                if (match != null) {
                    // Create match session
                    val sessionId = createMatchSession(match)

                    MatchmakingResult.MatchFound(
                        sessionId = sessionId,
                        players = match.players.map { it.playerId },
                        queueType = queue.queueType
                    )
                } else {
                    MatchmakingResult.Searching(queue.getQueueSize())
                }
            } catch (e: Exception) {
                MatchmakingResult.Error("Matchmaking error: ${e.message}")
            }
        }
    }

    /**
     * Get queue status
     */
    fun getQueueStatus(queueType: QueueType): QueueStatus {
        val queue = activeQueues[queueType.name]
        return QueueStatus(
            queueType = queueType,
            playersInQueue = queue?.getQueueSize() ?: 0,
            averageWaitTime = queue?.getAverageWaitTime() ?: 0L
        )
    }

    /**
     * Create match session in database
     */
    private suspend fun createMatchSession(match: Match): String {
        val sessionId = generateSessionId()

        // TODO: Store match data in Supabase
        // This would use: SupabaseClient.client.postgrest["match_sessions"].insert(...)

        return sessionId
    }

    /**
     * Generate unique session ID
     */
    private fun generateSessionId(): String {
        return "match_${System.currentTimeMillis()}_${(0..9999).random()}"
    }

    /**
     * Clean up resources
     */
    fun dispose() {
        matchmakingScope.cancel()
        activeQueues.clear()
    }
}

/**
 * Matchmaking Queue
 */
class MatchmakingQueue(val queueType: QueueType) {
    private val players = ConcurrentHashMap<String, MatchmakingPlayer>()
    private val queueTimes = mutableListOf<Long>()

    fun addPlayer(player: MatchmakingPlayer) {
        players[player.playerId] = player
    }

    fun removePlayer(playerId: String) {
        players.remove(playerId)?.let { player ->
            val waitTime = System.currentTimeMillis() - player.queueTime
            queueTimes.add(waitTime)
        }
    }

    fun getQueueSize(): Int = players.size

    fun getAverageWaitTime(): Long {
        return if (queueTimes.isEmpty()) 0L else queueTimes.average().toLong()
    }

    /**
     * Find a match for the player
     * Uses skill-based matchmaking with rating ranges
     */
    fun findMatch(playerId: String): Match? {
        val player = players[playerId] ?: return null

        val requiredPlayers = queueType.playersPerMatch
        if (players.size < requiredPlayers) return null

        // Find players with similar rating
        val ratingRange = 200 // ±200 rating
        val candidates = players.values.filter {
            kotlin.math.abs(it.rating - player.rating) <= ratingRange
        }

        if (candidates.size >= requiredPlayers) {
            val matchPlayers = candidates.take(requiredPlayers)

            // Remove matched players from queue
            matchPlayers.forEach { players.remove(it.playerId) }

            return Match(
                queueType = queueType,
                players = matchPlayers,
                averageRating = matchPlayers.map { it.rating }.average().toInt()
            )
        }

        return null
    }
}

/**
 * Queue Types
 */
enum class QueueType(val playersPerMatch: Int) {
    DUEL_1V1(2),
    ARENA_2V2(4),
    ARENA_3V3(6),
    BATTLEGROUND_5V5(10),
    DUNGEON_4_PLAYER(4),
    RAID_10_PLAYER(10),
    RAID_25_PLAYER(25)
}

/**
 * Matchmaking Player
 */
data class MatchmakingPlayer(
    val playerId: String,
    val rating: Int,
    val partyMembers: List<String> = emptyList(),
    val queueTime: Long = System.currentTimeMillis()
)

/**
 * Match
 */
data class Match(
    val queueType: QueueType,
    val players: List<MatchmakingPlayer>,
    val averageRating: Int
)

/**
 * Queue Status
 */
data class QueueStatus(
    val queueType: QueueType,
    val playersInQueue: Int,
    val averageWaitTime: Long
)

/**
 * Matchmaking Result
 */
sealed class MatchmakingResult {
    data class Queued(val queueType: QueueType, val queueSize: Int) : MatchmakingResult()
    data class Searching(val queueSize: Int) : MatchmakingResult()
    data class MatchFound(
        val sessionId: String,
        val players: List<String>,
        val queueType: QueueType
    ) : MatchmakingResult()
    data class Error(val message: String) : MatchmakingResult()
}
