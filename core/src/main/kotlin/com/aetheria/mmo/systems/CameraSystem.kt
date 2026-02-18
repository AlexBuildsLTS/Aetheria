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

/**
 * AAA+ Tier Camera System
 * Third-person camera that follows the player with smooth interpolation.
 * Features:
 * - Mouse drag to rotate around player
 * - Arrow keys for camera rotation
 * - Scroll wheel for zoom in/out
 * - Smooth camera movement with lerp
 */
class CameraSystem(private val camera: PerspectiveCamera) : IteratingSystem(
    Family.all(
        TransformComponent::class.java,
        PlayerComponent::class.java
    ).get()
) {
    // Camera settings
    private var cameraDistance = 10f
    private var cameraAngleX = 45f // Horizontal rotation (degrees)
    private var cameraAngleY = 30f // Vertical rotation (degrees)

    private val minDistance = 5f
    private val maxDistance = 30f
    private val minAngleY = 10f
    private val maxAngleY = 80f

    private val rotationSpeed = 100f // Degrees per second
    private val mouseRotationSpeed = 0.3f
    private val zoomSpeed = 2f
    private val smoothFactor = 10f // Higher = snappier camera

    private val targetPosition = Vector3()
    private val cameraOffset = Vector3()

    private var lastMouseX = 0
    private var lastMouseY = 0
    private var isDragging = false

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getComponent(TransformComponent::class.java)

        // Handle camera rotation input
        handleRotationInput(deltaTime)

        // Handle zoom input
        handleZoomInput(deltaTime)

        // Calculate camera position based on angles and distance
        calculateCameraPosition(transform.position)

        // Smoothly move camera to target position
        camera.position.lerp(targetPosition, smoothFactor * deltaTime)

        // Always look at the player
        camera.lookAt(transform.position)
        camera.up.set(Vector3.Y)
        camera.update()
    }

    /**
     * Handles keyboard and mouse input for camera rotation
     */
    private fun handleRotationInput(deltaTime: Float) {
        // Keyboard rotation (Arrow keys)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            cameraAngleX += rotationSpeed * deltaTime
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            cameraAngleX -= rotationSpeed * deltaTime
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            cameraAngleY = (cameraAngleY + rotationSpeed * deltaTime).coerceIn(minAngleY, maxAngleY)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            cameraAngleY = (cameraAngleY - rotationSpeed * deltaTime).coerceIn(minAngleY, maxAngleY)
        }

        // Mouse drag rotation (Right mouse button)
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            if (!isDragging) {
                isDragging = true
                lastMouseX = Gdx.input.x
                lastMouseY = Gdx.input.y
            } else {
                val deltaX = Gdx.input.x - lastMouseX
                val deltaY = Gdx.input.y - lastMouseY

                cameraAngleX -= deltaX * mouseRotationSpeed
                cameraAngleY = (cameraAngleY + deltaY * mouseRotationSpeed).coerceIn(minAngleY, maxAngleY)

                lastMouseX = Gdx.input.x
                lastMouseY = Gdx.input.y
            }
        } else {
            isDragging = false
        }

        // Normalize horizontal angle to 0-360
        cameraAngleX = cameraAngleX % 360f
    }

    /**
     * Handles mouse scroll wheel for zoom
     */
    private fun handleZoomInput(deltaTime: Float) {
        val scrollAmount = Gdx.input.getDeltaY()
        if (scrollAmount != 0) {
            cameraDistance = (cameraDistance + scrollAmount * zoomSpeed).coerceIn(minDistance, maxDistance)
        }
    }

    /**
     * Calculates the camera position based on spherical coordinates
     */
    private fun calculateCameraPosition(playerPosition: Vector3) {
        // Convert angles to radians
        val angleXRad = cameraAngleX * MathUtils.degreesToRadians
        val angleYRad = cameraAngleY * MathUtils.degreesToRadians

        // Calculate offset using spherical coordinates
        cameraOffset.x = cameraDistance * MathUtils.cos(angleYRad) * MathUtils.sin(angleXRad)
        cameraOffset.y = cameraDistance * MathUtils.sin(angleYRad)
        cameraOffset.z = cameraDistance * MathUtils.cos(angleYRad) * MathUtils.cos(angleXRad)

        // Set target position (player position + offset)
        targetPosition.set(playerPosition).add(cameraOffset)
    }

    /**
     * Resets camera to default position
     */
    fun resetCamera() {
        cameraDistance = 10f
        cameraAngleX = 45f
        cameraAngleY = 30f
    }
}
