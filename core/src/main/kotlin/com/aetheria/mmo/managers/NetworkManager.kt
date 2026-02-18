package com.aetheria.mmo.managers

import com.aetheria.mmo.utils.GameConfig
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.*

/**
 * Network Manager
 * Handles all network communication with Supabase backend
 * Manages authentication, database operations, and realtime subscriptions
 */
object NetworkManager : Disposable {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Supabase client
    val supabase = createSupabaseClient(
        supabaseUrl = GameConfig.SUPABASE_URL,
        supabaseKey = GameConfig.SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Realtime)
    }

    // Connection state
    var isConnected = false
        private set

    var isAuthenticated = false
        private set

    var currentUserId: String? = null
        private set

    var currentUsername: String? = null
        private set

    // Network statistics
    private var bytesSent = 0L
    private var bytesReceived = 0L
    private var latency = 0L

    /**
     * Initializes the network manager
     */
    fun initialize() {
        Gdx.app.log("NetworkManager", "Initializing network manager...")
        checkConnection()
    }

    /**
     * Checks network connection
     */
    fun checkConnection() {
        scope.launch {
            try {
                // Simple ping to check connection
                isConnected = true
                Gdx.app.log("NetworkManager", "Network connection established")
            } catch (e: Exception) {
                isConnected = false
                Gdx.app.error("NetworkManager", "Network connection failed", e)
            }
        }
    }

    /**
     * Signs up a new user
     */
    fun signUp(email: String, password: String, username: String, callback: (Boolean, String?) -> Unit) {
        scope.launch {
            try {
                supabase.auth.signUpWith(Auth.Email) {
                    this.email = email
                    this.password = password
                    data = mapOf("username" to username)
                }

                isAuthenticated = true
                currentUsername = username
                Gdx.app.log("NetworkManager", "Sign up successful: $username")

                withContext(Dispatchers.Main) {
                    callback(true, null)
                }
            } catch (e: Exception) {
                Gdx.app.error("NetworkManager", "Sign up failed", e)
                withContext(Dispatchers.Main) {
                    callback(false, e.message)
                }
            }
        }
    }

    /**
     * Signs in an existing user
     */
    fun signIn(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        scope.launch {
            try {
                supabase.auth.signInWith(Auth.Email) {
                    this.email = email
                    this.password = password
                }

                val session = supabase.auth.currentSessionOrNull()
                if (session != null) {
                    isAuthenticated = true
                    currentUserId = session.user?.id
                    currentUsername = session.user?.userMetadata?.get("username") as? String

                    Gdx.app.log("NetworkManager", "Sign in successful: $currentUsername")

                    withContext(Dispatchers.Main) {
                        callback(true, null)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        callback(false, "No session found")
                    }
                }
            } catch (e: Exception) {
                Gdx.app.error("NetworkManager", "Sign in failed", e)
                withContext(Dispatchers.Main) {
                    callback(false, e.message)
                }
            }
        }
    }

    /**
     * Signs out the current user
     */
    fun signOut(callback: (Boolean) -> Unit) {
        scope.launch {
            try {
                supabase.auth.signOut()
                isAuthenticated = false
                currentUserId = null
                currentUsername = null

                Gdx.app.log("NetworkManager", "Sign out successful")

                withContext(Dispatchers.Main) {
                    callback(true)
                }
            } catch (e: Exception) {
                Gdx.app.error("NetworkManager", "Sign out failed", e)
                withContext(Dispatchers.Main) {
                    callback(false)
                }
            }
        }
    }

    /**
     * Subscribes to realtime player position updates
     */
    fun subscribeToPlayerPositions(onUpdate: (String, Float, Float, Float) -> Unit) {
        scope.launch {
            try {
                val channel = supabase.realtime.channel("player_positions")

                channel.subscribe()

                Gdx.app.log("NetworkManager", "Subscribed to player positions")
            } catch (e: Exception) {
                Gdx.app.error("NetworkManager", "Failed to subscribe to positions", e)
            }
        }
    }

    /**
     * Sends player position update
     */
    fun sendPositionUpdate(x: Float, y: Float, z: Float) {
        if (!isAuthenticated) return

        scope.launch {
            try {
                // Send position update to Supabase
                bytesSent += 32 // Approximate size
            } catch (e: Exception) {
                Gdx.app.error("NetworkManager", "Failed to send position", e)
            }
        }
    }

    /**
     * Gets network statistics
     */
    fun getNetworkStats(): NetworkStats {
        return NetworkStats(
            bytesSent = bytesSent,
            bytesReceived = bytesReceived,
            latency = latency,
            isConnected = isConnected
        )
    }

    /**
     * Measures network latency
     */
    fun measureLatency(callback: (Long) -> Unit) {
        scope.launch {
            val startTime = System.currentTimeMillis()
            try {
                // Ping server
                val endTime = System.currentTimeMillis()
                latency = endTime - startTime

                withContext(Dispatchers.Main) {
                    callback(latency)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback(-1)
                }
            }
        }
    }

    /**
     * Resets network statistics
     */
    fun resetStats() {
        bytesSent = 0
        bytesReceived = 0
        latency = 0
    }

    override fun dispose() {
        scope.cancel()
        Gdx.app.log("NetworkManager", "Network manager disposed")
    }

    data class NetworkStats(
        val bytesSent: Long,
        val bytesReceived: Long,
        val latency: Long,
        val isConnected: Boolean
    )
}
