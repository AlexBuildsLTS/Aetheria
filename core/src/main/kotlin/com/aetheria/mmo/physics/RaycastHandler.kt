package com.aetheria.mmo.physics

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray

object RaycastHandler {
    private val ray = Ray()
    private val intersection = Vector3()

    fun getTargetUnderCrosshair(camera: Camera): Vector3? {
        // Shoots a ray from the center of the screen into the 3D world
        ray.set(camera.position, camera.direction)

        // This is where you would check against your Bullet Physics world
        // For now, it returns the direction for visual feedback
        return camera.direction.cpy().scl(100f).add(camera.position)
    }
}