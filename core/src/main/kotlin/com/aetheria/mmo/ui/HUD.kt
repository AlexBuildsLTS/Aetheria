package com.aetheria.mmo.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.aetheria.mmo.components.HealthComponent
import com.aetheria.mmo.components.StaminaComponent
import com.aetheria.mmo.components.CombatComponent
import com.badlogic.ashley.core.Entity

class HUD {
    private val shapeRenderer = ShapeRenderer()
    private val font = BitmapFont()

    fun render(batch: SpriteBatch, player: Entity?, className: String, width: Int, height: Int) {
        if (player == null) return
        val health = player.getComponent(HealthComponent::class.java) ?: return
        val stamina = player.getComponent(StaminaComponent::class.java) ?: return

        // Bars
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapeRenderer.projectionMatrix = batch.projectionMatrix
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        
        // Health
        shapeRenderer.color = Color.DARK_GRAY
        shapeRenderer.rect(20f, 20f, 200f, 20f)
        shapeRenderer.color = Color.RED
        shapeRenderer.rect(20f, 20f, 200f * (health.current / health.max), 20f)
        
        // Stamina
        shapeRenderer.color = Color.DARK_GRAY
        shapeRenderer.rect(20f, 45f, 200f, 15f)
        shapeRenderer.color = Color.YELLOW
        shapeRenderer.rect(20f, 45f, 200f * (stamina.current / stamina.max), 15f)
        
        shapeRenderer.end()

        // Text
        batch.begin()
        font.color = Color.WHITE
        font.draw(batch, "Class: $className", 20f, height - 20f)
        font.draw(batch, "FPS: ${Gdx.graphics.framesPerSecond}", width - 80f, height - 20f)
        batch.end()
    }

    fun dispose() {
        shapeRenderer.dispose()
        font.dispose()
    }
}
