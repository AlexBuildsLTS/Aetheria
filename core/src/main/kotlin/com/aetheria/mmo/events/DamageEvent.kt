package com.aetheria.mmo.events

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3

/**
 * Damage Event
 * Fired when an entity takes damage
 */
data class DamageEvent(
    val target: Entity,
    val source: Entity?,
    val amount: Float,
    val damageType: DamageType = DamageType.PHYSICAL,
    val position: Vector3? = null,
    val isCritical: Boolean = false
) : GameEvent

enum class DamageType {
    PHYSICAL,
    ENERGY,
    VOID,
    FIRE,
    ICE,
    LIGHTNING,
    POISON,
    TRUE_DAMAGE // Ignores shields/armor
}
