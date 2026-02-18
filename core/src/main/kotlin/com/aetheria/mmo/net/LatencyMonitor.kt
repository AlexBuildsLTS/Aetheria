package com.aetheria.mmo.net

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Latency Monitor
 * Tracks network latency (ping) and provides statistics
 */
class LatencyMonitor(
    private val sampleSize: Int = 20
) {
    private val samples = ConcurrentLinkedQueue<Int>()
    private var currentPing: Int = 0
    private var averagePing: Int = 0
    private var minPing: Int = Int.MAX_VALUE
    private var maxPing: Int = 0
    private var jitter: Int = 0

    // Ping tracking
    private val pendingPings = mutableMapOf<Long, Long>()

    /**
     * Send a ping request
     * Returns the client timestamp to be sent with the ping
     */
    fun sendPing(): Long {
        val timestamp = System.currentTimeMillis()
        pendingPings[timestamp] = timestamp
        return timestamp
    }

    /**
     * Receive a pong response
     * @param clientTimestamp The timestamp from the original ping
     */
    fun receivePong(clientTimestamp: Long) {
        pendingPings.remove(clientTimestamp)?.let { sentTime ->
            val rtt = (System.currentTimeMillis() - sentTime).toInt()
            addSample(rtt)
        }
    }

    /**
     * Add a latency sample
     */
    private fun addSample(latency: Int) {
        samples.offer(latency)

        // Keep only the most recent samples
        while (samples.size > sampleSize) {
            samples.poll()
        }

        // Update statistics
        updateStats()
    }

    /**
     * Update latency statistics
     */
    private fun updateStats() {
        if (samples.isEmpty()) return

        val sampleList = samples.toList()

        currentPing = sampleList.last()
        averagePing = sampleList.average().toInt()
        minPing = sampleList.minOrNull() ?: 0
        maxPing = sampleList.maxOrNull() ?: 0

        // Calculate jitter (variance in latency)
        if (sampleList.size >= 2) {
            val differences = sampleList.zipWithNext { a, b -> kotlin.math.abs(b - a) }
            jitter = differences.average().toInt()
        }
    }

    /**
     * Get current ping in milliseconds
     */
    fun getCurrentPing(): Int = currentPing

    /**
     * Get average ping in milliseconds
     */
    fun getAveragePing(): Int = averagePing

    /**
     * Get minimum ping in milliseconds
     */
    fun getMinPing(): Int = if (minPing == Int.MAX_VALUE) 0 else minPing

    /**
     * Get maximum ping in milliseconds
     */
    fun getMaxPing(): Int = maxPing

    /**
     * Get jitter (ping variance) in milliseconds
     */
    fun getJitter(): Int = jitter

    /**
     * Get connection quality rating
     */
    fun getConnectionQuality(): ConnectionQuality {
        return when {
            averagePing < 50 -> ConnectionQuality.EXCELLENT
            averagePing < 100 -> ConnectionQuality.GOOD
            averagePing < 150 -> ConnectionQuality.FAIR
            averagePing < 250 -> ConnectionQuality.POOR
            else -> ConnectionQuality.TERRIBLE
        }
    }

    /**
     * Check if connection is stable (low jitter)
     */
    fun isStable(): Boolean = jitter < 20

    /**
     * Clean up old pending pings (timeout after 5 seconds)
     */
    fun cleanupOldPings() {
        val now = System.currentTimeMillis()
        val timeout = 5000L

        pendingPings.entries.removeIf { (_, sentTime) ->
            now - sentTime > timeout
        }
    }

    /**
     * Reset all statistics
     */
    fun reset() {
        samples.clear()
        pendingPings.clear()
        currentPing = 0
        averagePing = 0
        minPing = Int.MAX_VALUE
        maxPing = 0
        jitter = 0
    }

    /**
     * Get detailed statistics as a string
     */
    fun getStats(): String {
        return """
            Current: ${currentPing}ms
            Average: ${averagePing}ms
            Min: ${getMinPing()}ms
            Max: ${maxPing}ms
            Jitter: ${jitter}ms
            Quality: ${getConnectionQuality()}
            Stable: ${isStable()}
        """.trimIndent()
    }
}

enum class ConnectionQuality {
    EXCELLENT,  // < 50ms
    GOOD,       // 50-100ms
    FAIR,       // 100-150ms
    POOR,       // 150-250ms
    TERRIBLE    // > 250ms
}
