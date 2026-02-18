package com.aetheria.mmo.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Time Utility Functions
 * Provides time-related helper functions for game timing, cooldowns, and formatting
 */
object TimeUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

    /**
     * Get current time in milliseconds
     */
    fun now(): Long = System.currentTimeMillis()

    /**
     * Get current time in seconds
     */
    fun nowSeconds(): Long = System.currentTimeMillis() / 1000L

    /**
     * Convert milliseconds to seconds
     */
    fun millisToSeconds(millis: Long): Float = millis / 1000f

    /**
     * Convert seconds to milliseconds
     */
    fun secondsToMillis(seconds: Float): Long = (seconds * 1000f).toLong()

    /**
     * Check if a cooldown has expired
     */
    fun hasCooldownExpired(lastTime: Long, cooldownMs: Long): Boolean {
        return now() - lastTime >= cooldownMs
    }

    /**
     * Get remaining cooldown time in seconds
     */
    fun getRemainingCooldown(lastTime: Long, cooldownMs: Long): Float {
        val elapsed = now() - lastTime
        val remaining = cooldownMs - elapsed
        return if (remaining > 0) remaining / 1000f else 0f
    }

    /**
     * Get cooldown progress (0.0 to 1.0)
     */
    fun getCooldownProgress(lastTime: Long, cooldownMs: Long): Float {
        val elapsed = now() - lastTime
        return (elapsed.toFloat() / cooldownMs.toFloat()).coerceIn(0f, 1f)
    }

    /**
     * Format milliseconds to readable time string (e.g., "1h 23m 45s")
     */
    fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

        return when {
            hours > 0 -> "${hours}h ${minutes}m ${seconds}s"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }

    /**
     * Format milliseconds to short time string (e.g., "1:23:45")
     */
    fun formatDurationShort(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%d:%02d", minutes, seconds)
        }
    }

    /**
     * Format timestamp to date string
     */
    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    /**
     * Format timestamp to time string
     */
    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    /**
     * Get time ago string (e.g., "5 minutes ago", "2 hours ago")
     */
    fun getTimeAgo(timestamp: Long): String {
        val diff = now() - timestamp
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            days > 0 -> if (days == 1L) "1 day ago" else "$days days ago"
            hours > 0 -> if (hours == 1L) "1 hour ago" else "$hours hours ago"
            minutes > 0 -> if (minutes == 1L) "1 minute ago" else "$minutes minutes ago"
            seconds > 5 -> "$seconds seconds ago"
            else -> "just now"
        }
    }

    /**
     * Check if timestamp is today
     */
    fun isToday(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        calendar.timeInMillis = timestamp
        val thatDay = calendar.get(Calendar.DAY_OF_YEAR)
        return today == thatDay
    }

    /**
     * Get start of day timestamp
     */
    fun getStartOfDay(timestamp: Long = now()): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * Get end of day timestamp
     */
    fun getEndOfDay(timestamp: Long = now()): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    /**
     * Smooth delta time to prevent spikes
     */
    fun smoothDeltaTime(deltaTime: Float, maxDelta: Float = 0.1f): Float {
        return deltaTime.coerceAtMost(maxDelta)
    }

    /**
     * Convert frame rate to delta time
     */
    fun fpsToDeltatime(fps: Int): Float {
        return if (fps > 0) 1f / fps else 0.016f
    }

    /**
     * Convert delta time to frame rate
     */
    fun deltaTimeToFps(deltaTime: Float): Int {
        return if (deltaTime > 0) (1f / deltaTime).toInt() else 60
    }
}
