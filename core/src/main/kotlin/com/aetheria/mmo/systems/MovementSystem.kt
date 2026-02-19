package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Quaternion
import com.aetheria.mmo.components.*

class MovementSystem : IteratingSystem(
    Family.all(TransformComponent::class.java, VelocityComponent::class.java, PlayerComponent::class.java).get()
) {
    private val tempDir = Vector3()
    private val targetRot = Quaternion()
    private val gravity = -20f
    private val jumpForce = 10f

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getComponent(TransformComponent::class.java)
        val velocity = entity.getComponent(VelocityComponent::class.java)
        val state = entity.getComponent(StateComponent::class.java)
        val input = entity.getComponent(InputComponent::class.java) ?: return

        // Movement
        tempDir.set(0f, 0f, 0f)
        if (Gdx.input.isKeyPressed(Input.Keys.W)) tempDir.z -= 1f
        if (Gdx.input.isKeyPressed(Input.Keys.S)) tempDir.z += 1f
        if (Gdx.input.isKeyPressed(Input.Keys.A)) tempDir.x -= 1f
        if (Gdx.input.isKeyPressed(Input.Keys.D)) tempDir.x += 1f

        if (tempDir.len2() > 0.1f) {
            tempDir.nor().scl(velocity.speed)
            velocity.linear.x = tempDir.x
            velocity.linear.z = tempDir.z
            
            // Rotation
            val angle = MathUtils.atan2(tempDir.x, tempDir.z) * MathUtils.radiansToDegrees
            targetRot.setFromAxis(Vector3.Y, angle)
            transform.rotation.slerp(targetRot, 10f * deltaTime)
            
            state.current = StateComponent.WALKING
        } else {
            velocity.linear.x = 0f
            velocity.linear.z = 0f
            state.current = StateComponent.IDLE
        }

        // Gravity & Jump
        if (transform.position.y > 0) {
            velocity.linear.y += gravity * deltaTime
        } else {
            transform.position.y = 0f
            velocity.linear.y = 0f
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                velocity.linear.y = jumpForce
            }
        }

        transform.position.add(velocity.linear.x * deltaTime, velocity.linear.y * deltaTime, velocity.linear.z * deltaTime)
    }
}
