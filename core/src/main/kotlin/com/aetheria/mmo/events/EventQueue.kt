package com.aetheria.mmo.events

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Thread-safe event queue for decoupled system communication
 * Prevents tight coupling between systems by using event-driven architecture
 */
object EventQueue {
    private val queue = ConcurrentLinkedQueue<GameEvent>()
    @PublishedApi
    internal val listeners = mutableMapOf<Class<out GameEvent>, MutableList<(GameEvent) -> Unit>>()

    /**
     * Post an event to the queue
     */
    fun post(event: GameEvent) {
        queue.offer(event)
    }

    /**
     * Register a listener for a specific event type
     */
    inline fun <reified T : GameEvent> subscribe(noinline listener: (T) -> Unit) {
        val eventClass = T::class.java
        listeners.getOrPut(eventClass) { mutableListOf() }
            .add(listener as (GameEvent) -> Unit)
    }

    /**
     * Process all queued events
     * Should be called once per frame
     */
    fun process() {
        while (queue.isNotEmpty()) {
            val event = queue.poll() ?: break

            // Notify all listeners for this event type
            listeners[event::class.java]?.forEach { listener ->
                try {
                    listener(event)
                } catch (e: Exception) {
                    System.err.println("Error processing event ${event::class.simpleName}: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Clear all events and listeners
     */
    fun clear() {
        queue.clear()
        listeners.clear()
    }

    /**
     * Get current queue size (for debugging)
     */
    fun size(): Int = queue.size
}
