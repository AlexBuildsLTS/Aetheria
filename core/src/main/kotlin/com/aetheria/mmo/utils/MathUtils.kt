package com.aetheria.mmo.utils

import com.badlogic.gdx.math.MathUtils as GdxMath
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import kotlin.math.*

/**
 * Math Utility Functions
 * Extends LibGDX MathUtils with game-specific mathematical operations
 */
object MathUtils {

    const val PI = GdxMath.PI
    const val PI2 = GdxMath.PI2
    const val HALF_PI = PI / 2f
    const val DEG_TO_RAD = GdxMath.degreesToRadians
    const val RAD_TO_DEG = GdxMath.radiansToDegrees

    /**
     * Clamp value between min and max
     */
    fun clamp(value: Float, min: Float, max: Float): Float {
        return GdxMath.clamp(value, min, max)
    }

    /**
     * Clamp value between min and max (Int version)
     */
    fun clamp(value: Int, min: Int, max: Int): Int {
        return GdxMath.clamp(value, min, max)
    }

    /**
     * Linear interpolation
     */
    fun lerp(start: Float, end: Float, alpha: Float): Float {
        return start + (end - start) * alpha
    }

    /**
     * Inverse lerp (get alpha from value)
     */
    fun inverseLerp(start: Float, end: Float, value: Float): Float {
        return if (abs(end - start) < 0.0001f) 0f
        else (value - start) / (end - start)
    }

    /**
     * Remap value from one range to another
     */
    fun remap(value: Float, fromMin: Float, fromMax: Float, toMin: Float, toMax: Float): Float {
        val alpha = inverseLerp(fromMin, fromMax, value)
        return lerp(toMin, toMax, alpha)
    }

    /**
     * Smooth step interpolation (ease in/out)
     */
    fun smoothStep(edge0: Float, edge1: Float, x: Float): Float {
        val t = clamp((x - edge0) / (edge1 - edge0), 0f, 1f)
        return t * t * (3f - 2f * t)
    }

    /**
     * Smoother step interpolation (more gradual ease)
     */
    fun smootherStep(edge0: Float, edge1: Float, x: Float): Float {
        val t = clamp((x - edge0) / (edge1 - edge0), 0f, 1f)
        return t * t * t * (t * (t * 6f - 15f) + 10f)
    }

    /**
     * Ease in (quadratic)
     */
    fun easeIn(t: Float): Float = t * t

    /**
     * Ease out (quadratic)
     */
    fun easeOut(t: Float): Float = t * (2f - t)

    /**
     * Ease in-out (quadratic)
     */
    fun easeInOut(t: Float): Float {
        return if (t < 0.5f) 2f * t * t
        else -1f + (4f - 2f * t) * t
    }

    /**
     * Bounce ease out
     */
    fun bounceOut(t: Float): Float {
        return when {
            t < 1f / 2.75f -> 7.5625f * t * t
            t < 2f / 2.75f -> {
                val t2 = t - 1.5f / 2.75f
                7.5625f * t2 * t2 + 0.75f
            }
            t < 2.5f / 2.75f -> {
                val t2 = t - 2.25f / 2.75f
                7.5625f * t2 * t2 + 0.9375f
            }
            else -> {
                val t2 = t - 2.625f / 2.75f
                7.5625f * t2 * t2 + 0.984375f
            }
        }
    }

    /**
     * Elastic ease out
     */
    fun elasticOut(t: Float): Float {
        if (t == 0f || t == 1f) return t
        val p = 0.3f
        return 2f.pow(-10f * t) * sin((t - p / 4f) * PI2 / p) + 1f
    }

    /**
     * Calculate angle between two points (in degrees)
     */
    fun angleBetween(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return atan2(y2 - y1, x2 - x1) * RAD_TO_DEG
    }

    /**
     * Calculate angle between two vectors (in degrees)
     */
    fun angleBetween(v1: Vector2, v2: Vector2): Float {
        return angleBetween(v1.x, v1.y, v2.x, v2.y)
    }

    /**
     * Normalize angle to 0-360 range
     */
    fun normalizeAngle(angle: Float): Float {
        var result = angle % 360f
        if (result < 0f) result += 360f
        return result
    }

