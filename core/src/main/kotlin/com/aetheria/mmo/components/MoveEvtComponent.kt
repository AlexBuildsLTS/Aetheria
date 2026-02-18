package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

/**
 * Movement Event Component
 * Queues movement commands for processing by MovementSystem
 */
class MoveEvtComponent : Component, Pool.Poolable {
    var targetPosition: Vector3? = null
    var moveDirection: Vector3 = Vector3()
    var moveSpeed: Float = 5f
    var isMoving: Boolean = false
    var isSprinting: Boolean = false
    var sprintMultiplier: Float = 1.5f

    fun setMoveDirection(x: Float, y: Float, z: Float) {
        moveDirection.set(x, y, z)
        isMoving = moveDirection.len2() > 0.01f
    }

    fun getCurrentSpeed(): Float {
        return if (isSprinting) moveSpeed * sprintMultiplier else moveSpeed
    }

    override fun reset() {
        targetPosition = null
        moveDirection.setZero()
        moveSpeed = 5f
        isMoving = false
        isSprinting = false
        sprintMultiplier = 1.5f
    }
}
