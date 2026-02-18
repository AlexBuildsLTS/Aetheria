package com.aetheria.mmo.physics

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3

/**
 * Motion State
 * Synchronizes physics body transforms with game entity transforms
 *
 * NOTE: Simplified version - full physics integration pending
 */
class MotionState(
    private val initialTransform: Matrix4 = Matrix4()
) {

    private val worldTransform = Matrix4(initialTransform)
    private val tmpMatrix = Matrix4()

    // Cached components for efficient access
    private val position = Vector3()
    private val rotation = Quaternion()
    private val scale = Vector3(1f, 1f, 1f)

    /**
     * Gets the world transform
     */
    fun getWorldTransform(worldTrans: Matrix4) {
        worldTrans.set(worldTransform)
    }

    /**
     * Sets the world transform
     */
    fun setWorldTransform(worldTrans: Matrix4) {
        worldTransform.set(worldTrans)

        // Extract components for easy access
        worldTransform.getTranslation(position)
        worldTransform.getRotation(rotation, true)
        worldTransform.getScale(scale)
    }

    /**
     * Gets the current world transform
     */
    fun getTransform(): Matrix4 = worldTransform

    /**
     * Gets the current position
     */
    fun getPosition(): Vector3 = position

    /**
     * Gets the current rotation
     */
    fun getRotation(): Quaternion = rotation

    /**
     * Gets the current scale
     */
    fun getScale(): Vector3 = scale

    /**
     * Sets the world transform manually
     */
    fun setTransform(transform: Matrix4) {
        worldTransform.set(transform)
        worldTransform.getTranslation(position)
        worldTransform.getRotation(rotation, true)
        worldTransform.getScale(scale)
    }

    /**
     * Sets position only
     */
    fun setPosition(x: Float, y: Float, z: Float) {
        position.set(x, y, z)
        worldTransform.setTranslation(position)
    }

    /**
     * Sets rotation only
     */
    fun setRotation(quaternion: Quaternion) {
        rotation.set(quaternion)
        tmpMatrix.set(worldTransform)
        tmpMatrix.getTranslation(position)
        worldTransform.set(position, rotation, scale)
    }

    /**
     * Resets to initial transform
     */
    fun reset() {
        worldTransform.set(initialTransform)
        worldTransform.getTranslation(position)
        worldTransform.getRotation(rotation, true)
        worldTransform.getScale(scale)
    }

    /**
     * Creates a copy of this motion state
     */
    fun copy(): MotionState {
        return MotionState(Matrix4(worldTransform))
    }
}
