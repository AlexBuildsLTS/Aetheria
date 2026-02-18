package com.aetheria.mmo.utils

/**
 * Z-Order Constants
 * Defines rendering layers for proper depth sorting in 3D space
 * Lower values render first (background), higher values render last (foreground)
 */
object ZOrder {
    // Background layers
    const val SKYBOX = 0
    const val BACKGROUND = 10
    const val TERRAIN = 20

    // Environment layers
    const val ENVIRONMENT_STATIC = 30
    const val ENVIRONMENT_DYNAMIC = 40
    const val VEGETATION = 50

    // Entity layers
    const val ITEMS_GROUND = 60
    const val DECALS = 70
    const val SHADOWS = 80
    const val ENTITIES = 100
    const val PLAYERS = 110
    const val NPCS = 105
    const val ENEMIES = 108

    // Effects layers
    const val PROJECTILES = 120
    const val PARTICLES_LOW = 130
    const val PARTICLES_MID = 140
    const val PARTICLES_HIGH = 150

    // UI layers (rendered after 3D scene)
    const val UI_BACKGROUND = 200
    const val UI_ELEMENTS = 210
    const val UI_TOOLTIPS = 220
    const val UI_OVERLAY = 230
    const val UI_DEBUG = 240

    /**
     * Get Z-order for entity type
     */
    fun forEntityType(type: String): Int {
        return when (type.lowercase()) {
            "player" -> PLAYERS
            "npc" -> NPCS
            "enemy", "monster" -> ENEMIES
            "projectile" -> PROJECTILES
            "item" -> ITEMS_GROUND
            "particle" -> PARTICLES_MID
            "environment" -> ENVIRONMENT_STATIC
            else -> ENTITIES
        }
    }

    /**
     * Check if layer A should render before layer B
     */
    fun shouldRenderBefore(layerA: Int, layerB: Int): Boolean {
        return layerA < layerB
    }
}