    /**
     * Get shortest angle difference between two angles
     */
    fun angleDifference(angle1: Float, angle2: Float): Float {
        var diff = (angle2 - angle1) % 360f
        if (diff > 180f) diff -= 360f
        if (diff < -180f) diff += 360f
        return diff
    }

    /**
     * Lerp between two angles (shortest path)
     */
    fun lerpAngle(start: Float, end: Float, alpha: Float): Float {
        val diff = angleDifference(start, end)
        return start + diff * alpha
    }

    /**
     * Check if value is approximately equal to target
     */
    fun approximately(value: Float, target: Float, epsilon: Float = 0.0001f): Boolean {
        return abs(value - target) < epsilon
    }

    /**
     * Check if value is within range
     */
    fun inRange(value: Float, min: Float, max: Float): Boolean {
        return value >= min && value <= max
    }

    /**
     * Ping-pong value between 0 and length
     */
    fun pingPong(t: Float, length: Float): Float {
        val t2 = t % (length * 2f)
        return if (t2 < length) t2 else length * 2f - t2
    }

    /**
     * Repeat value between 0 and length
     */
    fun repeat(t: Float, length: Float): Float {
        return clamp(t - floor(t / length) * length, 0f, length)
    }

    /**
     * Sign function (-1, 0, or 1)
     */
    fun sign(value: Float): Int {
        return when {
            value > 0f -> 1
            value < 0f -> -1
            else -> 0
        }
    }

    /**
     * Move towards target value
     */
    fun moveTowards(current: Float, target: Float, maxDelta: Float): Float {
        return if (abs(target - current) <= maxDelta) {
            target
        } else {
            current + sign(target - current).toFloat() * maxDelta
        }
    }

    /**
     * Move towards target angle (shortest path)
     */
    fun moveTowardsAngle(current: Float, target: Float, maxDelta: Float): Float {
        val diff = angleDifference(current, target)
        return if (abs(diff) <= maxDelta) {
            target
        } else {
            current + sign(diff).toFloat() * maxDelta
        }
    }

    /**
     * Calculate parabolic trajectory height
     */
    fun parabola(x: Float, height: Float): Float {
        return height * (1f - 4f * (x - 0.5f) * (x - 0.5f))
    }

    /**
     * Random float between min and max
     */
    fun random(min: Float, max: Float): Float {
        return GdxMath.random(min, max)
    }

    /**
     * Random int between min and max (inclusive)
     */
    fun random(min: Int, max: Int): Int {
        return GdxMath.random(min, max)
    }

    /**
     * Random boolean
     */
    fun randomBool(): Boolean {
        return GdxMath.randomBoolean()
    }

    /**
     * Random boolean with probability
     */
    fun randomBool(probability: Float): Boolean {
        return GdxMath.random() < probability
    }

    /**
     * Random point in circle
     */
    fun randomInCircle(radius: Float): Vector2 {
        val angle = GdxMath.random(0f, PI2)
        val r = sqrt(GdxMath.random()) * radius
        return Vector2(cos(angle) * r, sin(angle) * r)
    }

    /**
     * Random point on circle
     */
    fun randomOnCircle(radius: Float): Vector2 {
        val angle = GdxMath.random(0f, PI2)
        return Vector2(cos(angle) * radius, sin(angle) * radius)
    }

    /**
     * Random point in sphere
     */
    fun randomInSphere(radius: Float): Vector3 {
        val theta = GdxMath.random(0f, PI2)
        val phi = acos(GdxMath.random(-1f, 1f))
        val r = radius * cbrt(GdxMath.random())

        return Vector3(
            r * sin(phi) * cos(theta),
            r * sin(phi) * sin(theta),
            r * cos(phi)
        )
    }

    /**
     * Calculate distance between two points
     */
    fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        return sqrt(dx * dx + dy * dy)
    }

    /**
     * Calculate squared distance (faster, no sqrt)
     */
    fun distanceSquared(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        return dx * dx + dy * dy
    }

    /**
     * Check if point is inside circle
     */
    fun isInsideCircle(px: Float, py: Float, cx: Float, cy: Float, radius: Float): Boolean {
        return distanceSquared(px, py, cx, cy) <= radius * radius
    }

    /**
     * Check if point is inside rectangle
     */
    fun isInsideRect(px: Float, py: Float, rx: Float, ry: Float, width: Float, height: Float): Boolean {
        return px >= rx && px <= rx + width && py >= ry && py <= ry + height
    }
}
