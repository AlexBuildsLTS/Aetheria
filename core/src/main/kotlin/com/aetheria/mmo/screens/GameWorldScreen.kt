package com.aetheria.mmo.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Color
import com.aetheria.mmo.AetheriaGame
import com.aetheria.mmo.components.*
import com.aetheria.mmo.managers.ResourceManager
import com.aetheria.mmo.systems.*

/**
 * AAA+ Tier Game World Screen
 * Main gameplay screen with full ECS integration:
 * - 3D model rendering with GLTF support
 * - WASD movement controls
 * - Third-person camera with mouse/keyboard rotation
 * - Animation system
 */
class GameWorldScreen(val game: AetheriaGame) : ScreenAdapter() {
    private val engine = Engine()
    private val camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

    // Systems
    private lateinit var renderSystem: RenderSystem
    private lateinit var cameraSystem: CameraSystem
    private lateinit var movementSystem: MovementSystem

    init {
        setupCamera()
        setupSystems()
        createPlayer()

        Gdx.app.log("GameWorldScreen", "Initialized successfully")
        Gdx.app.log("Controls", "WASD: Move | Arrow Keys/Right Mouse: Rotate Camera | Scroll: Zoom")
    }

    private fun setupCamera() {
        camera.position.set(10f, 10f, 10f)
        camera.lookAt(0f, 0f, 0f)
        camera.near = 0.1f
        camera.far = 300f
        camera.update()
    }

    private fun setupSystems() {
        // Order matters: Movement -> Camera -> Render
        movementSystem = MovementSystem()
        cameraSystem = CameraSystem(camera)
        renderSystem = RenderSystem(game.modelBatch, camera)

        engine.addSystem(movementSystem)
        engine.addSystem(cameraSystem)
        engine.addSystem(renderSystem)
    }

    private fun createPlayer() {
        val player = Entity()

        // Load the Vanguard 3D model
        val model = ResourceManager.getModel("char_vanguard_base.glb")
        val modelInstance = ModelInstance(model)

        // Model Component
        val modelComp = ModelComponent().apply {
            this.modelInstance = modelInstance
            isVisible = true
        }

        // Transform Component (position, rotation, scale)
        val transformComp = TransformComponent().apply {
            position.set(0f, 0f, 0f) // Start at world origin
            scale.set(1f, 1f, 1f)
        }

        // Velocity Component (for movement)
        val velocityComp = VelocityComponent().apply {
            speed = 8f
        }

        // Player Component (marks this as the player entity)
        val playerComp = PlayerComponent()

        // Animation Component
        val animComp = AnimationComponent().apply {
            controller = AnimationController(modelInstance)

            // Auto-play first animation if available
            if (modelInstance.animations.size > 0) {
                val animName = modelInstance.animations[0].id
                controller.setAnimation(animName, -1) // -1 = loop forever
                currentAnimation = animName
                Gdx.app.log("GameWorld", "Playing animation: $animName")
            }
        }

        // Assemble the entity
        player.add(modelComp)
        player.add(transformComp)
        player.add(velocityComp)
        player.add(playerComp)
        player.add(animComp)

        // Add to engine
        engine.addEntity(player)

        Gdx.app.log("GameWorld", "Vanguard player entity created and added to engine")
    }

    override fun render(delta: Float) {
        // Clear screen with a dark blue-grey background (void aesthetic)
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f)
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        // Enable depth testing for proper 3D rendering
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)

        // Update all ECS systems
        engine.update(delta)
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
    }

    override fun dispose() {
        // Engine will be disposed by the game
        Gdx.app.log("GameWorldScreen", "Disposed")
    }
}