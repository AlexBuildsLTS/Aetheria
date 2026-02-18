package com.aetheria.mmo.events

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3

/**
 * Skill Cast Event
 * Fired when an entity casts a skill/ability
 */
data class SkillCastEvent(
    val caster: Entity,
    val skillId: String,
    val target: Entity? = null,
    val targetPosition: Vector3? = null,
    val manaCost: Float = 0f,
    val cooldown: Float = 0f
) : GameEvent
