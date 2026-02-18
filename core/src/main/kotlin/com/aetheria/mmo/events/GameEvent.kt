package com.aetheria.mmo.events

/**
 * Base interface for all game events
 * Events are used for decoupled communication between systems
 */
interface GameEvent {
    val timestamp: Long
        get() = System.currentTimeMillis()
}
