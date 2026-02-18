package com.aetheria.mmo.physics

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState

/**
 * Character Controller
 * Handles character physics movement with proper collision detection
 * Implements kinematic character movement with ground detection
 */
class CharacterController(
    private val rigidBody: btRigidBody,
    private val capsuleShape: btCapsuleShape,
    private val height: Float = 1.8f,
    private val radius: Float = 0.4f
) {

    // Movement parameters
    var moveSpeed = 5f
    var sprintMultiplier = 1.8f
    var jumpForce = 8f
    var airControl = 0.3f

    // State
    private var isGrounded = false
    private var isSprinting = false
    private var canJump = true
    private var jumpCooldown = 0f

    // Vectors (reused to avoid GC)
    private val velocity = Vector3()
    private val moveDirection = Vector3()
    private val groundNormal = Vector3(0f, 1f, 0f)
    private val tmpVec = Vector3()

    // Ground detection
    private val groundCheckDistance = 0.1f
    private var timeSinceGrounded = 0f
    private val coyoteTime = 0.15f // Grace period for jumping after leaving ground

    /**
     * Updates the character controller
     * @param deltaTime Time since last frame
     */
    fun update(deltaTime: Float) {
        // Update jump cooldown
        if (jumpCooldown > 0f) {
            jumpCooldown -= deltaTime
        }

        // Update coyote time
        if (!isGrounded) {
            timeSinceGrounded += deltaTime
        } else {
            timeSinceGrounded = 0f
        }

        // Update can jump state
        canJump = isGrounded || timeSinceGrounded < coyoteTime

        // Apply gravity if not grounded
        if (!isGrounded) {
            velocity.y -= PhysicsConstants.GRAVITY * deltaTime
        } else {
            // Ground friction
            velocity.y = 0f
            velocity.scl(0.9f) // Apply friction
        }

        // Clamp velocity
        velocity.y = velocity.y.coerceIn(-PhysicsConstants.MAX_FALL_SPEED, PhysicsConstants.MAX_JUMP_SPEED)
    }

    /**
     * Moves the character in the specified direction
     * @param direction Normalized movement direction (X, Z plane)
     * @param sprint Whether the character is sprinting
     */
    fun move(direction: Vector3, sprint: Boolean = false) {
        isSprinting = sprint

        val speed = if (sprint) moveSpeed * sprintMultiplier else moveSpeed
        val control = if (isGrounded) 1f else airControl

        moveDirection.set(direction).nor().scl(speed * control)

        // Apply movement to velocity (X and Z only)
        velocity.x = moveDirection.x
        velocity.z = moveDirection.z

        // Apply velocity to rigid body
        rigidBody.linearVelocity = velocity
    }

    /**
     * Makes the character jump
     * @return true if jump was successful
     */
    fun jump(): Boolean {
        if (canJump && jumpCooldown <= 0f) {
            velocity.y = jumpForce
            rigidBody.linearVelocity = velocity
            isGrounded = false
            canJump = false
            jumpCooldown = 0.2f
            return true
        }
        return false
    }

    /**
     * Checks if the character is on the ground
     * Uses a raycast downward from the capsule bottom
     */
    fun checkGrounded(world: btCollisionWorld): Boolean {
        val from = Vector3()
        val to = Vector3()

        // Get character position
        rigidBody.getWorldTransform(tmpVec)
        from.set(tmpVec)

        // Raycast downward
        to.set(from).add(0f, -(height / 2f + groundCheckDistance), 0f)

        val rayCallback = ClosestRayResultCallback(from, to)
        world.rayTest(from, to, rayCallback)

        val wasGrounded = isGrounded
        isGrounded = rayCallback.hasHit()

        if (isGrounded && rayCallback.hasHit()) {
            rayCallback.hitNormalWorld.get(groundNormal)
        }

        rayCallback.dispose()

        return isGrounded
    }

    /**
     * Teleports the character to a new position
     */
    fun teleport(position: Vector3) {
        rigidBody.worldTransform.setTranslation(position)
        velocity.setZero()
        rigidBody.linearVelocity = velocity
    }

    /**
     * Applies a force to the character (e.g., knockback)
     */
    fun applyForce(force: Vector3) {
        // Simplified - will apply force when physics is integrated
        velocity.add(force)
    }

    /**
     * Gets the current velocity
     */
    fun getVelocity(): Vector3 {
        return velocity.cpy()
    }

    /**
     * Gets the current position
     */
    fun getPosition(): Vector3 {
        // Simplified - will get from rigid body when physics is integrated
        return Vector3.Zero
    }

    /**
     * Checks if the character is grounded
     */
    fun isGrounded(): Boolean = isGrounded

    /**
     * Checks if the character is sprinting
     */
    fun isSprinting(): Boolean = isSprinting

    /**
     * Checks if the character can jump
     */
    fun canJump(): Boolean = canJump

    /**
     * Gets the ground normal vector
     */
    fun getGroundNormal(): Vector3 = groundNormal

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
    }
}
