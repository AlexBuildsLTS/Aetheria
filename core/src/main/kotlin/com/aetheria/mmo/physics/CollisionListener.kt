package com.aetheria.mmo.physics

import com.badlogic.gdx.utils.Disposable

/**
 * Collision Listener
 * Handles collision events
 * Dispatches events to game systems
 *
 * NOTE: Simplified version - full physics integration pending
 */
class CollisionListener : Disposable {

    interface CollisionCallback {
        fun onCollisionEnter(objectA: Any, objectB: Any)
        fun onCollisionStay(objectA: Any, objectB: Any)
        fun onCollisionExit(objectA: Any, objectB: Any)
    }

    private val callbacks = mutableListOf<CollisionCallback>()
    private val collisionPairs = mutableSetOf<Pair<Long, Long>>()

    /**
     * Registers a collision callback
     */
    fun addCallback(callback: CollisionCallback) {
        callbacks.add(callback)
    }

    /**
     * Removes a collision callback
     */
    fun removeCallback(callback: CollisionCallback) {
        callbacks.remove(callback)
    }

    /**
     * Processes a collision
     */
    fun processCollision(obj0: Any, obj1: Any) {
        val pairKey = createPairKey(obj0, obj1)

        if (!collisionPairs.contains(pairKey)) {
            // New collision - trigger enter event
            collisionPairs.add(pairKey)
            callbacks.forEach { it.onCollisionEnter(obj0, obj1) }
        } else {
            // Ongoing collision - trigger stay event
            callbacks.forEach { it.onCollisionStay(obj0, obj1) }
        }
    }

    /**
     * Updates the listener - call this each frame to detect collision exits
     */
    fun update(activeCollisions: Set<Pair<Long, Long>>) {
        // Find collisions that ended
        val endedCollisions = collisionPairs.filter { !activeCollisions.contains(it) }

        endedCollisions.forEach { pair ->
            collisionPairs.remove(pair)
        }
    }

    /**
     * Clears all tracked collisions
     */
    fun clear() {
        collisionPairs.clear()
    }

    /**
     * Creates a unique key for a collision pair
     */
    private fun createPairKey(obj0: Any, obj1: Any): Pair<Long, Long> {
        val id0 = obj0.hashCode().toLong()
        val id1 = obj1.hashCode().toLong()
        return if (id0 < id1) Pair(id0, id1) else Pair(id1, id0)
    }

    override fun dispose() {
        callbacks.clear()
        collisionPairs.clear()
    }
}
