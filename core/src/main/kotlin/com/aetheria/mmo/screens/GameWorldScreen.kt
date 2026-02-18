package com.aetheria.mmo.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.aetheria.mmo.AetheriaGame
import com.aetheria.mmo.components.*
import com.aetheria.mmo.managers.ResourceManager
import com.aetheria.mmo.systems.*

/**
 * AAA+ Tier Game World Screen
 * Main gameplay screen with full ECS integration:
 * - 3D model rendering with GLTF support
 * - WASD movement controls
 * - SPACE to jump
 * - Third-person camera with mouse/keyboard rotation
 * - Animation system
 * - Ground plane for reference
 * - On-screen UI
 */
class GameWorldScreen(val game: AetheriaGame, private val selectedClass: String = "Vanguard") : ScreenAdapter() {
    private val engine = Engine()
    private val camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

    // Systems
    private lateinit var renderSystem: RenderSystem
    private lateinit var cameraSystem: CameraSystem
    private lateinit var movementSystem: MovementSystem

    // Ground plane
    private lateinit var groundModel: Model
    private lateinit var groundInstance: ModelInstance

    // UI
    private val uiBatch = SpriteBatch()
    private val font = BitmapFont()

    init {
        setupCamera()
        setupSystems()
        createGround()
        createPlayer()

        Gdx.app.log("GameWorldScreen", "Initialized successfully")
        Gdx.app.log("Controls", "WASD: Move | SPACE: Jump | Arrow Keys/Right Mouse: Rotate Camera | Scroll: Zoom")
        Gdx.app.log("GameWorldScreen", "Selected class: $selectedClass")
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

    private fun createGround() {
        // Create a large ground plane for visual reference
        val modelBuilder = ModelBuilder()
        groundModel = modelBuilder.createBox(
            100f, 0.1f, 100f,
            Material(ColorAttribute.createDiffuse(Color(0.2f, 0.25f, 0.2f, 1f))),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
        groundInstance = ModelInstance(groundModel)
        groundInstance.transform.setToTranslation(0f, -0.05f, 0f)
    }

    private fun createPlayer() {
        val player = Entity()

        // Map class name to model file
        val modelFile = when (selectedClass) {
            "Vanguard" -> "char_vanguard_base.glb"
            "Weaver" -> "char_weaver_base.glb"
            "Strider" -> "char_strider_base.glb"
            "Medic" -> "char_medic_base.glb"
            else -> "char_vanguard_base.glb"
        }

        // Load the selected character model
        val model = ResourceManager.getModel(modelFile)
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

            // Auto-play idle/first animation if available
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

        Gdx.app.log("GameWorld", "$selectedClass player entity created and added to engine")
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

        // Render ground plane
        game.modelBatch.begin(camera)
        game.modelBatch.render(groundInstance, renderSystem.environment)
        game.modelBatch.end()

        // Render UI
        renderUI()

        // ESC to return to menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.screen = CharacterSelectScreen(game)
        }
    }

    private fun renderUI() {
        uiBatch.begin()
        font.color = Color.WHITE

        // Controls
        font.draw(uiBatch, "WASD: Move | SPACE: Jump | Arrow Keys: Rotate Camera", 10f, Gdx.graphics.height - 10f)
        font.draw(uiBatch, "Right Mouse: Drag Camera | Scroll: Zoom | ESC: Menu", 10f, Gdx.graphics.height - 30f)

        // Class info
        font.draw(uiBatch, "Class: $selectedClass", 10f, Gdx.graphics.height - 60f)

        // FPS
        font.draw(uiBatch, "FPS: ${Gdx.graphics.framesPerSecond}", 10f, 30f)

        uiBatch.end()
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
    }

    override fun dispose() {
        groundModel.dispose()
        uiBatch.dispose()
        font.dispose()
        Gdx.app.log("GameWorldScreen", "Disposed")
    }
}