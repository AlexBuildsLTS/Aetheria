package com.aetheria.mmo.physics

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray

/**
 * Raycast Handler for 3D world interaction
 * Handles raycasting from camera to detect objects under crosshair
 */
object RaycastHandler {
    private val ray = Ray()
    private val intersection = Vector3()

    /**
     * Gets the target position under the crosshair
     * @param camera The game camera
     * @return The 3D position in world space, or null if no hit
     */
    fun getTargetUnderCrosshair(camera: Camera): Vector3? {
        // Shoots a ray from the center of the screen into the 3D world
        ray.set(camera.position, camera.direction)

        // TODO: Integrate with Bullet Physics world for actual collision detection
        // For now, it returns the direction for visual feedback
        return camera.direction.cpy().scl(100f).add(camera.position)
    }

    /**
     * Performs a raycast and returns the intersection point
     * @param camera The game camera
     * @param maxDistance Maximum raycast distance
     * @return The intersection point, or null if no hit
     */
    fun raycast(camera: Camera, maxDistance: Float = 100f): Vector3? {
        ray.set(camera.position, camera.direction)

        // TODO: Implement actual physics raycast
        // This is a placeholder that returns a point along the ray
        intersection.set(camera.direction).scl(maxDistance).add(camera.position)
        return intersection
    }
}