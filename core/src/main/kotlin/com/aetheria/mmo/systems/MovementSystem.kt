package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.TransformComponent
import com.aetheria.mmo.components.VelocityComponent
import com.aetheria.mmo.components.PlayerComponent
import com.aetheria.mmo.components.StateComponent

/**
 * AAA+ Tier Movement System
 * Handles WASD keyboard input and applies velocity to player entities.
 * Features:
 * - Smooth acceleration/deceleration
 * - Normalized diagonal movement
 * - Jump with gravity
 * - Ground collision
 */
class MovementSystem : IteratingSystem(
    Family.all(
        TransformComponent::class.java,
        VelocityComponent::class.java,
        PlayerComponent::class.java
    ).get()
) {
    private val tempVelocity = Vector3()
    private val moveSpeed = 8f // Units per second
    private val acceleration = 30f // How fast we reach max speed
    private val deceleration = 20f // How fast we stop

    // Jump & Gravity
    private val jumpForce = 12f
    private val gravity = -25f
    private val groundLevel = 0f
    private var isGrounded = true

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getComponent(TransformComponent::class.java)
        val velocity = entity.getComponent(VelocityComponent::class.java)
        val state = entity.getComponent(StateComponent::class.java)

        // Get horizontal input direction
        val inputDir = getInputDirection()
        var isMoving = false

        // Apply horizontal acceleration or deceleration
        if (inputDir.len2() > 0.01f) {
            // Player is pressing movement keys
            inputDir.nor() // Normalize to prevent faster diagonal movement
            isMoving = true

            // Smoothly accelerate towards target velocity
            tempVelocity.set(inputDir).scl(moveSpeed)
            velocity.linear.x = lerp(velocity.linear.x, tempVelocity.x, acceleration * deltaTime)
            velocity.linear.z = lerp(velocity.linear.z, tempVelocity.z, acceleration * deltaTime)
        } else {
            // No input - decelerate to stop
            velocity.linear.x = lerp(velocity.linear.x, 0f, deceleration * deltaTime)
            velocity.linear.z = lerp(velocity.linear.z, 0f, deceleration * deltaTime)
        }

        // Handle jumping
        isGrounded = transform.position.y <= groundLevel
        if (isGrounded && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            velocity.linear.y = jumpForce
            Gdx.app.log("Movement", "Jump!")
        }

        // Apply gravity
        if (!isGrounded || velocity.linear.y > 0) {
            velocity.linear.y += gravity * deltaTime
        }

        // Apply velocity to position
        transform.position.add(
            velocity.linear.x * deltaTime,
            velocity.linear.y * deltaTime,
            velocity.linear.z * deltaTime
        )

        // Ground collision
        if (transform.position.y < groundLevel) {
            transform.position.y = groundLevel
            velocity.linear.y = 0f
        }

        // Clamp to world bounds
        transform.position.x = transform.position.x.coerceIn(-50f, 50f)
        transform.position.z = transform.position.z.coerceIn(-50f, 50f)

        // Update state for animations (if StateComponent exists)
        if (state != null) {
            state.current = when {
                !isGrounded -> StateComponent.JUMPING
                isMoving -> StateComponent.WALKING
                else -> StateComponent.IDLE
            }
        }
    }

    /**
     * Reads WASD keyboard input and returns a direction vector.
     * W/S = forward/backward (Z axis)
     * A/D = left/right (X axis)
     */
    private fun getInputDirection(): Vector3 {
        tempVelocity.setZero()

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            tempVelocity.z -= 1f // Forward (negative Z in LibGDX)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            tempVelocity.z += 1f // Backward
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            tempVelocity.x -= 1f // Left
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            tempVelocity.x += 1f // Right
        }

        return tempVelocity
    }

    private fun lerp(start: Float, end: Float, alpha: Float): Float {
        return start + (end - start) * alpha.coerceIn(0f, 1f)
    }
}
