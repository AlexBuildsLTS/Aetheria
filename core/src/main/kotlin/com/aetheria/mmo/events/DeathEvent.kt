package com.aetheria.mmo.events

import com.badlogic.ashley.core.Entity

/**
 * Death Event
 * Fired when an entity dies
 */
data class DeathEvent(
    val entity: Entity,
    val killer: Entity? = null,
    val deathType: DeathType = DeathType.COMBAT
) : GameEvent

enum class DeathType {
    COMBAT,
    ENVIRONMENTAL,
    FALL_DAMAGE,
    VOID,
    SUICIDE,
    DISCONNECT
}
