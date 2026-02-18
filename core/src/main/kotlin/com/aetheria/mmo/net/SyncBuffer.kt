package com.aetheria.mmo.net

import com.badlogic.gdx.math.Vector3
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Sync Buffer
 * Buffers network state updates for smooth interpolation
 * Implements snapshot interpolation for networked entities
 */
class SyncBuffer(private val bufferSize: Int = 10) {
    private val positionBuffer = ConcurrentLinkedQueue<PositionSnapshot>()
    private val rotationBuffer = ConcurrentLinkedQueue<RotationSnapshot>()

    data class PositionSnapshot(
        val position: Vector3,
        val timestamp: Long
    )

    data class RotationSnapshot(
        val yaw: Float,
        val pitch: Float,
        val timestamp: Long
    )

    /**
     * Add position snapshot
     */
    fun addPosition(position: Vector3, timestamp: Long = System.currentTimeMillis()) {
        positionBuffer.offer(PositionSnapshot(position.cpy(), timestamp))

        // Keep buffer size limited
        while (positionBuffer.size > bufferSize) {
            positionBuffer.poll()
        }
    }

    /**
     * Add rotation snapshot
     */
    fun addRotation(yaw: Float, pitch: Float, timestamp: Long = System.currentTimeMillis()) {
        rotationBuffer.offer(RotationSnapshot(yaw, pitch, timestamp))

        while (rotationBuffer.size > bufferSize) {
            rotationBuffer.poll()
        }
    }

    /**
     * Get interpolated position at specific time
     */
    fun getInterpolatedPosition(renderTime: Long): Vector3? {
        if (positionBuffer.size < 2) return positionBuffer.peek()?.position

        val snapshots = positionBuffer.toList()

        // Find two snapshots to interpolate between
        for (i in 0 until snapshots.size - 1) {
            val snap1 = snapshots[i]
            val snap2 = snapshots[i + 1]

            if (renderTime >= snap1.timestamp && renderTime <= snap2.timestamp) {
                val alpha = (renderTime - snap1.timestamp).toFloat() /
                           (snap2.timestamp - snap1.timestamp).toFloat()

                return Vector3(snap1.position).lerp(snap2.position, alpha)
            }
        }

        // If render time is ahead, use latest
        return snapshots.lastOrNull()?.position
    }

    /**
     * Get interpolated rotation at specific time
     */
    fun getInterpolatedRotation(renderTime: Long): Pair<Float, Float>? {
        if (rotationBuffer.size < 2) {
            val snap = rotationBuffer.peek()
            return snap?.let { Pair(it.yaw, it.pitch) }
        }

        val snapshots = rotationBuffer.toList()

        for (i in 0 until snapshots.size - 1) {
            val snap1 = snapshots[i]
            val snap2 = snapshots[i + 1]

            if (renderTime >= snap1.timestamp && renderTime <= snap2.timestamp) {
                val alpha = (renderTime - snap1.timestamp).toFloat() /
                           (snap2.timestamp - snap1.timestamp).toFloat()

                val yaw = lerpAngle(snap1.yaw, snap2.yaw, alpha)
                val pitch = lerpAngle(snap1.pitch, snap2.pitch, alpha)

                return Pair(yaw, pitch)
            }
        }

        val last = snapshots.lastOrNull()
        return last?.let { Pair(it.yaw, it.pitch) }
    }

    /**
     * Lerp between angles (handles wrapping)
     */
    private fun lerpAngle(a: Float, b: Float, t: Float): Float {
        var delta = b - a

        // Wrap to shortest path
        while (delta > 180f) delta -= 360f
        while (delta < -180f) delta += 360f

        return a + delta * t
    }

    /**
     * Clear all buffers
     */
    fun clear() {
        positionBuffer.clear()
        rotationBuffer.clear()
    }

    /**
     * Get buffer statistics
     */
    fun getStats(): BufferStats {
        return BufferStats(
            positionBufferSize = positionBuffer.size,
            rotationBufferSize = rotationBuffer.size,
            oldestPositionTime = positionBuffer.peek()?.timestamp ?: 0L,
            newestPositionTime = positionBuffer.toList().lastOrNull()?.timestamp ?: 0L
        )
    }
}

data class BufferStats(
    val positionBufferSize: Int,
    val rotationBufferSize: Int,
    val oldestPositionTime: Long,
    val newestPositionTime: Long
) {
    fun getBufferDelay(): Long = newestPositionTime - oldestPositionTime
}
