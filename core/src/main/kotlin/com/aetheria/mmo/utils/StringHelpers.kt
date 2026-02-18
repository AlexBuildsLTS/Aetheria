package com.aetheria.mmo.utils

import java.util.*

/**
 * String Helper Functions
 * Provides utility functions for string manipulation, formatting, and validation
 */
object StringHelpers {

    /**
     * Capitalize first letter of each word
     */
    fun titleCase(text: String): String {
        return text.split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    }

    /**
     * Truncate string with ellipsis
     */
    fun truncate(text: String, maxLength: Int, ellipsis: String = "..."): String {
        return if (text.length > maxLength) {
            text.take(maxLength - ellipsis.length) + ellipsis
        } else {
            text
        }
    }

    /**
     * Format number with commas (e.g., 1000000 -> "1,000,000")
     */
    fun formatNumber(number: Int): String {
        return String.format(Locale.US, "%,d", number)
    }

    /**
     * Format number with commas (long version)
     */
    fun formatNumber(number: Long): String {
        return String.format(Locale.US, "%,d", number)
    }

    /**
     * Format large numbers with suffixes (e.g., 1000 -> "1K", 1000000 -> "1M")
     */
    fun formatLargeNumber(number: Long): String {
        return when {
            number >= 1_000_000_000_000 -> String.format("%.1fT", number / 1_000_000_000_000.0)
            number >= 1_000_000_000 -> String.format("%.1fB", number / 1_000_000_000.0)
            number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
            number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
            else -> number.toString()
        }
    }

    /**
     * Format percentage (e.g., 0.75 -> "75%")
     */
    fun formatPercentage(value: Float, decimals: Int = 0): String {
        return String.format("%.${decimals}f%%", value * 100)
    }

    /**
     * Format decimal number
     */
    fun formatDecimal(value: Float, decimals: Int = 2): String {
        return String.format("%.${decimals}f", value)
    }

    /**
     * Validate username (alphanumeric, 3-16 characters)
     */
    fun isValidUsername(username: String): Boolean {
        return username.matches(Regex("^[a-zA-Z0-9_]{3,16}$"))
    }

    /**
     * Validate email address
     */
    fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
    }

    /**
     * Validate password (min 8 chars, at least one letter and one number)
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
               password.any { it.isLetter() } &&
               password.any { it.isDigit() }
    }

    /**
     * Remove color codes from text (e.g., "[#FF0000]Red Text[]" -> "Red Text")
     */
    fun stripColorCodes(text: String): String {
        return text.replace(Regex("\\[#[0-9A-Fa-f]{6}\\]|\\[\\]"), "")
    }

    /**
     * Wrap text to fit within a certain width (character count)
     */
    fun wrapText(text: String, maxWidth: Int): String {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            if ((currentLine + word).length > maxWidth) {
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine.trim())
                    currentLine = ""
                }
            }
            currentLine += "$word "
        }

        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.trim())
        }

        return lines.joinToString("\n")
    }

    /**
     * Generate random alphanumeric string
     */
    fun randomString(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    /**
     * Convert camelCase to snake_case
     */
    fun camelToSnake(text: String): String {
        return text.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
    }

    /**
     * Convert snake_case to camelCase
     */
    fun snakeToCamel(text: String): String {
        return text.split("_").mapIndexed { index, word ->
            if (index == 0) word else word.replaceFirstChar { it.uppercase() }
        }.joinToString("")
    }

    /**
     * Pluralize word based on count
     */
    fun pluralize(word: String, count: Int): String {
        return if (count == 1) word else "${word}s"
    }

    /**
     * Format with count (e.g., "5 items", "1 item")
     */
    fun formatWithCount(word: String, count: Int): String {
        return "$count ${pluralize(word, count)}"
    }

    /**
     * Sanitize input (remove special characters)
     */
    fun sanitize(text: String): String {
        return text.replace(Regex("[^a-zA-Z0-9 ]"), "")
    }

    /**
     * Check if string contains only whitespace
     */
    fun isBlank(text: String?): Boolean {
        return text.isNullOrBlank()
    }

    /**
     * Get initials from name (e.g., "John Doe" -> "JD")
     */
    fun getInitials(name: String): String {
        return name.split(" ")
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .take(2)
            .joinToString("")
    }

    /**
     * Highlight search term in text
     */
    fun highlightSearch(text: String, searchTerm: String, highlightColor: String = "#FFFF00"): String {
        if (searchTerm.isEmpty()) return text
        return text.replace(
            Regex("($searchTerm)", RegexOption.IGNORE_CASE),
            "[#$highlightColor]$1[]"
        )
    }

    /**
     * Convert bytes to human-readable size
     */
    fun formatBytes(bytes: Long): String {
        return when {
            bytes >= 1_073_741_824 -> String.format("%.2f GB", bytes / 1_073_741_824.0)
            bytes >= 1_048_576 -> String.format("%.2f MB", bytes / 1_048_576.0)
            bytes >= 1_024 -> String.format("%.2f KB", bytes / 1_024.0)
            else -> "$bytes B"
        }
    }
}
