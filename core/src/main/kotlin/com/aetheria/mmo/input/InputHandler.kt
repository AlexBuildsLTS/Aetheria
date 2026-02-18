package com.aetheria.mmo.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

/**
 * Input Handler
 * Processes keyboard, mouse, and touch input
 * Translates raw input into game actions
 */
class InputHandler : InputAdapter() {

    // Input state
    private val keysPressed = mutableSetOf<Int>()
    private val keysJustPressed = mutableSetOf<Int>()
    private val keysJustReleased = mutableSetOf<Int>()

    // Mouse state
    private val mousePosition = Vector2()
    private val mouseDelta = Vector2()
    private var mouseButtonsPressed = mutableSetOf<Int>()

    // Movement input
    private val movementInput = Vector2()
    private var sprintPressed = false
    private var jumpPressed = false
    private var jumpJustPressed = false

    // Camera input
    private val cameraInput = Vector2()
    var mouseSensitivity = 0.3f
    var invertY = false

    // Touch input (for mobile)
    private val touchPositions = mutableMapOf<Int, Vector2>()

    /**
     * Updates input state - call this each frame
     */
    fun update() {
        // Clear just-pressed/released states
        keysJustPressed.clear()
        keysJustReleased.clear()
        jumpJustPressed = false

        // Update movement input
        updateMovementInput()

        // Update camera input
        updateCameraInput()

        // Reset mouse delta
        mouseDelta.setZero()
    }

    /**
     * Updates movement input from keyboard
     */
    private fun updateMovementInput() {
        movementInput.setZero()

        if (isKeyPressed(KeyBindings.MOVE_FORWARD)) movementInput.y += 1f
        if (isKeyPressed(KeyBindings.MOVE_BACKWARD)) movementInput.y -= 1f
        if (isKeyPressed(KeyBindings.MOVE_LEFT)) movementInput.x -= 1f
        if (isKeyPressed(KeyBindings.MOVE_RIGHT)) movementInput.x += 1f

        // Normalize diagonal movement
        if (movementInput.len2() > 0f) {
            movementInput.nor()
        }

        sprintPressed = isKeyPressed(KeyBindings.SPRINT)
        jumpPressed = isKeyPressed(KeyBindings.JUMP)
    }

    /**
     * Updates camera input from mouse
     */
    private fun updateCameraInput() {
        cameraInput.set(mouseDelta).scl(mouseSensitivity)
        if (invertY) {
            cameraInput.y = -cameraInput.y
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        keysPressed.add(keycode)
        keysJustPressed.add(keycode)

        if (keycode == KeyBindings.JUMP) {
            jumpJustPressed = true
        }

        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        keysPressed.remove(keycode)
        keysJustReleased.add(keycode)
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        mouseButtonsPressed.add(button)
        touchPositions[pointer] = Vector2(screenX.toFloat(), screenY.toFloat())
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        mouseButtonsPressed.remove(button)
        touchPositions.remove(pointer)
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val lastPos = touchPositions[pointer] ?: return false
        mouseDelta.set(screenX - lastPos.x, screenY - lastPos.y)
        lastPos.set(screenX.toFloat(), screenY.toFloat())
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val deltaX = screenX - mousePosition.x
        val deltaY = screenY - mousePosition.y
        mouseDelta.set(deltaX, deltaY)
        mousePosition.set(screenX.toFloat(), screenY.toFloat())
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        // Handle zoom
        return true
    }

    // Query methods
    fun isKeyPressed(keycode: Int): Boolean = keysPressed.contains(keycode)
    fun isKeyJustPressed(keycode: Int): Boolean = keysJustPressed.contains(keycode)
    fun isKeyJustReleased(keycode: Int): Boolean = keysJustReleased.contains(keycode)

    fun isMouseButtonPressed(button: Int): Boolean = mouseButtonsPressed.contains(button)

    fun getMovementInput(): Vector2 = movementInput
    fun getCameraInput(): Vector2 = cameraInput
    fun isSprintPressed(): Boolean = sprintPressed
    fun isJumpPressed(): Boolean = jumpPressed
    fun isJumpJustPressed(): Boolean = jumpJustPressed

    fun getMousePosition(): Vector2 = mousePosition
    fun getMouseDelta(): Vector2 = mouseDelta

    /**
     * Checks if ability key was just pressed
     */
    fun getAbilityPressed(): String? {
        return when {
            isKeyJustPressed(KeyBindings.ABILITY_1) -> "Q"
            isKeyJustPressed(KeyBindings.ABILITY_2) -> "E"
            isKeyJustPressed(KeyBindings.ABILITY_3) -> "R"
            isKeyJustPressed(KeyBindings.ABILITY_4) -> "F"
            else -> null
        }
    }

    /**
     * Resets all input state
     */
    fun reset() {
        keysPressed.clear()
        keysJustPressed.clear()
        keysJustReleased.clear()
        mouseButtonsPressed.clear()
        touchPositions.clear()
        movementInput.setZero()
        cameraInput.setZero()
        mouseDelta.setZero()
        sprintPressed = false
        jumpPressed = false
        jumpJustPressed = false
    }
}
