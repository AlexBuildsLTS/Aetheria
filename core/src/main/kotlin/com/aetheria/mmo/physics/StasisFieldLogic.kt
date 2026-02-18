package com.aetheria.mmo.physics

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array

/**
 * Stasis Field Logic
 * Implements time-manipulation mechanics (slow-mo, freeze, time reversal)
 * Part of the Weaver class abilities
 */
class StasisFieldLogic {

    data class StasisField(
        val center: Vector3,
        val radius: Float,
        val timeScale: Float, // 0.0 = frozen, 0.5 = half speed, 1.0 = normal
        val duration: Float,
        var elapsed: Float = 0f,
        val affectedBodies: MutableSet<Any> = mutableSetOf()
    )

    data class BodyState(
        val velocity: Vector3,
        val angularVelocity: Vector3,
        val originalTimeScale: Float = 1f
    )

    private val activeFields = Array<StasisField>()
    private val bodyStates = mutableMapOf<Any, BodyState>()
    private val tmpVec = Vector3()

    /**
     * Creates a new stasis field
     */
    fun createStasisField(
        center: Vector3,
        radius: Float,
        timeScale: Float = 0.1f,
        duration: Float = 5f
    ): StasisField {
        val field = StasisField(center.cpy(), radius, timeScale, duration)
        activeFields.add(field)
        return field
    }

    /**
     * Creates a freeze field (complete time stop)
     */
    fun createFreezeField(center: Vector3, radius: Float, duration: Float = 3f): StasisField {
        return createStasisField(center, radius, 0f, duration)
    }

    /**
     * Creates a slow-motion field
     */
    fun createSlowMotionField(center: Vector3, radius: Float, slowFactor: Float = 0.3f, duration: Float = 5f): StasisField {
        return createStasisField(center, radius, slowFactor, duration)
    }

    /**
     * Updates all stasis fields
     */
    fun update(deltaTime: Float, allBodies: List<Any>) {
        // Update field durations
        val iterator = activeFields.iterator()
        while (iterator.hasNext()) {
            val field = iterator.next()
            field.elapsed += deltaTime

            if (field.elapsed >= field.duration) {
                // Field expired - restore affected bodies
                restoreBodies(field)
                iterator.remove()
            } else {
                // Update field effects
                updateField(field, allBodies, deltaTime)
            }
        }

        // Clean up body states for bodies no longer affected
        cleanupBodyStates()
    }

    /**
     * Updates a single stasis field
     */
    private fun updateField(field: StasisField, allBodies: List<Any>, deltaTime: Float) {
        // Simplified implementation - actual physics integration would go here
        // This is a placeholder for the stasis field logic
    }

    /**
     * Applies time scale to a body
     */
    private fun applyTimeScale(body: Any, timeScale: Float) {
        // Placeholder - actual implementation would manipulate physics body
    }

    /**
     * Restores a body to its original state
     */
    private fun restoreBody(body: Any) {
        val state = bodyStates[body] ?: return
        bodyStates.remove(body)
    }

    /**
     * Restores all bodies affected by a field
     */
    private fun restoreBodies(field: StasisField) {
        field.affectedBodies.forEach { body ->
            restoreBody(body)
        }
        field.affectedBodies.clear()
    }

    /**
     * Cleans up body states for bodies no longer in any field
     */
    private fun cleanupBodyStates() {
        val affectedBodies = activeFields.flatMap { it.affectedBodies }.toSet()
        val bodiesToRemove = bodyStates.keys.filter { !affectedBodies.contains(it) }
        bodiesToRemove.forEach { bodyStates.remove(it) }
    }

    /**
     * Checks if a body is affected by any stasis field
     */
    fun isBodyAffected(body: Any): Boolean {
        return activeFields.any { it.affectedBodies.contains(body) }
    }

    /**
     * Gets the time scale affecting a body
     */
    fun getBodyTimeScale(body: Any): Float {
        for (field in activeFields) {
            if (field.affectedBodies.contains(body)) {
                return field.timeScale
            }
        }
        return 1f
    }

    /**
     * Removes all stasis fields
     */
    fun clear() {
        activeFields.forEach { restoreBodies(it) }
        activeFields.clear()
        bodyStates.clear()
    }

    /**
     * Gets all active fields
     */
    fun getActiveFields(): List<StasisField> = activeFields.toList()

    /**
     * Gets the number of active fields
     */
    fun getFieldCount(): Int = activeFields.size
}
