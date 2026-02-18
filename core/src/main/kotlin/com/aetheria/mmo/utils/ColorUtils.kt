package com.aetheria.mmo.utils

import com.badlogic.gdx.graphics.Color

/**
 * Color Utility Functions
 * Provides color manipulation, conversion, and predefined color palettes
 */
object ColorUtils {

    // ==================== Aetheria Color Palette ====================

    // Primary Colors
    val VOID_PURPLE = Color(0.4f, 0.1f, 0.6f, 1f)
    val CYBER_BLUE = Color(0f, 0.8f, 1f, 1f)
    val NEON_PINK = Color(1f, 0.1f, 0.6f, 1f)
    val PLASMA_GREEN = Color(0f, 1f, 0.5f, 1f)

    // UI Colors
    val UI_BACKGROUND = Color(0.05f, 0.05f, 0.1f, 0.9f)
    val UI_PANEL = Color(0.1f, 0.1f, 0.15f, 0.95f)
    val UI_BORDER = Color(0.3f, 0.3f, 0.4f, 1f)
    val UI_TEXT = Color(0.9f, 0.9f, 1f, 1f)
    val UI_TEXT_DISABLED = Color(0.5f, 0.5f, 0.6f, 1f)
    val UI_HIGHLIGHT = Color(0.5f, 0.5f, 1f, 1f)

    // Status Colors
    val HEALTH_COLOR = Color(0f, 1f, 0.3f, 1f)
    val SHIELD_COLOR = Color(0.3f, 0.7f, 1f, 1f)
    val STAMINA_COLOR = Color(1f, 0.8f, 0f, 1f)
    val MANA_COLOR = Color(0.5f, 0.3f, 1f, 1f)
    val ENERGY_COLOR = Color(0f, 1f, 1f, 1f)

    // Damage Type Colors
    val PHYSICAL_DAMAGE = Color(1f, 0.5f, 0f, 1f)
    val FIRE_DAMAGE = Color(1f, 0.3f, 0f, 1f)
    val ICE_DAMAGE = Color(0.3f, 0.8f, 1f, 1f)
    val LIGHTNING_DAMAGE = Color(0.8f, 0.8f, 1f, 1f)
    val POISON_DAMAGE = Color(0.5f, 1f, 0f, 1f)
    val VOID_DAMAGE = Color(0.6f, 0f, 0.8f, 1f)

    // Rarity Colors
    val COMMON = Color(0.7f, 0.7f, 0.7f, 1f)
    val UNCOMMON = Color(0.3f, 1f, 0.3f, 1f)
    val RARE = Color(0.3f, 0.5f, 1f, 1f)
    val EPIC = Color(0.7f, 0.3f, 1f, 1f)
    val LEGENDARY = Color(1f, 0.6f, 0f, 1f)
    val MYTHIC = Color(1f, 0.3f, 0.3f, 1f)

    // Faction Colors
    val FACTION_ALLIANCE = Color(0.2f, 0.5f, 1f, 1f)
    val FACTION_HORDE = Color(1f, 0.2f, 0.2f, 1f)
    val FACTION_NEUTRAL = Color(0.8f, 0.8f, 0.5f, 1f)

    /**
     * Lerp between two colors
     */
    fun lerp(start: Color, end: Color, alpha: Float): Color {
        return Color(start).lerp(end, alpha.coerceIn(0f, 1f))
    }

    /**
     * Create color from hex string (e.g., "#FF00FF" or "FF00FF")
     */
    fun fromHex(hex: String): Color {
        val cleanHex = hex.removePrefix("#")
        return Color(
            cleanHex.substring(0, 2).toInt(16) / 255f,
            cleanHex.substring(2, 4).toInt(16) / 255f,
            cleanHex.substring(4, 6).toInt(16) / 255f,
            if (cleanHex.length == 8) cleanHex.substring(6, 8).toInt(16) / 255f else 1f
        )
    }

    /**
     * Convert color to hex string
     */
    fun toHex(color: Color): String {
        val r = (color.r * 255).toInt().coerceIn(0, 255)
        val g = (color.g * 255).toInt().coerceIn(0, 255)
        val b = (color.b * 255).toInt().coerceIn(0, 255)
        val a = (color.a * 255).toInt().coerceIn(0, 255)
        return String.format("#%02X%02X%02X%02X", r, g, b, a)
    }

    /**
     * Brighten color by a factor
     */
    fun brighten(color: Color, factor: Float): Color {
        return Color(
            (color.r * (1f + factor)).coerceIn(0f, 1f),
            (color.g * (1f + factor)).coerceIn(0f, 1f),
            (color.b * (1f + factor)).coerceIn(0f, 1f),
            color.a
        )
    }

