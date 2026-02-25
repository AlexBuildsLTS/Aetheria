package com.aetheria.mmo.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport

/**
 * Touch Controls for Android
 * Provides on-screen buttons for mobile gameplay
 */
class TouchControls(private val viewport: Viewport) {
    private val shapeRenderer = ShapeRenderer()

    // Button positions and sizes
    private val buttonSize = 80f
    private val buttonPadding = 20f

    // Action buttons (right side)
    private val attackButton = TouchButton(
        x = Gdx.graphics.width - buttonSize - buttonPadding,
        y = buttonPadding + buttonSize + buttonPadding,
        size = buttonSize,
        label = "ATK",
        color = Color.RED
    )

    private val jumpButton = TouchButton(
        x = Gdx.graphics.width - buttonSize - buttonPadding,
        y = buttonPadding,
        size = buttonSize,
        label = "JUMP",
        color = Color.GREEN
    )

    private val ability1Button = TouchButton(
        x = Gdx.graphics.width - (buttonSize + buttonPadding) * 2,
        y = buttonPadding + buttonSize + buttonPadding,
        size = buttonSize * 0.7f,
        label = "Q",
        color = Color.CYAN
    )

    private val ability2Button = TouchButton(
        x = Gdx.graphics.width - (buttonSize + buttonPadding) * 2,
        y = buttonPadding,
        size = buttonSize * 0.7f,
        label = "E",
        color = Color.CYAN
    )

    private val ability3Button = TouchButton(
        x = Gdx.graphics.width - (buttonSize + buttonPadding) * 3,
        y = buttonPadding + buttonSize + buttonPadding,
        size = buttonSize * 0.7f,
        label = "R",
        color = Color.MAGENTA
    )

    private val ability4Button = TouchButton(
        x = Gdx.graphics.width - (buttonSize + buttonPadding) * 3,
        y = buttonPadding,
        size = buttonSize * 0.7f,
        label = "F",
        color = Color.YELLOW
    )

    private val buttons = listOf(
        attackButton, jumpButton,
        ability1Button, ability2Button,
        ability3Button, ability4Button
    )

    // Input state
    var isAttackPressed = false
        private set
    var isJumpPressed = false
        private set
    var isAbility1Pressed = false
        private set
    var isAbility2Pressed = false
        private set
    var isAbility3Pressed = false
        private set
    var isAbility4Pressed = false
        private set

    fun update() {
        // Reset states
        isAttackPressed = false
        isJumpPressed = false
        isAbility1Pressed = false
        isAbility2Pressed = false
        isAbility3Pressed = false
        isAbility4Pressed = false

        // Check touch input
        for (i in 0 until 5) { // Support up to 5 simultaneous touches
            if (Gdx.input.isTouched(i)) {
                val touchX = Gdx.input.getX(i).toFloat()
                val touchY = (Gdx.graphics.height - Gdx.input.getY(i)).toFloat()

                when {
                    attackButton.contains(touchX, touchY) -> isAttackPressed = true
                    jumpButton.contains(touchX, touchY) -> isJumpPressed = true
                    ability1Button.contains(touchX, touchY) -> isAbility1Pressed = true
                    ability2Button.contains(touchX, touchY) -> isAbility2Pressed = true
                    ability3Button.contains(touchX, touchY) -> isAbility3Pressed = true
                    ability4Button.contains(touchX, touchY) -> isAbility4Pressed = true
                }
            }
        }
    }

    fun render(batch: SpriteBatch) {
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA)

        shapeRenderer.projectionMatrix = batch.projectionMatrix

        // Draw buttons
        buttons.forEach { button ->
            val isPressed = when (button) {
                attackButton -> isAttackPressed
                jumpButton -> isJumpPressed
                ability1Button -> isAbility1Pressed
                ability2Button -> isAbility2Pressed
                ability3Button -> isAbility3Pressed
                ability4Button -> isAbility4Pressed
                else -> false
            }

            button.render(shapeRenderer, isPressed)
        }
    }

    fun resize(width: Int, height: Int) {
        // Update button positions for new screen size
        attackButton.x = width - buttonSize - buttonPadding
        jumpButton.x = width - buttonSize - buttonPadding

        ability1Button.x = width - (buttonSize + buttonPadding) * 2
        ability2Button.x = width - (buttonSize + buttonPadding) * 2

        ability3Button.x = width - (buttonSize + buttonPadding) * 3
        ability4Button.x = width - (buttonSize + buttonPadding) * 3
    }

    fun dispose() {
        shapeRenderer.dispose()
    }

    private class TouchButton(
        var x: Float,
        var y: Float,
        val size: Float,
        val label: String,
        val color: Color
    ) {
        fun contains(touchX: Float, touchY: Float): Boolean {
            return touchX >= x && touchX <= x + size &&
                   touchY >= y && touchY <= y + size
        }

        fun render(renderer: ShapeRenderer, isPressed: Boolean) {
            renderer.begin(ShapeRenderer.ShapeType.Filled)

            // Background
            renderer.color = if (isPressed) {
                Color(color.r, color.g, color.b, 0.8f)
            } else {
                Color(color.r, color.g, color.b, 0.4f)
            }
            renderer.circle(x + size / 2f, y + size / 2f, size / 2f)

            renderer.end()

            // Border
            renderer.begin(ShapeRenderer.ShapeType.Line)
            renderer.color = color
            renderer.circle(x + size / 2f, y + size / 2f, size / 2f)
            renderer.end()
        }
    }
}
