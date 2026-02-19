package com.aetheria.mmo.screens

import com.aetheria.mmo.AetheriaGame
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport

class CharacterSelectScreen(private val game: AetheriaGame) : ScreenAdapter() {

    // --- UI (2D) ---
    private val stage = Stage(ScreenViewport())
    private lateinit var skin: Skin

    // --- 3D SCENE ---
    private lateinit var modelBatch: ModelBatch
    private lateinit var environment: Environment
    private lateinit var cam: PerspectiveCamera

    // Models
    private val models = HashMap<String, Model>()
    private var currentInstance: ModelInstance? = null

    // Data Class for Archetypes
    data class Archetype(
        val id: String,
        val name: String,
        val role: String,
        val desc: String,
        val color: Color,
        val modelType: Int // Helper for placeholder shape
    )

    // YOUR DEFINED CLASSES
    private val classes = listOf(
        Archetype("chrono", "CHRONO VANGUARD", "Tank / Heavy DPS", "Time-bending warrior. Uses shields to freeze damage or hammers to shatter reality.", Color.GOLD, 0),
        Archetype("nano", "NANO WEAVER", "Medic / Tech DPS", "Bio-hacker. Deploys healing mists or acidic nanite swarms with dual pistols.", Color.LIME, 1),
        Archetype("void", "VOID STALKER", "Stealth / Burst", "Shadow assassin. Masters of invisibility and critical backstabs.", Color.PURPLE, 2),
        Archetype("aether", "AETHER MAGUS", "Mage / Control", "Elementalist. Weaves void magic to control the battlefield from afar.", Color.CYAN, 3)
    )

    private var selectedIndex = 0

    override fun show() {
        // 1. Setup Input
        Gdx.input.inputProcessor = stage
        skin = Skin(Gdx.files.internal("ui/skin/metalui.json"))

        // 2. Setup 3D Environment
        modelBatch = ModelBatch()
        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

        // 3. Setup Camera (Isometric-ish view)
        cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.position.set(5f, 5f, 5f)
        cam.lookAt(0f, 0f, 0f)
        cam.near = 1f
        cam.far = 300f
        cam.update()

        // 4. Generate Placeholder Models (So it runs NOW)
        generatePlaceholderModels()
        updateModelDisplay()

        // 5. Build UI
        buildUI()
    }

    private fun generatePlaceholderModels() {
        val mb = ModelBuilder()
        // Chrono (Box)
        models["chrono"] = mb.createBox(2f, 4f, 2f, Material(ColorAttribute.createDiffuse(Color.GOLD)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        // Nano (Cylinder)
        models["nano"] = mb.createCylinder(2f, 4f, 2f, 16, Material(ColorAttribute.createDiffuse(Color.LIME)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        // Void (Cone)
        models["void"] = mb.createCone(2f, 4f, 2f, 16, Material(ColorAttribute.createDiffuse(Color.PURPLE)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        // Aether (Sphere)
        models["aether"] = mb.createSphere(3f, 3f, 3f, 24, 24, Material(ColorAttribute.createDiffuse(Color.CYAN)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
    }

    private fun updateModelDisplay() {
        val arch = classes[selectedIndex]
        val model = models[arch.id]

        if (model != null) {
            currentInstance = ModelInstance(model)
            // Center it
            currentInstance!!.transform.setToTranslation(0f, 0f, 0f)
        }
    }

    private fun buildUI() {
        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

        // LEFT PANEL: Selection List
        val listTable = Table()
        listTable.background = skin.newDrawable("white", 0f, 0f, 0f, 0.5f) // Semi-transparent black

        classes.forEachIndexed { index, archetype ->
            val btn = TextButton(archetype.name, skin).apply {
                color = archetype.color
                addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        selectedIndex = index
                        updateModelDisplay()
                        updateInfoPanel(root) // Refresh text
                    }
                })
            }
            listTable.add(btn).width(220f).height(50f).pad(10f).row()
        }

        // RIGHT PANEL: Info & Deploy
        val infoTable = Table()
        infoTable.name = "InfoPanel"
        infoTable.background = skin.newDrawable("white", 0f, 0f, 0f, 0.5f)

        // Add panels to root
        root.add(listTable).expandY().left().top().pad(20f).width(250f)
        root.add().expandX() // Spacer for the 3D model in the center
        root.add(infoTable).expandY().right().bottom().pad(20f).width(350f)

        // Initial populate
        updateInfoPanel(root)
    }

    private fun updateInfoPanel(root: Table) {
        val infoTable = root.findActor<Table>("InfoPanel")
        infoTable.clear()

        val arch = classes[selectedIndex]

        val nameLbl = Label(arch.name, skin).apply { setFontScale(2f); color = arch.color; setAlignment(Align.center) }
        val roleLbl = Label("CLASS: ${arch.role}", skin).apply { color = Color.LIGHT_GRAY }
        val descLbl = Label(arch.desc, skin).apply { wrap = true; setAlignment(Align.center) }

        val deployBtn = TextButton(">> DEPLOY AGENT <<", skin).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    Gdx.app.log("Game", "Deploying as ${arch.name}")
                }
            })
        }

        infoTable.add(nameLbl).padBottom(10f).growX().row()
        infoTable.add(roleLbl).padBottom(20f).row()
        infoTable.add(descLbl).width(300f).padBottom(40f).row()
        infoTable.add(deployBtn).width(250f).height(60f)
    }

    override fun render(delta: Float) {
        // 1. Clear Screen (Dark Void Blue)
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        // 2. Render 3D Model
        if (currentInstance != null) {
            // Rotate model slightly for effect
            currentInstance!!.transform.rotate(Vector3.Y, 15f * delta)

            modelBatch.begin(cam)
            modelBatch.render(currentInstance, environment)
            modelBatch.end()
        }

        // 3. Render UI
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
        modelBatch.dispose()
        models.values.forEach { it.dispose() }
    }
}