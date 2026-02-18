package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

/**
 * Steering Component
 * AI steering behaviors for autonomous movement (seek, flee, wander, etc.)
 */
class SteeringComponent : Component, Pool.Poolable {
    var targetPosition: Vector3? = null
    var steeringBehavior: SteeringBehaviorType = SteeringBehaviorType.NONE
    var maxLinearSpeed: Float = 5f
    var maxLinearAcceleration: Float = 10f
    var maxAngularSpeed: Float = 5f
    var maxAngularAcceleration: Float = 10f
    var isEnabled: Boolean = true
    var arrivalTolerance: Float = 0.5f
    var decelerationRadius: Float = 3f

    // Wander behavior parameters
    var wanderRadius: Float = 5f
    var wanderDistance: Float = 10f
    var wanderAngle: Float = 0f
    var wanderJitter: Float = 10f

    // Flee behavior parameters
    var fleeDistance: Float = 10f

    // Additional properties for SteeringSystem
    var seekTarget: Vector3? = null
    var fleeTarget: Vector3? = null
    var arriveTarget: Vector3? = null
    var arriveRadius: Float = 2f
    var isWandering: Boolean = false
    var maxSpeed: Float = 5f
    var maxForce: Float = 10f
    val facingDirection = Vector3(0f, 0f, 1f)

    override fun reset() {
        targetPosition = null
        steeringBehavior = SteeringBehaviorType.NONE
        maxLinearSpeed = 5f
        maxLinearAcceleration = 10f
        maxAngularSpeed = 5f
        maxAngularAcceleration = 10f
        isEnabled = true
        arrivalTolerance = 0.5f
        decelerationRadius = 3f
        wanderRadius = 5f
        wanderDistance = 10f
        wanderAngle = 0f
        wanderJitter = 10f
        fleeDistance = 10f
        seekTarget = null
        fleeTarget = null
        arriveTarget = null
        arriveRadius = 2f
        isWandering = false
        maxSpeed = 5f
        maxForce = 10f
        facingDirection.set(0f, 0f, 1f)
    }
}

/**
 * Steering Behavior Types
 */
enum class SteeringBehaviorType {
    NONE,
    SEEK,           // Move towards target
    FLEE,           // Move away from target
    ARRIVE,         // Move towards target and slow down
    WANDER,         // Random wandering
    PURSUE,         // Predict and chase moving target
    EVADE,          // Predict and flee from moving target
    PATROL          // Follow waypoints
}
