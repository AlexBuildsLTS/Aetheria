package com.aetheria.mmo.utils

import com.badlogic.gdx.Gdx

/**
 * Logger Utility
 * Provides structured logging with different severity levels
 * Wraps LibGDX logging with additional features like tagging and formatting
 */
object Logger {

    private const val DEFAULT_TAG = "Aetheria"

    // Log levels
    enum class Level(val value: Int) {
        DEBUG(0),
        INFO(1),
        WARN(2),
        ERROR(3),
        NONE(4)
    }

    var logLevel: Level = Level.DEBUG
    var enableTimestamps: Boolean = true
    var enableColors: Boolean = true

    /**
     * Debug log (verbose information for development)
     */
    fun debug(tag: String = DEFAULT_TAG, message: String) {
        if (logLevel.value <= Level.DEBUG.value) {
            val formatted = formatMessage(tag, message, "DEBUG")
            Gdx.app.debug(tag, formatted)
        }
    }

    /**
     * Info log (general information)
     */
    fun info(tag: String = DEFAULT_TAG, message: String) {
        if (logLevel.value <= Level.INFO.value) {
            val formatted = formatMessage(tag, message, "INFO")
            Gdx.app.log(tag, formatted)
        }
    }

    /**
     * Warning log (potential issues)
     */
    fun warn(tag: String = DEFAULT_TAG, message: String) {
        if (logLevel.value <= Level.WARN.value) {
            val formatted = formatMessage(tag, message, "WARN")
            Gdx.app.error(tag, formatted)
        }
    }

    /**
     * Error log (critical issues)
     */
    fun error(tag: String = DEFAULT_TAG, message: String, exception: Throwable? = null) {
        if (logLevel.value <= Level.ERROR.value) {
            val formatted = formatMessage(tag, message, "ERROR")
            if (exception != null) {
                Gdx.app.error(tag, formatted, exception)
            } else {
                Gdx.app.error(tag, formatted)
            }
        }
    }

    /**
     * Network log (network-related events)
     */
    fun network(message: String) {
        debug("Network", message)
    }

    /**
     * System log (ECS system events)
     */
    fun system(systemName: String, message: String) {
        debug("System:$systemName", message)
    }

    /**
     * Entity log (entity lifecycle events)
     */
    fun entity(message: String) {
        debug("Entity", message)
    }

    /**
     * Performance log (performance metrics)
     */
    fun performance(message: String) {
        debug("Performance", message)
    }

    /**
     * Audio log (audio system events)
     */
    fun audio(message: String) {
        debug("Audio", message)
    }

    /**
     * Resource log (asset loading events)
     */
    fun resource(message: String) {
        debug("Resource", message)
    }

    /**
     * UI log (UI events)
     */
    fun ui(message: String) {
        debug("UI", message)
    }

    /**
     * Format log message with timestamp and level
     */
    private fun formatMessage(tag: String, message: String, level: String): String {
        val timestamp = if (enableTimestamps) {
            "[${TimeUtils.formatTime(TimeUtils.now())}] "
        } else {
            ""
        }
        return "$timestamp[$level] $message"
    }

    /**
     * Log method entry (for debugging)
     */
    fun enter(className: String, methodName: String) {
        debug(className, "→ $methodName()")
    }

    /**
     * Log method exit (for debugging)
     */
    fun exit(className: String, methodName: String) {
        debug(className, "← $methodName()")
    }

    /**
     * Log with custom format
     */
    fun log(tag: String = DEFAULT_TAG, level: Level, message: String) {
        when (level) {
            Level.DEBUG -> debug(tag, message)
            Level.INFO -> info(tag, message)
            Level.WARN -> warn(tag, message)
            Level.ERROR -> error(tag, message)
            Level.NONE -> {} // No logging
        }
    }

    /**
     * Assert condition and log error if false
     */
    fun assert(condition: Boolean, tag: String = DEFAULT_TAG, message: String) {
        if (!condition) {
            error(tag, "Assertion failed: $message")
        }
    }

    /**
     * Log separator line (for visual organization)
     */
    fun separator(tag: String = DEFAULT_TAG) {
        debug(tag, "═".repeat(60))
    }

    /**
     * Log header (for section organization)
     */
    fun header(tag: String = DEFAULT_TAG, title: String) {
        separator(tag)
        info(tag, title)
        separator(tag)
    }
}
