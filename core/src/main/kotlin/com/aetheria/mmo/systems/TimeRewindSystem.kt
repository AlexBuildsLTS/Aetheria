package com.aetheria.mmo.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*
import com.aetheria.mmo.utils.Constants
import com.aetheria.mmo.utils.Logger
import java.util.*

/**
 * Time Rewind System
 * Implements time manipulation mechanics allowing players to rewind time
 * Stores entity snapshots and can restore previous states
 * Core mechanic for the "Chronomancer" class
 */
class TimeRewindSystem : IteratingSystem(
    Family.all(TimeDebtComponent::class.java, TransformComponent::class.java).get()
) {

    private val timeDebtMapper = ComponentMapper.getFor(TimeDebtComponent::class.java)
    private val transformMapper = ComponentMapper.getFor(TransformComponent::class.java)
    private val velocityMapper = ComponentMapper.getFor(VelocityComponent::class.java)
    private val healthMapper = ComponentMapper.getFor(HealthComponent::class.java)

    // Snapshot history per entity
    private val snapshotHistory = mutableMapOf<Entity, LinkedList<TimeSnapshot>>()

    // Rewind state
    private var isRewinding = false
    private var rewindSpeed = 1f
    private var snapshotTimer = 0f

    override fun update(deltaTime: Float) {
        if (!isRewinding) {
            // Record snapshots
            snapshotTimer += deltaTime

            if (snapshotTimer >= Constants.TIME_SNAPSHOT_INTERVAL) {
                recordSnapshots()
                snapshotTimer = 0f
            }
        } else {
            // Rewind time
            rewindTime(deltaTime)
        }

        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val timeDebt = timeDebtMapper.get(entity)

        // Update time debt
        if (timeDebt.debtAmount > 0f) {
            timeDebt.debtAmount -= deltaTime
            timeDebt.debtAmount = timeDebt.debtAmount.coerceAtLeast(0f)
        }
    }

    /**
     * Record snapshots for all entities
     */
    private fun recordSnapshots() {
        entities.forEach { entity ->
            recordSnapshot(entity)
        }
    }

    /**
     * Record snapshot for entity
     */
    private fun recordSnapshot(entity: Entity) {
        val transform = transformMapper.get(entity)
        val velocity = velocityMapper.get(entity)
        val health = healthMapper.get(entity)

        val snapshot = TimeSnapshot(
            position = transform.position.cpy(),
            rotation = transform.rotation.cpy(),
            velocity = velocity?.linear?.cpy() ?: Vector3.Zero,
            health = health?.current ?: 0f,
            timestamp = System.currentTimeMillis()
        )

        val history = snapshotHistory.getOrPut(entity) { LinkedList() }
        history.addLast(snapshot)

        // Limit history size
        if (history.size > Constants.MAX_TIME_SNAPSHOTS) {
            history.removeFirst()
        }
    }

    /**
     * Start time rewind
     */
    fun startRewind(duration: Float = Constants.TIME_REWIND_DURATION) {
        if (isRewinding) return

        isRewinding = true
        rewindSpeed = 1f
        Logger.info("TimeRewind", "Starting time rewind for ${duration}s")
    }

    /**
     * Stop time rewind
     */
    fun stopRewind() {
        if (!isRewinding) return

        isRewinding = false
        Logger.info("TimeRewind", "Stopped time rewind")
    }

    /**
     * Rewind time
     */
    private fun rewindTime(deltaTime: Float) {
        entities.forEach { entity ->
            rewindEntity(entity, deltaTime)
        }
    }

    /**
     * Rewind entity to previous state
     */
    private fun rewindEntity(entity: Entity, deltaTime: Float) {
        val history = snapshotHistory[entity] ?: return

        if (history.isEmpty()) {
            return
        }

        // Get previous snapshot
        val snapshot = history.removeLast()

        // Restore state
        val transform = transformMapper.get(entity)
        val velocity = velocityMapper.get(entity)
        val health = healthMapper.get(entity)

        transform.position.set(snapshot.position)
        transform.rotation.set(snapshot.rotation)
        velocity?.linear?.set(snapshot.velocity)
        health?.let { it.current = snapshot.health }
    }

    /**
     * Rewind entity to specific time
     */
    fun rewindToTime(entity: Entity, secondsAgo: Float) {
        val history = snapshotHistory[entity] ?: return
        val targetTime = System.currentTimeMillis() - (secondsAgo * 1000).toLong()

        // Find closest snapshot
        var closestSnapshot: TimeSnapshot? = null
        var closestDiff = Long.MAX_VALUE

        for (snapshot in history) {
            val diff = kotlin.math.abs(snapshot.timestamp - targetTime)
            if (diff < closestDiff) {
                closestDiff = diff
                closestSnapshot = snapshot
            }
        }

        // Restore to closest snapshot
        closestSnapshot?.let { snapshot ->
            val transform = transformMapper.get(entity)
            val velocity = velocityMapper.get(entity)
            val health = healthMapper.get(entity)

            transform.position.set(snapshot.position)
            transform.rotation.set(snapshot.rotation)
            velocity?.linear?.set(snapshot.velocity)
            health?.let { it.current = snapshot.health }

            Logger.debug("TimeRewind", "Rewound entity to ${secondsAgo}s ago")
        }
    }

    /**
     * Create time clone (snapshot of entity at current state)
     */
    fun createTimeClone(entity: Entity): TimeSnapshot? {
        val transform = transformMapper.get(entity) ?: return null
        val velocity = velocityMapper.get(entity)
        val health = healthMapper.get(entity)

        return TimeSnapshot(
            position = transform.position.cpy(),
            rotation = transform.rotation.cpy(),
            velocity = velocity?.linear?.cpy() ?: Vector3.Zero,
            health = health?.current ?: 0f,
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * Restore entity from snapshot
     */
    fun restoreFromSnapshot(entity: Entity, snapshot: TimeSnapshot) {
        val transform = transformMapper.get(entity) ?: return
        val velocity = velocityMapper.get(entity)
        val health = healthMapper.get(entity)

        transform.position.set(snapshot.position)
        transform.rotation.set(snapshot.rotation)
        velocity?.linear?.set(snapshot.velocity)
        health?.let { it.current = snapshot.health }
    }

    /**
     * Get snapshot history for entity
     */
    fun getHistory(entity: Entity): List<TimeSnapshot> {
        return snapshotHistory[entity]?.toList() ?: emptyList()
    }

    /**
     * Clear history for entity
     */
    fun clearHistory(entity: Entity) {
        snapshotHistory[entity]?.clear()
    }

    /**
     * Clear all history
     */
    fun clearAllHistory() {
        snapshotHistory.clear()
    }

    /**
     * Check if currently rewinding
     */
    fun isRewinding(): Boolean = isRewinding

    /**
     * Get number of snapshots for entity
     */
    fun getSnapshotCount(entity: Entity): Int {
        return snapshotHistory[entity]?.size ?: 0
    }
}

/**
 * Time Snapshot
 * Stores entity state at a specific point in time
 */
data class TimeSnapshot(
    val position: Vector3,
    val rotation: Quaternion,
    val velocity: Vector3,
    val health: Float,
    val timestamp: Long
)
