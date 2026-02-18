package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

/**
 * Network Component
 * Tracks network synchronization state for multiplayer entities
 */
class NetworkComponent : Component, Pool.Poolable {
    var networkId: String = ""
    var ownerId: String = "" // Player who owns this entity
    var isLocalPlayer: Boolean = false
    var isNetworked: Boolean = true

    // Interpolation for smooth movement
    var lastPosition: Vector3 = Vector3()
    var targetPosition: Vector3 = Vector3()
    var interpolationAlpha: Float = 0f

    // Timestamp tracking
    var lastUpdateTime: Long = 0L
    var updateInterval: Float = 0.05f // 20 updates per second

    // Authority
    var hasAuthority: Boolean = false // Can this client modify this entity?

    override fun reset() {
        networkId = ""
        ownerId = ""
        isLocalPlayer = false
        isNetworked = true
        lastPosition.setZero()
        targetPosition.setZero()
        interpolationAlpha = 0f
        lastUpdateTime = 0L
        updateInterval = 0.05f
        hasAuthority = false
    }
}