    /**
     * Darken color by a factor
     */
    fun darken(color: Color, factor: Float): Color {
        return Color(
            (color.r * (1f - factor)).coerceIn(0f, 1f),
            (color.g * (1f - factor)).coerceIn(0f, 1f),
            (color.b * (1f - factor)).coerceIn(0f, 1f),
            color.a
        )
    }

    /**
     * Set alpha channel
     */
    fun withAlpha(color: Color, alpha: Float): Color {
        return Color(color.r, color.g, color.b, alpha.coerceIn(0f, 1f))
    }

    /**
     * Get color for health percentage
     */
    fun getHealthColor(healthPercent: Float): Color {
        return when {
            healthPercent > 0.6f -> HEALTH_COLOR
            healthPercent > 0.3f -> Color.YELLOW
            else -> Color.RED
        }
    }

    /**
     * Get color for damage type
     */
    fun getDamageTypeColor(damageType: String): Color {
        return when (damageType.lowercase()) {
            "physical" -> PHYSICAL_DAMAGE
            "fire" -> FIRE_DAMAGE
            "ice", "frost" -> ICE_DAMAGE
            "lightning", "electric" -> LIGHTNING_DAMAGE
            "poison", "toxic" -> POISON_DAMAGE
            "void", "dark" -> VOID_DAMAGE
            else -> Color.WHITE
        }
    }

    /**
     * Get color for item rarity
     */
    fun getRarityColor(rarity: String): Color {
        return when (rarity.lowercase()) {
            "common" -> COMMON
            "uncommon" -> UNCOMMON
            "rare" -> RARE
            "epic" -> EPIC
            "legendary" -> LEGENDARY
            "mythic" -> MYTHIC
            else -> COMMON
        }
    }

    /**
     * Create gradient color based on value (0.0 to 1.0)
     */
    fun gradient(value: Float, colorStart: Color, colorEnd: Color): Color {
        return lerp(colorStart, colorEnd, value.coerceIn(0f, 1f))
    }

    /**
     * Create rainbow color based on time
     */
    fun rainbow(time: Float, saturation: Float = 1f, brightness: Float = 1f): Color {
        val hue = (time % 1f) * 360f
        return fromHSV(hue, saturation, brightness)
    }

    /**
     * Convert HSV to RGB color
     */
    fun fromHSV(hue: Float, saturation: Float, value: Float): Color {
        val h = hue / 60f
        val s = saturation.coerceIn(0f, 1f)
        val v = value.coerceIn(0f, 1f)

        val c = v * s
        val x = c * (1f - kotlin.math.abs((h % 2f) - 1f))
        val m = v - c

        val (r, g, b) = when (h.toInt()) {
            0 -> Triple(c, x, 0f)
            1 -> Triple(x, c, 0f)
            2 -> Triple(0f, c, x)
            3 -> Triple(0f, x, c)
            4 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        return Color(r + m, g + m, b + m, 1f)
    }

    /**
     * Get complementary color
     */
    fun complementary(color: Color): Color {
        return Color(1f - color.r, 1f - color.g, 1f - color.b, color.a)
    }

    /**
     * Mix two colors
     */
    fun mix(color1: Color, color2: Color, ratio: Float = 0.5f): Color {
        return lerp(color1, color2, ratio)
    }

    /**
     * Desaturate color (convert to grayscale)
     */
    fun desaturate(color: Color, amount: Float = 1f): Color {
        val gray = color.r * 0.299f + color.g * 0.587f + color.b * 0.114f
        return lerp(color, Color(gray, gray, gray, color.a), amount.coerceIn(0f, 1f))
    }

    /**
     * Create pulsing color effect
     */
    fun pulse(baseColor: Color, time: Float, speed: Float = 1f, intensity: Float = 0.3f): Color {
        val pulse = (kotlin.math.sin(time * speed) + 1f) / 2f
        return lerp(darken(baseColor, intensity), brighten(baseColor, intensity), pulse)
    }

    /**
     * Get random color
     */
    fun random(): Color {
        return Color(
            MathUtils.random(0f, 1f),
            MathUtils.random(0f, 1f),
            MathUtils.random(0f, 1f),
            1f
        )
    }

    /**
     * Get random pastel color
     */
    fun randomPastel(): Color {
        return Color(
            MathUtils.random(0.5f, 1f),
            MathUtils.random(0.5f, 1f),
            MathUtils.random(0.5f, 1f),
            1f
        )
    }
}
