package com.aetheria.mmo.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*
import com.aetheria.mmo.utils.clampLength
import com.aetheria.mmo.utils.directionTo

/**
 * Steering System
 * Implements steering behaviors for AI entities (seek, flee, wander, etc.)
 * Based on Craig Reynolds' steering behaviors
 */
class SteeringSystem : IteratingSystem(
    Family.all(SteeringComponent::class.java, TransformComponent::class.java, VelocityComponent::class.java).get()
) {

    private val steeringMapper = ComponentMapper.getFor(SteeringComponent::class.java)
    private val transformMapper = ComponentMapper.getFor(TransformComponent::class.java)
    private val velocityMapper = ComponentMapper.getFor(VelocityComponent::class.java)

    private val tempVec = Vector3()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val steering = steeringMapper.get(entity)
        val transform = transformMapper.get(entity)
        val velocity = velocityMapper.get(entity)

        // Calculate steering force based on active behaviors
        val steeringForce = Vector3.Zero.cpy()

        if (steering.seekTarget != null) {
            steeringForce.add(seek(transform.position, velocity.linear, steering.seekTarget!!, steering.maxSpeed))
        }

        if (steering.fleeTarget != null) {
            steeringForce.add(flee(transform.position, velocity.linear, steering.fleeTarget!!, steering.maxSpeed))
        }

        if (steering.isWandering) {
            steeringForce.add(wander(transform.position, velocity.linear, steering, deltaTime))
        }

        if (steering.arriveTarget != null) {
            steeringForce.add(arrive(transform.position, velocity.linear, steering.arriveTarget!!, steering.maxSpeed, steering.arriveRadius))
        }

        // Apply steering force
        steeringForce.clampLength(steering.maxForce)
        velocity.linear.add(steeringForce.scl(deltaTime))
        velocity.linear.clampLength(steering.maxSpeed)

        // Update facing direction
        if (velocity.linear.len2() > 0.01f) {
            steering.facingDirection.set(velocity.linear).nor()
        }
    }

    /**
     * Seek behavior - move towards target
     */
    private fun seek(position: Vector3, velocity: Vector3, target: Vector3, maxSpeed: Float): Vector3 {
        val desired = position.directionTo(target).scl(maxSpeed)
        return desired.sub(velocity)
    }

    /**
     * Flee behavior - move away from target
     */
    private fun flee(position: Vector3, velocity: Vector3, target: Vector3, maxSpeed: Float): Vector3 {
        val desired = target.directionTo(position).scl(maxSpeed)
        return desired.sub(velocity)
    }

    /**
     * Arrive behavior - slow down when approaching target
     */
    private fun arrive(position: Vector3, velocity: Vector3, target: Vector3, maxSpeed: Float, slowingRadius: Float): Vector3 {
        val toTarget = Vector3(target).sub(position)
        val distance = toTarget.len()

        if (distance < 0.1f) {
            return Vector3.Zero
        }

        val speed = if (distance < slowingRadius) {
            maxSpeed * (distance / slowingRadius)
        } else {
            maxSpeed
        }

        val desired = toTarget.nor().scl(speed)
        return desired.sub(velocity)
    }

    /**
     * Wander behavior - random wandering
     */
    private fun wander(position: Vector3, velocity: Vector3, steering: SteeringComponent, deltaTime: Float): Vector3 {
        // Update wander angle
        steering.wanderAngle += (Math.random().toFloat() - 0.5f) * steering.wanderJitter * deltaTime

        // Calculate wander target
        val circleCenter = Vector3(velocity).nor().scl(steering.wanderDistance)
        val displacement = Vector3(
            kotlin.math.cos(steering.wanderAngle) * steering.wanderRadius,
            0f,
            kotlin.math.sin(steering.wanderAngle) * steering.wanderRadius
        )

        val wanderTarget = Vector3(position).add(circleCenter).add(displacement)
        return seek(position, velocity, wanderTarget, steering.maxSpeed)
    }

    /**
     * Pursuit behavior - predict target's future position
     */
    private fun pursuit(position: Vector3, velocity: Vector3, targetPos: Vector3, targetVel: Vector3, maxSpeed: Float): Vector3 {
        val distance = position.dst(targetPos)
        val prediction = distance / maxSpeed

        val futurePos = Vector3(targetPos).add(Vector3(targetVel).scl(prediction))
        return seek(position, velocity, futurePos, maxSpeed)
    }

    /**
     * Evade behavior - predict and flee from target
     */
    private fun evade(position: Vector3, velocity: Vector3, targetPos: Vector3, targetVel: Vector3, maxSpeed: Float): Vector3 {
        val distance = position.dst(targetPos)
        val prediction = distance / maxSpeed

        val futurePos = Vector3(targetPos).add(Vector3(targetVel).scl(prediction))
        return flee(position, velocity, futurePos, maxSpeed)
    }

    /**
     * Set seek target
     */
    fun setSeekTarget(entity: Entity, target: Vector3) {
        val steering = steeringMapper.get(entity) ?: return
        steering.seekTarget = target.cpy()
    }

    /**
     * Set flee target
     */
    fun setFleeTarget(entity: Entity, target: Vector3) {
        val steering = steeringMapper.get(entity) ?: return
        steering.fleeTarget = target.cpy()
    }

    /**
     * Set arrive target
     */
    fun setArriveTarget(entity: Entity, target: Vector3, radius: Float = 2f) {
        val steering = steeringMapper.get(entity) ?: return
        steering.arriveTarget = target.cpy()
        steering.arriveRadius = radius
    }

    /**
     * Enable wandering
     */
    fun enableWander(entity: Entity, enable: Boolean = true) {
        val steering = steeringMapper.get(entity) ?: return
        steering.isWandering = enable
    }

    /**
     * Clear all steering targets
     */
    fun clearTargets(entity: Entity) {
        val steering = steeringMapper.get(entity) ?: return
        steering.seekTarget = null
        steering.fleeTarget = null
        steering.arriveTarget = null
        steering.isWandering = false
    }
}
