package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

/**
 * Collision Component
 * Handles collision detection and response
 */
class CollisionComponent : Component, Pool.Poolable {
    var collisionShape: CollisionShape = CollisionShape.SPHERE
    var radius: Float = 0.5f // For sphere/capsule
    var width: Float = 1f // For box
    var height: Float = 2f // For box/capsule
    var depth: Float = 1f // For box

    var isSolid: Boolean = true
    var isTrigger: Boolean = false // Trigger zones don't block movement
    var isStatic: Boolean = false // Static objects don't move

    var collisionLayer: Int = 1
    var collisionMask: Int = -1 // Which layers this collides with

    // Collision callbacks
    val collidingWith = mutableSetOf<Entity>()
    var onCollisionEnter: ((Entity) -> Unit)? = null
    var onCollisionExit: ((Entity) -> Unit)? = null
    var onCollisionStay: ((Entity) -> Unit)? = null

    // Physics properties
    var mass: Float = 1f
    var friction: Float = 0.5f
    var restitution: Float = 0.3f // Bounciness

    fun isCollidingWith(layer: Int): Boolean {
        return (collisionMask and (1 shl layer)) != 0
    }

    override fun reset() {
        collisionShape = CollisionShape.SPHERE
        radius = 0.5f
        width = 1f
        height = 2f
        depth = 1f
        isSolid = true
        isTrigger = false
        isStatic = false
        collisionLayer = 1
        collisionMask = -1
        collidingWith.clear()
        onCollisionEnter = null
        onCollisionExit = null
        onCollisionStay = null
        mass = 1f
        friction = 0.5f
        restitution = 0.3f
    }
}

enum class CollisionShape {
    SPHERE,
    BOX,
    CAPSULE,
    MESH
}
