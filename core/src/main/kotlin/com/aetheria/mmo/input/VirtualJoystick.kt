package com.aetheria.mmo.input

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.Color

/**
 * Virtual Joystick
 * On-screen joystick for mobile controls
 * Provides analog movement input
 */
class VirtualJoystick(
    private val x: Float,
    private val y: Float,
    private val outerRadius: Float = 80f,
    private val innerRadius: Float = 30f
) {

    private val center = Vector2(x, y)
    private val knobPosition = Vector2(x, y)
    private val touchPosition = Vector2()
    private val delta = Vector2()

    private var isTouched = false
    private var touchPointer = -1

    private val shapeRenderer = ShapeRenderer()

    /**
     * Handles touch down event
     */
    fun touchDown(screenX: Float, screenY: Float, pointer: Int): Boolean {
        val distance = Vector2(screenX, screenY).dst(center)

        if (distance <= outerRadius) {
            isTouched = true
            touchPointer = pointer
            touchPosition.set(screenX, screenY)
            updateKnobPosition()
            return true
        }

        return false
    }

    /**
     * Handles touch dragged event
     */
    fun touchDragged(screenX: Float, screenY: Float, pointer: Int): Boolean {
        if (isTouched && pointer == touchPointer) {
            touchPosition.set(screenX, screenY)
            updateKnobPosition()
            return true
        }
        return false
    }

    /**
     * Handles touch up event
     */
    fun touchUp(pointer: Int): Boolean {
        if (pointer == touchPointer) {
            isTouched = false
            touchPointer = -1
            knobPosition.set(center)
            delta.setZero()
            return true
        }
        return false
    }

    /**
     * Updates knob position based on touch
     */
    private fun updateKnobPosition() {
        delta.set(touchPosition).sub(center)

        if (delta.len() > outerRadius) {
            delta.nor().scl(outerRadius)
        }

        knobPosition.set(center).add(delta)
    }

    /**
     * Renders the joystick
     */
    fun render(batch: Batch) {
        batch.end()

        shapeRenderer.projectionMatrix = batch.projectionMatrix
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Outer circle (background)
        shapeRenderer.color = Color(0.3f, 0.3f, 0.3f, 0.5f)
        shapeRenderer.circle(center.x, center.y, outerRadius)

        // Inner circle (knob)
        val knobColor = if (isTouched) Color(0.5f, 0.8f, 1f, 0.8f) else Color(0.6f, 0.6f, 0.6f, 0.7f)
        shapeRenderer.color = knobColor
        shapeRenderer.circle(knobPosition.x, knobPosition.y, innerRadius)

        shapeRenderer.end()

        batch.begin()
    }

    /**
     * Gets normalized X input (-1 to 1)
     */
    fun getKnobX(): Float {
        return if (outerRadius > 0f) delta.x / outerRadius else 0f
    }

    /**
     * Gets normalized Y input (-1 to 1)
     */
    fun getKnobY(): Float {
        return if (outerRadius > 0f) delta.y / outerRadius else 0f
    }

    /**
     * Gets the input as a vector
     */
    fun getInput(): Vector2 {
        return Vector2(getKnobX(), getKnobY())
    }

    /**
     * Checks if joystick is being touched
     */
    fun isTouched(): Boolean = isTouched

    /**
     * Resets the joystick
     */
    fun reset() {
        isTouched = false
        touchPointer = -1
        knobPosition.set(center)
        delta.setZero()
    }

    /**
     * Disposes resources
     */
    fun dispose() {
        shapeRenderer.dispose()
    }
}
