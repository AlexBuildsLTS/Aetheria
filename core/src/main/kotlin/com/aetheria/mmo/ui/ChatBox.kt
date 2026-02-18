package com.aetheria.mmo.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

/**
 * Chat Box Widget
 * Displays chat messages with fade-out
 */
class ChatBox(
    private val width: Float = 400f,
    private val height: Float = 200f
) {
    private val shapeRenderer = ShapeRenderer()
    private val font = BitmapFont()
    private val messages = Array<ChatMessage>()
    private val maxMessages = 10
    private val messageFadeTime = 5f

    private val bgColor = Color(0f, 0f, 0f, 0.5f)
    private val borderColor = Color(0.3f, 0.3f, 0.3f, 0.8f)

    fun addMessage(sender: String, text: String, color: Color = Color.WHITE) {
        messages.add(ChatMessage(sender, text, color))
        if (messages.size > maxMessages) {
            messages.removeIndex(0)
        }
    }

    fun update(deltaTime: Float) {
        val iterator = messages.iterator()
        while (iterator.hasNext()) {
            val msg = iterator.next()
            msg.lifetime += deltaTime
            if (msg.lifetime > messageFadeTime) {
                iterator.remove()
            }
        }
    }

    fun render(batch: SpriteBatch) {
        val x = 20f
        val y = 300f

        // End batch for shapes
        batch.end()

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        shapeRenderer.projectionMatrix = batch.projectionMatrix
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Background
        shapeRenderer.color = bgColor
        shapeRenderer.rect(x, y, width, height)

        shapeRenderer.end()

        // Border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = borderColor
        shapeRenderer.rect(x, y, width, height)
        shapeRenderer.end()

        // Resume batch for text
        batch.begin()

        // Render messages
        var yOffset = y + height - 20f
        for (msg in messages) {
            val alpha = 1f - (msg.lifetime / messageFadeTime).coerceIn(0f, 1f)
            font.color = Color(msg.color.r, msg.color.g, msg.color.b, alpha)

            val fullText = "[${msg.sender}]: ${msg.text}"
            font.draw(batch, fullText, x + 10f, yOffset)
            yOffset -= 20f
        }
    }

    fun dispose() {
        shapeRenderer.dispose()
        font.dispose()
    }

    private data class ChatMessage(
        val sender: String,
        val text: String,
        val color: Color,
        var lifetime: Float = 0f
    )
}
