package com.aetheria.mmo.systems

import com.aetheria.mmo.components.*
import com.aetheria.mmo.input.VirtualJoystick
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3

class PlayerControlSystem(private val camera: Camera) : IteratingSystem(
    Family.all(PlayerComponent::class.java, VelocityComponent::class.java, TransformComponent::class.java).get()
) {
    private val moveDirection = Vector3()
    private val camDirection = Vector3()
    private val rightVector = Vector3()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val velocity = entity.getComponent(VelocityComponent::class.java)
        val transform = entity.getComponent(TransformComponent::class.java)
        val state = entity.getComponent(StateComponent::class.java)

        // 1. Read Joystick Input
        val inputX = VirtualJoystick.getKnobX()
        val inputY = VirtualJoystick.getKnobY()

        if (inputX != 0f || inputY != 0f) {
            // 2. Calculate movement relative to Camera (Standard 3rd Person logic)
            camDirection.set(camera.direction.x, 0f, camera.direction.z).nor()
            rightVector.set(camera.direction).crs(camera.up).nor()

            moveDirection.setZero()
            moveDirection.add(camDirection.scl(inputY)) // Forward/Back
            moveDirection.add(rightVector.scl(inputX))   // Left/Right
            moveDirection.nor().scl(velocity.speed)

            velocity.linear.set(moveDirection)

            // 3. Update Rotation (Face movement direction)
            transform.rotation.setFromCross(Vector3.Z, moveDirection.nor())
            state.set(StateComponent.WALKING)
        } else {
            velocity.linear.scl(velocity.friction) // Smooth stop
            state.set(StateComponent.IDLE)
        }
    }
}