package com.aetheria.mmo.net

/**
 * Network Connection State
 */
enum class NetworkState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    AUTHENTICATING,
    AUTHENTICATED,
    IN_GAME,
    RECONNECTING,
    ERROR
}

/**
 * Network State Manager
 * Tracks current connection state and transitions
 */
class NetworkStateManager {
    private var currentState: NetworkState = NetworkState.DISCONNECTED
    private val stateListeners = mutableListOf<(NetworkState, NetworkState) -> Unit>()

    fun getCurrentState(): NetworkState = currentState

    fun setState(newState: NetworkState) {
        if (currentState != newState) {
            val oldState = currentState
            currentState = newState
            notifyListeners(oldState, newState)
        }
    }

    fun isConnected(): Boolean {
        return currentState in listOf(
            NetworkState.CONNECTED,
            NetworkState.AUTHENTICATING,
            NetworkState.AUTHENTICATED,
            NetworkState.IN_GAME
        )
    }

    fun canSendPackets(): Boolean {
        return currentState in listOf(
            NetworkState.AUTHENTICATED,
            NetworkState.IN_GAME
        )
    }

    fun addStateListener(listener: (oldState: NetworkState, newState: NetworkState) -> Unit) {
        stateListeners.add(listener)
    }

    fun removeStateListener(listener: (NetworkState, NetworkState) -> Unit) {
        stateListeners.remove(listener)
    }

    private fun notifyListeners(oldState: NetworkState, newState: NetworkState) {
        stateListeners.forEach { it(oldState, newState) }
    }

    fun reset() {
        currentState = NetworkState.DISCONNECTED
    }
}
