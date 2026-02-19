package com.aetheria.mmo.screens

import com.aetheria.mmo.AetheriaGame
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array

class GameWorldScreen(private val game: AetheriaGame, private val archetypeId: String) : ScreenAdapter() {

    // --- 3D ENGINE ---
    private lateinit var modelBatch: ModelBatch
    private lateinit var environment: Environment
    private lateinit var camera: PerspectiveCamera
    private lateinit var camController: ThirdPersonController

    // --- WORLD OBJECTS ---
    private val instances = Array<ModelInstance>()
    private val assets = Array<Model>() // Keep track to dispose later
    private lateinit var playerInstance: ModelInstance

    // Player State
    private val playerPosition = Vector3(0f, 0f, 0f)
    private val playerSpeed = 10f

    override fun show() {
        // 1. Setup Environment (Lighting)
        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

        // 2. Setup Camera
        modelBatch = ModelBatch()
        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.near = 1f
        camera.far = 300f
        camera.update()

        // 3. Build World
        buildFloor()
        spawnPlayer()

        // 4. Setup Controls
        camController = ThirdPersonController()
        Gdx.input.inputProcessor = camController

        Gdx.app.log("GameWorld", "Welcome to Aetheria. Class: $archetypeId")
    }

    private fun buildFloor() {
        val mb = ModelBuilder()
        // Create a large gray floor
        val floorModel = mb.createBox(100f, 1f, 100f,
            Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
        assets.add(floorModel)
        instances.add(ModelInstance(floorModel, 0f, -1f, 0f))
    }

    private fun spawnPlayer() {
        val mb = ModelBuilder()
        val material = Material()

        // Set Color based on Class ID
        when (archetypeId) {
            "chrono" -> material.set(ColorAttribute.createDiffuse(Color.GOLD))
            "nano" -> material.set(ColorAttribute.createDiffuse(Color.LIME))
            "void" -> material.set(ColorAttribute.createDiffuse(Color.PURPLE))
            "aether" -> material.set(ColorAttribute.createDiffuse(Color.CYAN))
            else -> material.set(ColorAttribute.createDiffuse(Color.WHITE))
        }

        // Create Player Capsule
        val playerModel = mb.createCapsule(1f, 4f, 16, material,
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())

        assets.add(playerModel)
        playerInstance = ModelInstance(playerModel, 0f, 2f, 0f)
        instances.add(playerInstance)
    }

    override fun render(delta: Float) {
        // 1. Process Input (Movement)
        camController.update(delta)

        // 2. Clear Screen
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        // 3. Render
        modelBatch.begin(camera)
        modelBatch.render(instances, environment)
        modelBatch.end()
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
    }

    override fun dispose() {
        modelBatch.dispose()
        assets.forEach { it.dispose() }
    }

    // --- INNER CLASS: INPUT CONTROLLER ---
    inner class ThirdPersonController : InputAdapter() {
        private val temp = Vector3()

        fun update(delta: Float) {
            var moved = false
            // WASD Movement
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                playerPosition.z -= playerSpeed * delta
                moved = true
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                playerPosition.z += playerSpeed * delta
                moved = true
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                playerPosition.x -= playerSpeed * delta
                moved = true
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                playerPosition.x += playerSpeed * delta
                moved = true
            }

            // Update Player Transform
            playerInstance.transform.setToTranslation(playerPosition)

            // Update Camera to follow player
            camera.position.set(playerPosition.x, playerPosition.y + 10f, playerPosition.z + 10f)
            camera.lookAt(playerPosition)
            camera.update()
        }
    }
}