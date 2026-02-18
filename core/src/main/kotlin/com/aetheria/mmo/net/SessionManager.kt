package com.aetheria.mmo.net

import java.util.concurrent.ConcurrentHashMap

/**
 * Session Manager
 * Manages player sessions and connection state
 */
class SessionManager {
    private val sessions = ConcurrentHashMap<String, PlayerSession>()
    private var localSession: PlayerSession? = null

    /**
     * Create a new session for the local player
     */
    fun createLocalSession(playerId: String, username: String, token: String): PlayerSession {
        val session = PlayerSession(
            playerId = playerId,
            username = username,
            token = token,
            isLocal = true
        )
        localSession = session
        sessions[playerId] = session
        return session
    }

    /**
     * Add a remote player session
     */
    fun addRemoteSession(playerId: String, username: String): PlayerSession {
        val session = PlayerSession(
            playerId = playerId,
            username = username,
            isLocal = false
        )
        sessions[playerId] = session
        return session
    }

    /**
     * Get session by player ID
     */
    fun getSession(playerId: String): PlayerSession? {
        return sessions[playerId]
    }

    /**
     * Get local player session
     */
    fun getLocalSession(): PlayerSession? = localSession

    /**
     * Remove a session
     */
    fun removeSession(playerId: String) {
        sessions.remove(playerId)
        if (localSession?.playerId == playerId) {
            localSession = null
        }
    }

    /**
     * Get all active sessions
     */
    fun getAllSessions(): List<PlayerSession> {
        return sessions.values.toList()
    }

    /**
     * Get all remote sessions (excluding local player)
     */
    fun getRemoteSessions(): List<PlayerSession> {
        return sessions.values.filter { !it.isLocal }
    }

    /**
     * Update session last activity
     */
    fun updateActivity(playerId: String) {
        sessions[playerId]?.updateActivity()
    }

    /**
     * Check for inactive sessions (timeout after 30 seconds)
     */
    fun getInactiveSessions(timeoutMs: Long = 30000): List<PlayerSession> {
        val now = System.currentTimeMillis()
        return sessions.values.filter { session ->
            !session.isLocal && (now - session.lastActivity) > timeoutMs
        }
    }

    /**
     * Clear all sessions
     */
    fun clear() {
        sessions.clear()
        localSession = null
    }

    /**
     * Get session count
     */
    fun getSessionCount(): Int = sessions.size
}

/**
 * Player Session
 * Represents a connected player's session
 */
data class PlayerSession(
    val playerId: String,
    val username: String,
    val token: String = "",
    val isLocal: Boolean = false,
    var lastActivity: Long = System.currentTimeMillis(),
    var ping: Int = 0,
    var isConnected: Boolean = true
) {
    fun updateActivity() {
        lastActivity = System.currentTimeMillis()
    }

    fun updatePing(newPing: Int) {
        ping = newPing
    }

    fun disconnect() {
        isConnected = false
    }

    fun reconnect() {
        isConnected = true
        updateActivity()
    }
}
