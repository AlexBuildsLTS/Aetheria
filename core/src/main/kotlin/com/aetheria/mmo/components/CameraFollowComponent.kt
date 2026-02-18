package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

/**
 * Camera Follow Component
 * Makes camera follow this entity with smooth interpolation
 */
class CameraFollowComponent : Component, Pool.Poolable {
    var offset = Vector3(0f, 5f, -10f) // Camera offset from target
    var lookAtOffset = Vector3(0f, 1.5f, 0f) // Where camera looks (character head height)
    var followSpeed: Float = 5f // Lerp speed for smooth following
    var rotationSpeed: Float = 3f // Camera rotation speed

    var minDistance: Float = 3f
    var maxDistance: Float = 20f
    var currentDistance: Float = 10f

    var minPitch: Float = -80f // Degrees
    var maxPitch: Float = 80f // Degrees
    var currentPitch: Float = 0f
    var currentYaw: Float = 0f

    var isThirdPerson: Boolean = true
    var enableCollision: Boolean = true // Pull camera closer if blocked
    var smoothDampVelocity = Vector3()

    override fun reset() {
        offset.set(0f, 5f, -10f)
        lookAtOffset.set(0f, 1.5f, 0f)
        followSpeed = 5f
        rotationSpeed = 3f
        minDistance = 3f
        maxDistance = 20f
        currentDistance = 10f
        minPitch = -80f
        maxPitch = 80f
        currentPitch = 0f
        currentYaw = 0f
        isThirdPerson = true
        enableCollision = true
        smoothDampVelocity.setZero()
    }
}
