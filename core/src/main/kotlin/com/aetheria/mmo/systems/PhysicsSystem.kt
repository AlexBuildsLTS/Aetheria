package com.aetheria.mmo.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*
import com.aetheria.mmo.utils.Constants
import com.aetheria.mmo.utils.Logger

/**
 * Physics System
 * Handles physics simulation including gravity, velocity, and collision response
 * Uses semi-implicit Euler integration for stability
 */
class PhysicsSystem : IteratingSystem(
    Family.all(TransformComponent::class.java, VelocityComponent::class.java).get()
) {

    private val transformMapper = ComponentMapper.getFor(TransformComponent::class.java)
    private val velocityMapper = ComponentMapper.getFor(VelocityComponent::class.java)
    private val collisionMapper = ComponentMapper.getFor(CollisionComponent::class.java)
    private val moveMapper = ComponentMapper.getFor(MoveEvtComponent::class.java)

    private val gravity = Vector3(0f, Constants.GRAVITY, 0f)
    private val tempVec = Vector3()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = transformMapper.get(entity)
        val velocity = velocityMapper.get(entity)
        val collision = collisionMapper.get(entity)

        // Clamp delta time to prevent physics explosions
        val dt = deltaTime.coerceAtMost(Constants.MAX_PHYSICS_DELTA)

        // Apply gravity if not static
        if (collision == null || !collision.isStatic) {
            applyGravity(velocity, dt)
        }

        // Apply friction
        applyFriction(velocity, collision, dt)

        // Clamp terminal velocity
        clampVelocity(velocity)

        // Update position using velocity (semi-implicit Euler)
        tempVec.set(velocity.linear).scl(dt)
        transform.position.add(tempVec)

        // Apply angular velocity (rotation)
        if (velocity.angular.len2() > 0.0001f) {
            tempVec.set(velocity.angular).scl(dt)
            // Simple rotation update - in a real implementation you'd use quaternions properly
            val currentYaw = transform.rotation.yaw
            val currentPitch = transform.rotation.pitch
            val currentRoll = transform.rotation.roll
            transform.rotation.setEulerAngles(
                currentYaw + tempVec.y,
                currentPitch + tempVec.x,
                currentRoll + tempVec.z
            )
        }

        // Ground check (simple Y-axis check)
        if (transform.position.y <= 0f) {
            transform.position.y = 0f
            velocity.linear.y = 0f
            velocity.isGrounded = true
        } else {
            velocity.isGrounded = false
        }
    }

    /**
     * Apply gravity to velocity
     */
    private fun applyGravity(velocity: VelocityComponent, deltaTime: Float) {
        if (!velocity.isGrounded) {
            velocity.linear.y += gravity.y * deltaTime
        }
    }

    /**
     * Apply friction to velocity
     */
    private fun applyFriction(velocity: VelocityComponent, collision: CollisionComponent?, deltaTime: Float) {
        val friction = if (velocity.isGrounded) {
            Constants.GROUND_FRICTION
        } else {
            Constants.AIR_FRICTION
        }

        // Apply friction to horizontal movement
        velocity.linear.x *= friction
        velocity.linear.z *= friction

        // Apply angular friction
        velocity.angular.scl(0.9f)
    }

    /**
     * Clamp velocity to terminal velocity
     */
    private fun clampVelocity(velocity: VelocityComponent) {
        // Clamp downward velocity (terminal velocity)
        if (velocity.linear.y < Constants.TERMINAL_VELOCITY) {
            velocity.linear.y = Constants.TERMINAL_VELOCITY
        }

        // Clamp horizontal velocity if needed
        val horizontalSpeed = Math.sqrt(
            (velocity.linear.x * velocity.linear.x + velocity.linear.z * velocity.linear.z).toDouble()
        ).toFloat()

        if (horizontalSpeed > 50f) { // Max horizontal speed
            val scale = 50f / horizontalSpeed
            velocity.linear.x *= scale
            velocity.linear.z *= scale
        }
    }

    /**
     * Apply impulse to entity
     */
    fun applyImpulse(entity: Entity, impulse: Vector3) {
        val velocity = velocityMapper.get(entity) ?: return
        velocity.linear.add(impulse)
    }

    /**
     * Apply force to entity (force = mass * acceleration)
     */
    fun applyForce(entity: Entity, force: Vector3, deltaTime: Float) {
        val velocity = velocityMapper.get(entity) ?: return
        val collision = collisionMapper.get(entity)
        val mass = collision?.mass ?: 1f

        // F = ma, therefore a = F/m
        tempVec.set(force).scl(1f / mass * deltaTime)
        velocity.linear.add(tempVec)
    }

    /**
     * Set velocity directly
     */
    fun setVelocity(entity: Entity, velocity: Vector3) {
        val vel = velocityMapper.get(entity) ?: return
        vel.linear.set(velocity)
    }

    /**
     * Stop entity movement
     */
    fun stop(entity: Entity) {
        val velocity = velocityMapper.get(entity) ?: return
        velocity.linear.setZero()
        velocity.angular.setZero()
    }
}
