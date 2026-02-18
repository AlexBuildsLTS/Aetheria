package com.aetheria.mmo.physics

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.Pool

/**
 * Character Controller
 * Handles character physics movement with proper collision detection
 * Implements kinematic character movement with ground detection
 * Optimized for performance with object pooling and reduced allocations
 */
class CharacterController(
    private val rigidBody: btRigidBody,
    private val capsuleShape: btCapsuleShape,
    private val motionState: btMotionState,
    private val height: Float = 1.8f,
    private val radius: Float = 0.4f
) {

    // Movement parameters - made private with getters/setters for encapsulation
    private var _moveSpeed = 5f
    var moveSpeed: Float
        get() = _moveSpeed
        set(value) { _moveSpeed = value.coerceAtLeast(0f) }

    private var _sprintMultiplier = 1.8f
    var sprintMultiplier: Float
        get() = _sprintMultiplier
        set(value) { _sprintMultiplier = value.coerceAtLeast(1f) }

    private var _jumpForce = 8f
    var jumpForce: Float
        get() = _jumpForce
        set(value) { _jumpForce = value.coerceAtLeast(0f) }

    private var _airControl = 0.3f
    var airControl: Float
        get() = _airControl
        set(value) { _airControl = value.coerceIn(0f, 1f) }

    // State variables
    private var isGrounded = false
    private var isSprinting = false
    private var canJump = true
    private var jumpCooldown = 0f

    // Reusable vectors to avoid garbage collection - initialized once
    private val velocity = Vector3()
    private val moveDirection = Vector3()
    private val groundNormal = Vector3(0f, 1f, 0f)
    private val tmpVec = Vector3()
    private val zeroVec = Vector3() // Used for angular velocity reset

    // Ground detection constants
    private val groundCheckDistance = 0.1f
    private var timeSinceGrounded = 0f
    private val coyoteTime = 0.15f // Grace period for jumping after leaving ground
    private val groundFriction = 0.9f // Friction coefficient when grounded
    private val jumpCooldownDuration = 0.2f // Minimum time between jumps

    // Performance optimization: cache frequently used values
    private val halfHeight = height / 2f
    private val groundCheckOffset = halfHeight + groundCheckDistance
    private val maxFallSpeed = PhysicsConstants.MAX_FALL_SPEED
    private val maxJumpSpeed = PhysicsConstants.MAX_JUMP_SPEED
    private val gravity = PhysicsConstants.GRAVITY

    // Reusable objects for raycasting
    private val rayFrom = Vector3()
    private val rayTo = Vector3()
    private val rayCallback = ClosestRayResultCallback(Vector3.Zero, Vector3.Zero)

    /**
     * Updates the character controller
     * @param deltaTime Time since last frame (must be positive)
     */
    fun update(deltaTime: Float) {
        require(deltaTime > 0) { "deltaTime must be positive" }

        // Update jump cooldown
        if (jumpCooldown > 0f) {
            jumpCooldown -= deltaTime
        }

        // Update coyote time - only increment if not grounded
        if (!isGrounded) {
            timeSinceGrounded += deltaTime
        } else {
            timeSinceGrounded = 0f
        }

        // Update can jump state based on ground state and coyote time
        canJump = isGrounded || timeSinceGrounded < coyoteTime

        // Apply gravity if not grounded
        if (!isGrounded) {
            velocity.y -= gravity * deltaTime
        } else {
            // Ground friction - only apply to horizontal movement
            velocity.y = 0f
            velocity.x *= groundFriction
            velocity.z *= groundFriction
        }

        // Clamp vertical velocity to prevent unrealistic speeds
        velocity.y = velocity.y.coerceIn(-maxFallSpeed, maxJumpSpeed)

        // Apply updated velocity to rigid body
        applyVelocityToRigidBody()
    }

    /**
     * Moves the character in the specified direction
     * @param direction Normalized movement direction (X, Z plane)
     * @param sprint Whether the character is sprinting
     */
    fun move(direction: Vector3, sprint: Boolean = false) {
        isSprinting = sprint

        // Calculate speed based on sprint state
        val speed = if (sprint) _moveSpeed * _sprintMultiplier else _moveSpeed

        // Determine control factor based on ground state
        val control = if (isGrounded) 1f else _airControl

        // Set movement direction and scale by speed and control
        // Only modify X and Z components to preserve vertical velocity
        moveDirection.set(direction).nor().scl(speed * control)

        // Apply horizontal movement while preserving vertical velocity
        velocity.x = moveDirection.x
        velocity.z = moveDirection.z
    }

    /**
     * Makes the character jump
     * @return true if jump was successful
     */
    fun jump(): Boolean {
        // Check if jump is allowed (grounded or in coyote time, and cooldown expired)
        if (canJump && jumpCooldown <= 0f) {
            velocity.y = _jumpForce

            // Update character state
            isGrounded = false
            canJump = false
            jumpCooldown = jumpCooldownDuration
            return true
        }
        return false
    }

    /**
     * Checks if the character is on the ground using raycast
     * @param world The collision world to test against
     * @return true if character is grounded
     */
    fun checkGrounded(world: btCollisionWorld): Boolean {
        // Get current character position
        val transform = rigidBody.worldTransform
        transform.getTranslation(tmpVec)

        // Calculate raycast start and end points
        rayFrom.set(tmpVec)
        rayTo.set(tmpVec.x, tmpVec.y - groundCheckOffset, tmpVec.z)

        // Reset ray callback
        rayCallback.collisionObject = null
        rayCallback.closestHitFraction = 1f

        // Perform raycast test
        world.rayTest(rayFrom, rayTo, rayCallback)

        // Update ground state
        val wasGrounded = isGrounded
        isGrounded = rayCallback.hasHit()

        // Update ground normal if hit was detected
        if (isGrounded) {
            rayCallback.getHitNormalWorld(groundNormal)

            // Reset coyote time when landing
            if (!wasGrounded) {
                timeSinceGrounded = 0f
            }
        }

        return isGrounded
    }

    /**
     * Teleports the character to a new position
     * @param position Target position
     */
    fun teleport(position: Vector3) {
        // Get current transform and update position
        val transform = rigidBody.worldTransform
        transform.setTranslation(position)
        rigidBody.worldTransform = transform

        // Reset velocity
        velocity.setZero()
        tmpVec.setZero()
        rigidBody.setLinearVelocity(tmpVec)
        rigidBody.setAngularVelocity(tmpVec)

        // Update motion state to match new position
        motionState.setWorldTransform(transform)
    }

    /**
     * Applies a force to the character (e.g., knockback)
     * @param force Force vector to apply
     */
    fun applyForce(force: Vector3) {
        // Apply central impulse for immediate force effect
        rigidBody.applyCentralImpulse(force)

        // Update velocity to reflect applied force
        velocity.set(rigidBody.linearVelocity)
    }

    /**
     * Gets the current velocity
     * @return Copy of current velocity vector
     */
    fun getVelocity(): Vector3 {
        return Vector3(velocity) // Create new instance to prevent external modification
    }

    /**
     * Gets the current position
     * @return Current position vector (new instance)
     */
    fun getPosition(): Vector3 {
        return rigidBody.worldTransform.getTranslation(Vector3())
    }

    /**
     * Checks if the character is grounded
     * @return Grounded state
     */
    fun isGrounded(): Boolean = isGrounded

    /**
     * Checks if the character is sprinting
     * @return Sprinting state
     */
    fun isSprinting(): Boolean = isSprinting

    /**
     * Checks if the character can jump
     * @return Jump availability
     */
    fun canJump(): Boolean = canJump

    /**
     * Gets the ground normal vector
     * @return Ground normal vector (new instance)
     */
    fun getGroundNormal(): Vector3 {
        return Vector3(groundNormal)
    }

    /**
     * Resets the character controller state
     */
    fun reset() {
        velocity.setZero()
        moveDirection.setZero()
        isGrounded = false
        isSprinting = false
        canJump = true
        jumpCooldown = 0f
        timeSinceGrounded = 0f
        groundNormal.set(0f, 1f, 0f)

        // Reset rigid body state
        tmpVec.setZero()
        rigidBody.setLinearVelocity(tmpVec)
        rigidBody.setAngularVelocity(tmpVec)
    }

    /**
     * Helper method to apply velocity to rigid body
     * Centralized velocity application for consistency
     */
    private fun applyVelocityToRigidBody() {
        // Apply linear velocity directly
        rigidBody.setLinearVelocity(velocity)

        // Reset angular velocity using temporary zero vector
        tmpVec.setZero()
        rigidBody.setAngularVelocity(tmpVec)

        // Lock Y-axis rotation to prevent character from tipping over
        tmpVec.set(1f, 0f, 1f)
        rigidBody.setAngularFactor(tmpVec)

        // Sync motion state with rigid body transform
        motionState.setWorldTransform(rigidBody.worldTransform)
    }

    /**
     * Clean up resources when controller is no longer needed
     */
    fun dispose() {
        // Clear vector references
        velocity.setZero()
        moveDirection.setZero()
        groundNormal.setZero()
        tmpVec.setZero()

        // Clear object pool if this is the last instance
        // Note: In production, consider a proper resource cleanup strategy
    }
}
