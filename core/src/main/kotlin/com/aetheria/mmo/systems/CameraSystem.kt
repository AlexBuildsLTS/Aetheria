package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.TransformComponent
import com.aetheria.mmo.components.PlayerComponent

class CameraSystem(private val camera: PerspectiveCamera) : IteratingSystem(
    Family.all(TransformComponent::class.java, PlayerComponent::class.java).get()
) {
    private var distance = 12f
    private var angleX = 180f // Start facing character
    private var angleY = 30f
    private val targetPos = Vector3()
    private val offset = Vector3()
    
    // Config
    private var mouseSensitivity = 0.2f
    private var joystickSensitivity = 150f

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getComponent(TransformComponent::class.java)
        val input = entity.getComponent(com.aetheria.mmo.components.InputComponent::class.java)

        // Rotation input (Mouse)
        // Only rotate if cursor is caught OR right mouse is held
        if (Gdx.input.isCursorCatched || Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            angleX -= Gdx.input.deltaX * mouseSensitivity
            angleY = MathUtils.clamp(angleY + Gdx.input.deltaY * mouseSensitivity, 5f, 85f)
        }
        
        // Rotation input (Joystick)
        if (input != null && (Math.abs(input.aimX) > 0.1f || Math.abs(input.aimY) > 0.1f)) {
            angleX -= input.aimX * joystickSensitivity * deltaTime
            angleY = MathUtils.clamp(angleY + input.aimY * joystickSensitivity * deltaTime, 5f, 85f)
        }

        // Toggle cursor catching with ESC/F1 or similar (Optional, can be moved to a UI system)
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            Gdx.input.isCursorCatched = !Gdx.input.isCursorCatched
        }

        // Calculate position (Smooth Orbit Camera)
        val hDist = distance * MathUtils.cos(angleY * MathUtils.degreesToRadians)
        val vDist = distance * MathUtils.sin(angleY * MathUtils.degreesToRadians)
        
        val targetAngleX = angleX * MathUtils.degreesToRadians
        
        offset.set(
            hDist * MathUtils.sin(targetAngleX),
            vDist,
            hDist * MathUtils.cos(targetAngleX)
        )

        targetPos.set(transform.position).add(offset)
        camera.position.lerp(targetPos, 8f * deltaTime)
        camera.lookAt(transform.position.x, transform.position.y + 1.5f, transform.position.z)
        camera.update()
    }
}
