package com.aetheria.mmo.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController

// --- YOUR PROJECT IMPORTS ---
import com.aetheria.mmo.entities.EntityBuilder
import com.aetheria.mmo.systems.AnimationSystem
import com.aetheria.mmo.systems.PlayerControlSystem
import com.aetheria.mmo.systems.RenderSystem
// If RenderSystem is missing, comment it out for a second, but it should be there from your file list.

class GameWorldScreen : ScreenAdapter() {

    // 1. The Entity Component System (ECS) Engine
    private val engine: Engine = PooledEngine()

    // 2. The 3D Camera & Controller (So you can look around)
    private val camera: PerspectiveCamera
    private val camController: CameraInputController

    // 3. The Lighting Environment
    private val environment: Environment

    init {
        // --- A. Setup Camera ---
        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.position.set(2f, 2f, 5f) // Positioned up and back to see the model
        camera.lookAt(0f, 1f, 0f)       // Look at the character's chest height
        camera.near = 0.1f
        camera.far = 300f
        camera.update()

        // Allow mouse/touch to rotate camera
        camController = CameraInputController(camera)
        Gdx.input.inputProcessor = camController

        // --- B. Setup Lighting (High Fidelity PBR-lite) ---
        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

        // --- C. Add Systems to Engine ---
        // 1. Animation System (Updates bones)
        engine.addSystem(AnimationSystem())

        // 2. Player Control (Reads Joystick)
        engine.addSystem(PlayerControlSystem(camera))

        // 3. Render System (Draws the model)
        // Pass the camera and environment so the system knows how to draw
        engine.addSystem(RenderSystem(camera, environment))

        // --- D. Spawn the Character ---
        // We call the EntityBuilder we just fixed.
        // Make sure this string matches the file you renamed exactly!
        val playerEntity = EntityBuilder.createPlayer("char_vanguard_base.glb")

        engine.addEntity(playerEntity)
    }

    override fun render(delta: Float) {
        // 1. Clear Screen
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f) // Dark Sci-fi Blue background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        // 2. Update Camera Controls
        camController.update()

        // 3. Update ECS Engine (Process all logic + rendering)
        engine.update(delta)
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
    }
}