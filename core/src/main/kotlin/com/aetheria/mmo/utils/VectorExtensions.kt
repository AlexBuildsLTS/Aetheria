package com.aetheria.mmo.utils

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import kotlin.math.sqrt

/**
 * Vector Extension Functions
 * Provides utility functions for vector operations commonly used in game development
 */

// ==================== Vector3 Extensions ====================

/**
 * Get the horizontal distance (ignoring Y axis)
 */
fun Vector3.distanceXZ(other: Vector3): Float {
    val dx = x - other.x
    val dz = z - other.z
    return sqrt(dx * dx + dz * dz)
}

/**
 * Get the squared horizontal distance (faster, no sqrt)
 */
fun Vector3.distanceXZ2(other: Vector3): Float {
    val dx = x - other.x
    val dz = z - other.z
    return dx * dx + dz * dz
}

/**
 * Set only the horizontal components (X and Z)
 */
fun Vector3.setXZ(x: Float, z: Float): Vector3 {
    this.x = x
    this.z = z
    return this
}

/**
 * Normalize only the horizontal components
 */
fun Vector3.normalizeXZ(): Vector3 {
    val len = sqrt(x * x + z * z)
    if (len > 0.0001f) {
        x /= len
        z /= len
    }
    return this
}

/**
 * Lerp (linear interpolation) between two vectors
 */
fun Vector3.lerpTo(target: Vector3, alpha: Float): Vector3 {
    return this.lerp(target, alpha.coerceIn(0f, 1f))
}

/**
 * Smooth damp towards target (spring-like movement)
 */
fun Vector3.smoothDamp(target: Vector3, velocity: Vector3, smoothTime: Float, deltaTime: Float, maxSpeed: Float = Float.POSITIVE_INFINITY): Vector3 {
    val omega = 2f / smoothTime
    val x = omega * deltaTime
    val exp = 1f / (1f + x + 0.48f * x * x + 0.235f * x * x * x)

    var changeX = this.x - target.x
    var changeY = this.y - target.y
    var changeZ = this.z - target.z

    val maxChange = maxSpeed * smoothTime
    val maxChangeSq = maxChange * maxChange
    val sqDist = changeX * changeX + changeY * changeY + changeZ * changeZ

    if (sqDist > maxChangeSq) {
        val mag = sqrt(sqDist)
        changeX = changeX / mag * maxChange
        changeY = changeY / mag * maxChange
        changeZ = changeZ / mag * maxChange
    }

    val targetX = this.x - changeX
    val targetY = this.y - changeY
    val targetZ = this.z - changeZ

    val tempX = (velocity.x + omega * changeX) * deltaTime
    val tempY = (velocity.y + omega * changeY) * deltaTime
    val tempZ = (velocity.z + omega * changeZ) * deltaTime

    velocity.x = (velocity.x - omega * tempX) * exp
    velocity.y = (velocity.y - omega * tempY) * exp
    velocity.z = (velocity.z - omega * tempZ) * exp

    this.x = targetX + (changeX + tempX) * exp
    this.y = targetY + (changeY + tempY) * exp
    this.z = targetZ + (changeZ + tempZ) * exp

    return this
}

/**
 * Clamp vector magnitude
 */
fun Vector3.clampLength(maxLength: Float): Vector3 {
    val len2 = len2()
    if (len2 > maxLength * maxLength) {
        scl(maxLength / sqrt(len2))
    }
    return this
}

/**
 * Get direction to another vector
 */
fun Vector3.directionTo(target: Vector3): Vector3 {
    return Vector3(target).sub(this).nor()
}

/**
 * Check if vector is approximately zero
 */
fun Vector3.isZero(epsilon: Float = 0.0001f): Boolean {
    return len2() < epsilon * epsilon
}

/**
 * Rotate around Y axis (horizontal rotation)
 */
fun Vector3.rotateY(degrees: Float): Vector3 {
    val rad = degrees * MathUtils.degreesToRadians
    val cos = MathUtils.cos(rad)
    val sin = MathUtils.sin(rad)
    val newX = x * cos - z * sin
    val newZ = x * sin + z * cos
    x = newX
    z = newZ
    return this
}

/**
 * Convert to Vector2 (X, Z)
 */
fun Vector3.toVector2XZ(): Vector2 {
    return Vector2(x, z)
}

/**
 * Copy from another vector
 */
fun Vector3.copyFrom(other: Vector3): Vector3 {
    return this.set(other)
}

// ==================== Vector2 Extensions ====================

/**
 * Lerp to target
 */
fun Vector2.lerpTo(target: Vector2, alpha: Float): Vector2 {
    return this.lerp(target, alpha.coerceIn(0f, 1f))
}

/**
 * Clamp vector magnitude
 */
fun Vector2.clampLength(maxLength: Float): Vector2 {
    val len2 = len2()
    if (len2 > maxLength * maxLength) {
        scl(maxLength / sqrt(len2))
    }
    return this
}

/**
 * Get direction to another vector
 */
fun Vector2.directionTo(target: Vector2): Vector2 {
    return Vector2(target).sub(this).nor()
}

/**
 * Check if vector is approximately zero
 */
fun Vector2.isZero(epsilon: Float = 0.0001f): Boolean {
    return len2() < epsilon * epsilon
}

/**
 * Rotate by degrees
 */
fun Vector2.rotateDeg(degrees: Float): Vector2 {
    return this.rotateDeg(degrees)
}

/**
 * Convert to Vector3 (X, 0, Y)
 */
fun Vector2.toVector3XZ(y: Float = 0f): Vector3 {
    return Vector3(x, y, this.y)
}

/**
 * Get perpendicular vector (rotated 90 degrees)
 */
fun Vector2.perpendicular(): Vector2 {
    return Vector2(-y, x)
}

/**
 * Reflect vector across a normal
 */
fun Vector2.reflect(normal: Vector2): Vector2 {
    val dot = this.dot(normal)
    return this.sub(normal.cpy().scl(2f * dot))
}

/**
 * Project vector onto another vector
 */
fun Vector2.projectOnto(other: Vector2): Vector2 {
    val dot = this.dot(other)
    val len2 = other.len2()
    return if (len2 > 0.0001f) {
        other.cpy().scl(dot / len2)
    } else {
        Vector2.Zero
    }
}
