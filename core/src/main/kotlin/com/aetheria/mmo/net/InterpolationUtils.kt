package com.aetheria.mmo.net

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3

/**
 * Interpolation Utilities
 * Smooth interpolation for network entity positions and rotations
 * Implements snapshot interpolation for lag compensation
 */
object InterpolationUtils {

    /**
     * Linear interpolation between two positions
     */
    fun lerpPosition(start: Vector3, end: Vector3, alpha: Float, out: Vector3 = Vector3()): Vector3 {
        return out.set(start).lerp(end, alpha)
    }

    /**
     * Spherical linear interpolation for rotations
     */
    fun slerpRotation(start: Quaternion, end: Quaternion, alpha: Float, out: Quaternion = Quaternion()): Quaternion {
        return out.set(start).slerp(end, alpha)
    }

    /**
     * Smooth damping interpolation (exponential decay)
     * Good for camera following and smooth movement
     */
    fun smoothDamp(
        current: Vector3,
        target: Vector3,
        velocity: Vector3,
        smoothTime: Float,
        deltaTime: Float,
        maxSpeed: Float = Float.POSITIVE_INFINITY,
        out: Vector3 = Vector3()
    ): Vector3 {
        val omega = 2f / smoothTime
        val x = omega * deltaTime
        val exp = 1f / (1f + x + 0.48f * x * x + 0.235f * x * x * x)

        var changeX = current.x - target.x
        var changeY = current.y - target.y
        var changeZ = current.z - target.z

        val originalTarget = target.cpy()

        // Clamp maximum change
        val maxChange = maxSpeed * smoothTime
        val maxChangeSq = maxChange * maxChange
        val sqDist = changeX * changeX + changeY * changeY + changeZ * changeZ

        if (sqDist > maxChangeSq) {
            val mag = kotlin.math.sqrt(sqDist)
            changeX = changeX / mag * maxChange
            changeY = changeY / mag * maxChange
            changeZ = changeZ / mag * maxChange
        }

        val targetX = current.x - changeX
        val targetY = current.y - changeY
        val targetZ = current.z - changeZ

        val tempX = (velocity.x + omega * changeX) * deltaTime
        val tempY = (velocity.y + omega * changeY) * deltaTime
        val tempZ = (velocity.z + omega * changeZ) * deltaTime

        velocity.x = (velocity.x - omega * tempX) * exp
        velocity.y = (velocity.y - omega * tempY) * exp
        velocity.z = (velocity.z - omega * tempZ) * exp

        var outputX = targetX + (changeX + tempX) * exp
        var outputY = targetY + (changeY + tempY) * exp
        var outputZ = targetZ + (changeZ + tempZ) * exp

        // Prevent overshooting
        val origMinusCurrentX = originalTarget.x - current.x
        val origMinusCurrentY = originalTarget.y - current.y
        val origMinusCurrentZ = originalTarget.z - current.z
        val outMinusOrigX = outputX - originalTarget.x
        val outMinusOrigY = outputY - originalTarget.y
        val outMinusOrigZ = outputZ - originalTarget.z

        if (origMinusCurrentX * outMinusOrigX + origMinusCurrentY * outMinusOrigY + origMinusCurrentZ * outMinusOrigZ > 0) {
            outputX = originalTarget.x
            outputY = originalTarget.y
            outputZ = originalTarget.z
            velocity.setZero()
        }

        return out.set(outputX, outputY, outputZ)
    }

    /**
     * Calculate interpolation alpha based on timestamps
     * Used for snapshot interpolation
     */
    fun calculateAlpha(
        currentTime: Long,
        snapshotTime1: Long,
        snapshotTime2: Long
    ): Float {
        if (snapshotTime2 <= snapshotTime1) return 1f

        val totalTime = (snapshotTime2 - snapshotTime1).toFloat()
        val elapsed = (currentTime - snapshotTime1).toFloat()

        return MathUtils.clamp(elapsed / totalTime, 0f, 1f)
    }

    /**
     * Extrapolate position based on velocity
     * Used for client-side prediction
     */
    fun extrapolatePosition(
        position: Vector3,
        velocity: Vector3,
        deltaTime: Float,
        out: Vector3 = Vector3()
    ): Vector3 {
        return out.set(position).mulAdd(velocity, deltaTime)
    }

    /**
     * Dead reckoning - predict future position
     */
    fun deadReckon(
        lastPosition: Vector3,
        lastVelocity: Vector3,
        timeSinceUpdate: Float,
        maxExtrapolation: Float = 1f,
        out: Vector3 = Vector3()
    ): Vector3 {
        val clampedTime = MathUtils.clamp(timeSinceUpdate, 0f, maxExtrapolation)
        return extrapolatePosition(lastPosition, lastVelocity, clampedTime, out)
    }

    /**
     * Reconcile predicted position with server position
     * Returns the corrected position
     */
    fun reconcile(
        predictedPosition: Vector3,
        serverPosition: Vector3,
        threshold: Float = 0.5f,
        out: Vector3 = Vector3()
    ): Vector3 {
        val distance = predictedPosition.dst(serverPosition)

        return if (distance > threshold) {
            // Snap to server position if too far off
            out.set(serverPosition)
        } else {
            // Smoothly correct
            out.set(predictedPosition).lerp(serverPosition, 0.1f)
        }
    }
}

/**
 * Snapshot Buffer
 * Stores historical snapshots for interpolation
 */
class SnapshotBuffer<T>(private val bufferSize: Int = 10) {
    private val snapshots = mutableListOf<Snapshot<T>>()

    data class Snapshot<T>(
        val data: T,
        val timestamp: Long
    )

    fun add(data: T, timestamp: Long = System.currentTimeMillis()) {
        snapshots.add(Snapshot(data, timestamp))

        // Keep buffer size limited
        while (snapshots.size > bufferSize) {
            snapshots.removeAt(0)
        }
    }

    fun getInterpolated(renderTime: Long): Pair<Snapshot<T>, Snapshot<T>>? {
        if (snapshots.size < 2) return null

        // Find two snapshots to interpolate between
        for (i in 0 until snapshots.size - 1) {
            val snap1 = snapshots[i]
            val snap2 = snapshots[i + 1]

            if (renderTime >= snap1.timestamp && renderTime <= snap2.timestamp) {
                return Pair(snap1, snap2)
            }
        }

        // If render time is ahead of all snapshots, use the two most recent
        return if (snapshots.size >= 2) {
            Pair(snapshots[snapshots.size - 2], snapshots[snapshots.size - 1])
        } else {
            null
        }
    }

    fun clear() {
        snapshots.clear()
    }

    fun size(): Int = snapshots.size
}
