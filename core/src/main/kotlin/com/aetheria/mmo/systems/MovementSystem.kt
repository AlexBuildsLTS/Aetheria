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

/**
 * AAA+ Tier Movement System
 * Handles WASD keyboard input and applies velocity to player entities.
 * Features smooth acceleration/deceleration and normalized diagonal movement.
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

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getComponent(TransformComponent::class.java)
        val velocity = entity.getComponent(VelocityComponent::class.java)

        // Get input direction
        val inputDir = getInputDirection()

        // Apply acceleration or deceleration
        if (inputDir.len2() > 0.01f) {
            // Player is pressing movement keys
            inputDir.nor() // Normalize to prevent faster diagonal movement

            // Smoothly accelerate towards target velocity
            tempVelocity.set(inputDir).scl(moveSpeed)
            velocity.linear.lerp(tempVelocity, acceleration * deltaTime)
        } else {
            // No input - decelerate to stop
            velocity.linear.lerp(Vector3.Zero, deceleration * deltaTime)
        }

        // Apply velocity to position
        transform.position.add(
            velocity.linear.x * deltaTime,
            velocity.linear.y * deltaTime,
            velocity.linear.z * deltaTime
        )

        // Optional: Clamp to world bounds (example: -50 to 50)
        transform.position.x = transform.position.x.coerceIn(-50f, 50f)
        transform.position.z = transform.position.z.coerceIn(-50f, 50f)
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
}
