package com.aetheria.mmo.screens

import com.aetheria.mmo.AetheriaGame
import com.aetheria.mmo.components.*
import com.aetheria.mmo.entities.EntityBuilder
import com.aetheria.mmo.events.EventQueue
import com.aetheria.mmo.managers.NetworkManager
import com.aetheria.mmo.managers.ResourceManager
import com.aetheria.mmo.systems.*
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx
import net.mgsx.gltf.scene3d.scene.SceneManager
import net.mgsx.gltf.scene3d.scene.SceneAsset
import net.mgsx.gltf.scene3d.scene.Scene
import net.mgsx.gltf.scene3d.utils.IBLBuilder
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute

class GameWorldScreen(private val game: AetheriaGame, private val archetypeId: String) : ScreenAdapter() {

    private lateinit var engine: PooledEngine
    private var playerEntity: Entity? = null
    private lateinit var sceneManager: SceneManager
    private lateinit var camera: PerspectiveCamera
    private val viewport3D = FitViewport(1920f, 1080f)

    private val disposableModels = Array<Model>()
    private lateinit var hud: GameHUD
    private lateinit var multiplexer: InputMultiplexer
    
    // Lighting
    private lateinit var light: DirectionalLightEx
    private lateinit var diffuseCubemap: Cubemap
    private lateinit var environmentCubemap: Cubemap
    private lateinit var specularCubemap: Cubemap

    override fun show() {
        Gdx.app.log("AETHERIA_DEBUG", "3D ENGINE INITIALIZED SUCCESSFULLY")

        // 1. Setup Camera
        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.position.set(10f, 10f, 10f)
        camera.lookAt(0f, 0f, 0f)
        camera.near = 0.1f
        camera.far = 1000f
        camera.update()
        viewport3D.camera = camera

        // 2. Setup SceneManager
        sceneManager = SceneManager()
        sceneManager.setCamera(camera)

        // 3. Setup AAA Lighting
        setupLighting()

        // 4. Setup HUD
        hud = GameHUD()
        multiplexer = InputMultiplexer()
        multiplexer.addProcessor(hud.stage)
        Gdx.input.inputProcessor = multiplexer

        // 5. Setup ECS
        setupECS()
        
        // 6. Build Environment (Neon Trees + Floor)
        buildEnvironment()
        
        // 7. Spawn Player
        spawnPlayer()
    }

    private fun setupLighting() {
        light = DirectionalLightEx()
        light.direction.set(-0.5f, -0.8f, -0.2f).nor()
        light.color.set(Color.CYAN)
        light.intensity = 2f
        sceneManager.environment.add(light)

        try {
            val iblBuilder = IBLBuilder.createOutdoor(light)
            diffuseCubemap = iblBuilder.buildIrradianceMap(256)
            specularCubemap = iblBuilder.buildRadianceMap(10)
            environmentCubemap = iblBuilder.buildEnvMap(1024)

            sceneManager.setAmbientLight(0.2f)
            sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap))
            sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap))
            
            iblBuilder.dispose()
        } catch (e: Exception) {
            sceneManager.setAmbientLight(0.5f)
        }
    }

    private fun setupECS() {
        engine = PooledEngine()
        val inputSystem = InputHandlerSystem()
        inputSystem.joystickProvider = hud
        engine.addSystem(inputSystem)
        
        engine.addSystem(MovementSystem(camera))
        engine.addSystem(CombatSystem(camera))
        engine.addSystem(HealthSystem())
        engine.addSystem(CameraSystem(camera))
        engine.addSystem(RenderSystem(sceneManager, camera))
    }

    private fun buildEnvironment() {
        val mb = ModelBuilder()
        val material = Material(ColorAttribute.createDiffuse(Color(0.05f, 0.05f, 0.1f, 1f)))
        val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        
        // Floor
        val floorModel = mb.createBox(100f, 1f, 100f, material, attributes)
        disposableModels.add(floorModel)
        
        val floorEntity = engine.createEntity()
        floorEntity.add(TransformComponent().apply { position.set(0f, -0.5f, 0f) })
        floorEntity.add(ModelComponent().apply { 
            modelInstance = ModelInstance(floorModel) 
        })
        engine.addEntity(floorEntity)

        // Neon Trees
        val treeAsset = ResourceManager.getSceneAsset("models/environment/env_tree_neon.glb")
        if (treeAsset != null) {
            for (i in 0 until 10) {
                val x = (Math.random() * 80 - 40).toFloat()
                val z = (Math.random() * 80 - 40).toFloat()
                
                val treeEntity = engine.createEntity()
                treeEntity.add(TransformComponent().apply { 
                    position.set(x, 0f, z) 
                    scale.set(2f, 2f, 2f)
                })
                treeEntity.add(ModelComponent().apply { 
                    modelInstance = ModelInstance(treeAsset.scene.model) 
                })
                engine.addEntity(treeEntity)
            }
        }
    }

    private fun spawnPlayer() {
        playerEntity = EntityBuilder.spawnPlayer(engine, archetypeId)
        
        // Link player to HUD for skill events
        hud.playerEntity = playerEntity

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val profile = NetworkManager.fetchProfile()
            if (profile != null && playerEntity != null) {
                Gdx.app.postRunnable {
                    NetworkManager.applyStatsToEntity(playerEntity!!, profile.stats)
                }
            }
        }
    }

    override fun render(delta: Float) {
        EventQueue.process()
        engine.update(delta)

        playerEntity?.let { player ->
            val health = player.getComponent(HealthComponent::class.java)
            val stamina = player.getComponent(StaminaComponent::class.java)
            if (health != null && stamina != null) {
                hud.updatePlayerStats(health.current, health.max, stamina.current, stamina.max)
            }
        }

        viewport3D.apply()
        Gdx.gl.glClearColor(0f, 0f, 0.05f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        
        sceneManager.update(delta)
        sceneManager.render()
        
        hud.render(delta)
    }

    override fun resize(width: Int, height: Int) {
        viewport3D.update(width, height, true)
        sceneManager.updateViewport(width.toFloat(), height.toFloat())
        hud.resize(width, height)
    }

    override fun dispose() {
        sceneManager.dispose()
        disposableModels.forEach { it.dispose() }
        hud.dispose()
        if (::diffuseCubemap.isInitialized) diffuseCubemap.dispose()
        if (::environmentCubemap.isInitialized) environmentCubemap.dispose()
        if (::specularCubemap.isInitialized) specularCubemap.dispose()
    }
}
