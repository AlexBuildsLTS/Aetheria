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

    // Flee behavior parameters
    var fleeDistance: Float = 10f

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
        fleeDistance = 10f
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
