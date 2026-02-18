package com.aetheria.mmo.events

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3

/**
 * Spawn Event
 * Fired when an entity spawns in the world
 */
data class SpawnEvent(
    val entity: Entity,
    val position: Vector3,
    val spawnType: SpawnType = SpawnType.NORMAL
) : GameEvent

enum class SpawnType {
    NORMAL,
    RESPAWN,
    TELEPORT,
    SUMMON,
    BOSS,
    NETWORK_SYNC
}
