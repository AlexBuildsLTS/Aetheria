package com.aetheria.mmo.screens

import com.aetheria.mmo.AetheriaGame
import com.aetheria.mmo.managers.ResourceManager
import com.aetheria.mmo.managers.SkinManager
import com.aetheria.mmo.net.CharacterClass
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx
import net.mgsx.gltf.scene3d.scene.Scene
import net.mgsx.gltf.scene3d.scene.SceneManager

class CharacterSelectScreen(private val game: AetheriaGame) : ScreenAdapter() {

    private val stage = Stage(FitViewport(1920f, 1080f))
    private val skin = SkinManager.skin
    private lateinit var statsPanel: Table
    private lateinit var abilitiesPanel: Table
    
    private lateinit var sceneManager: SceneManager
    private lateinit var cam: PerspectiveCamera
    private var currentScene: Scene? = null
    private var modelRotation = 180f

    data class ArchetypeData(
        val enumVal: CharacterClass,
        val name: String,
        val role: String,
        val desc: String,
        val color: Color,
        val modelPath: String,
        val hp: Float,
        val damage: Float,
        val mobility: Float,
        val abilities: List<Pair<String, String>>
    )

    private val classes = listOf(
        ArchetypeData(CharacterClass.Vanguard, "CHRONO VANGUARD", "Tank", "Guardian of time.", Color.GOLD, ResourceManager.CHAR_VANGUARD, 100f, 40f, 30f, listOf("Shield" to "Block incoming damage", "Time Warp" to "Slow nearby enemies")),
        ArchetypeData(CharacterClass.Weaver, "AETHER MAGUS", "Mage", "Elemental master.", Color.CYAN, ResourceManager.CHAR_WEAVER, 50f, 90f, 40f, listOf("Bolt" to "Fire a magic bolt", "Blast" to "Area of effect explosion")),
        ArchetypeData(CharacterClass.Strider, "VOID STRIDER", "DPS", "Shadow assassin.", Color.PURPLE, ResourceManager.CHAR_STRIDER, 60f, 100f, 90f, listOf("Blink" to "Teleport forward", "Shadow Strike" to "Critical hit from behind")),
        ArchetypeData(CharacterClass.Medic, "NANO WEAVER", "Support", "Medic of the void.", Color.LIME, ResourceManager.CHAR_MEDIC, 70f, 30f, 60f, listOf("Heal" to "Restore ally health", "Nano Shield" to "Grant temporary shield"))
    )

    private var selectedIndex = 0

    override fun show() {
        Gdx.input.inputProcessor = stage

        setup3D()
        buildUI()
        loadCharacter(selectedIndex)
    }

    private fun setup3D() {
        sceneManager = SceneManager()
        cam = PerspectiveCamera(60f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.position.set(0f, 1.5f, 3.5f)
        cam.lookAt(0f, 1.0f, 0f)
        cam.near = 0.1f
        cam.far = 100f
        sceneManager.setCamera(cam)

        val light = DirectionalLightEx()
        light.direction.set(1f, -2f, -1f).nor()
        light.intensity = 2.0f
        sceneManager.environment.add(light)
        sceneManager.setAmbientLight(0.4f)
    }

    private fun loadCharacter(index: Int) {
        val arch = classes[index]
        currentScene?.let { sceneManager.removeScene(it) }
        
        var modelInstance: ModelInstance? = null
        val sceneAsset = ResourceManager.getSceneAsset(arch.modelPath)
        
        if (sceneAsset != null) {
            modelInstance = ModelInstance(sceneAsset.scene.model)
        } else {
            // Fallback
            val fallback = ResourceManager.createPlaceholderModel(arch.color, 1f, 2f, "capsule")
            modelInstance = ModelInstance(fallback)
        }

        currentScene = Scene(modelInstance)
        currentScene?.modelInstance?.transform?.setToRotation(Vector3.Y, modelRotation)
        sceneManager.addScene(currentScene!!)
        
        updateInfoPanels()
    }

    private fun buildUI() {
        val root = Table(skin)
        root.setFillParent(true)
        stage.addActor(root)

        val dragArea = Container<Actor>()
        dragArea.addListener(object : DragListener() {
            override fun drag(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                modelRotation -= getDeltaX() * 0.5f
                currentScene?.modelInstance?.transform?.setToRotation(Vector3.Y, modelRotation)
            }
        })

        statsPanel = Table(skin)
        abilitiesPanel = Table(skin)

        val main = Table(skin)
        main.add(statsPanel).width(450f).growY().pad(20f).left()
        main.add(dragArea).grow()
        main.add(abilitiesPanel).width(450f).growY().pad(20f).right()
        root.add(main).grow().row()

        val bottom = Table(skin)
        val strip = Table(skin)
        
        classes.forEachIndexed { i, a ->
            val b = TextButton(a.name.replace(" ", "\n"), skin)
            b.color = a.color
            b.addListener(object : ClickListener() { 
                override fun clicked(e: InputEvent?, x: Float, y: Float) { 
                    selectedIndex = i
                    loadCharacter(i) 
                } 
            })
            strip.add(b).width(180f).height(100f).pad(10f)
        }
        
        bottom.add(strip).expandX().center().padRight(50f)
        
        val deployBtn = TextButton("DEPLOY", skin)
        deployBtn.addListener(object : ClickListener() { 
            override fun clicked(e: InputEvent?, x: Float, y: Float) { 
                game.screen = GameWorldScreen(game, classes[selectedIndex].enumVal.name) 
            } 
        })
        bottom.add(deployBtn).width(300f).height(100f).pad(20f).right()
        
        root.add(bottom).growX().height(180f)
    }

    private fun updateInfoPanels() {
        val arch = classes[selectedIndex]
        
        statsPanel.clear()
        statsPanel.defaults().pad(15f).left()
        statsPanel.add(Label(arch.name, skin, "title")).expandX().center().row()
        statsPanel.add(Label(arch.role, skin)).center().row()
        statsPanel.add(Label(arch.desc, skin)).growX().center().padBottom(30f).row()
        
        statsPanel.add(Label("Health", skin)).row()
        statsPanel.add(ProgressBar(0f, 100f, 1f, false, skin).apply { value = arch.hp }).growX().row()
        statsPanel.add(Label("Damage", skin)).row()
        statsPanel.add(ProgressBar(0f, 100f, 1f, false, skin).apply { value = arch.damage }).growX().row()
        statsPanel.add(Label("Mobility", skin)).row()
        statsPanel.add(ProgressBar(0f, 100f, 1f, false, skin).apply { value = arch.mobility }).growX().row()
        
        abilitiesPanel.clear()
        abilitiesPanel.defaults().pad(15f).left()
        abilitiesPanel.add(Label("ABILITIES", skin, "title")).expandX().center().row()
        
        arch.abilities.forEach { (name, desc) ->
            val t = Table(skin)
            t.add(Label(name, skin).apply { color = arch.color }).left().row()
            t.add(Label(desc, skin).apply { setWrap(true) }).growX().left().row()
            abilitiesPanel.add(t).growX().row()
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.01f, 0.01f, 0.05f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        
        sceneManager.update(delta)
        sceneManager.render()
        
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        cam.viewportWidth = width.toFloat()
        cam.viewportHeight = height.toFloat()
        cam.update()
    }

    override fun dispose() {
        stage.dispose()
        sceneManager.dispose()
    }
}
