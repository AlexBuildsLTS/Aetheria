package com.aetheria.mmo.input

import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3

/**
 * Camera Input Controller
 * Handles third-person camera movement and rotation
 * Supports orbit, zoom, and collision detection
 */
class CameraInputController(
    private val camera: PerspectiveCamera,
    private val target: Vector3 = Vector3.Zero
) {

    // Camera settings
    var distance = 8f
    var minDistance = 3f
    var maxDistance = 20f
    var zoomSpeed = 2f

    var pitch = 20f // Vertical angle (degrees)
    var yaw = 0f    // Horizontal angle (degrees)
    var minPitch = -80f
    var maxPitch = 80f

    var rotationSpeed = 0.5f
    var smoothing = 0.15f

    // Camera offset
    var heightOffset = 1.5f
    var shoulderOffset = 0.5f

    // Internal state
    private val desiredPosition = Vector3()
    private val currentPosition = Vector3()
    private val tmpVec = Vector3()

    /**
     * Updates camera based on input
     */
    fun update(deltaTime: Float, cameraInput: com.badlogic.gdx.math.Vector2) {
        // Update rotation from input
        yaw -= cameraInput.x * rotationSpeed
        pitch += cameraInput.y * rotationSpeed

        // Clamp pitch
        pitch = MathUtils.clamp(pitch, minPitch, maxPitch)

        // Normalize yaw
        yaw = yaw % 360f

        // Calculate desired camera position
        calculateDesiredPosition()

        // Smoothly interpolate to desired position
        currentPosition.lerp(desiredPosition, smoothing)

        // Update camera
        camera.position.set(currentPosition)
        camera.lookAt(target.x, target.y + heightOffset, target.z)
        camera.up.set(Vector3.Y)
        camera.update()
    }

    /**
     * Calculates the desired camera position based on angles and distance
     */
    private fun calculateDesiredPosition() {
        val pitchRad = pitch * MathUtils.degreesToRadians
        val yawRad = yaw * MathUtils.degreesToRadians

        // Calculate position on sphere around target
        val x = MathUtils.cos(pitchRad) * MathUtils.sin(yawRad) * distance
        val y = MathUtils.sin(pitchRad) * distance
        val z = MathUtils.cos(pitchRad) * MathUtils.cos(yawRad) * distance

        // Apply offsets
        desiredPosition.set(
            target.x + x + shoulderOffset,
            target.y + y + heightOffset,
            target.z + z
        )
    }

    /**
     * Zooms the camera in or out
     */
    fun zoom(amount: Float) {
        distance += amount * zoomSpeed
        distance = MathUtils.clamp(distance, minDistance, maxDistance)
    }

    /**
     * Sets the camera target
     */
    fun setTarget(newTarget: Vector3) {
        target.set(newTarget)
    }

    /**
     * Gets the camera forward direction (horizontal plane)
     */
    fun getForwardDirection(): Vector3 {
        val yawRad = yaw * MathUtils.degreesToRadians
        return tmpVec.set(
            MathUtils.sin(yawRad),
            0f,
            MathUtils.cos(yawRad)
        ).nor()
    }

    /**
     * Gets the camera right direction
     */
    fun getRightDirection(): Vector3 {
        return getForwardDirection().crs(Vector3.Y).nor()
    }

    /**
     * Resets camera to default position
     */
    fun reset() {
        pitch = 20f
        yaw = 0f
        distance = 8f
        currentPosition.set(desiredPosition)
    }

    /**
     * Snaps camera behind target (useful after teleport)
     */
    fun snapBehindTarget() {
        calculateDesiredPosition()
        currentPosition.set(desiredPosition)
        camera.position.set(currentPosition)
        camera.lookAt(target)
        camera.update()
    }
}
