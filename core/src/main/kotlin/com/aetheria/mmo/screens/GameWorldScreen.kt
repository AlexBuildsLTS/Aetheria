package com.aetheria.mmo.screens

import com.aetheria.mmo.components.ModelComponent
import com.aetheria.mmo.managers.ResourceManager
import com.aetheria.mmo.systems.RenderSystem
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelInstance

class GameWorldScreen : ScreenAdapter() {

    private val engine = Engine()
    private val camera: PerspectiveCamera

    init {
        // Setup Camera
        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.position.set(5f, 5f, 5f)
        camera.lookAt(0f, 0f, 0f)
        camera.near = 0.1f
        camera.far = 300f
        camera.update()

        // Add Rendering System
        engine.addSystem(RenderSystem(camera))

        // Spawn the Strider Character
        spawnCharacter(ResourceManager.MODEL_STRIDER)
    }

    private fun spawnCharacter(modelPath: String) {
        val entity = Entity()
        val modelComp = ModelComponent()

        // Load the specific GLB file you requested
        val model = ResourceManager.getModel(modelPath)
        val instance = ModelInstance(model)

        // Reset transform to 0,0,0
        instance.transform.idt()

        modelComp.modelInstance = instance
        entity.add(modelComp)

        engine.addEntity(entity)
    }

    override fun render(delta: Float) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        engine.update(delta)

        // Rotate camera to view the model
        camera.rotateAround(com.badlogic.gdx.math.Vector3.Zero, com.badlogic.gdx.math.Vector3.Y, 20f * delta)
        camera.update()
    }
}