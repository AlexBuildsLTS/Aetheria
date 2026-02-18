package com.aetheria.mmo.screens

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.aetheria.mmo.AetheriaGame

/**
 * Character Selection Screen
 * Allows player to choose their class before entering the game world.
 */
class CharacterSelectScreen(val game: AetheriaGame) : ScreenAdapter() {
    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private val shapeRenderer = ShapeRenderer()

    private val classes = listOf("Vanguard", "Weaver", "Strider", "Medic")
    private val classDescriptions = mapOf(
        "Vanguard" to "Tank/Time-Bender - Heavy armor, rewinds damage",
        "Weaver" to "Glass Cannon - Holographic caster, devastating spells",
        "Strider" to "Ranger/Assassin - Stealth specialist, dual weapons",
        "Medic" to "Support - Tactical healer, nanite spray guns"
    )

    private var selectedIndex = 0
    private var buttonWidth = 300f
    private var buttonHeight = 60f
    private var buttonSpacing = 20f

    init {
        font.data.setScale(1.5f)
        Gdx.app.log("CharacterSelectScreen", "Initialized")
    }

    override fun render(delta: Float) {
        // Clear screen
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        val centerX = Gdx.graphics.width / 2f
        val startY = Gdx.graphics.height / 2f + 150f

        // Handle input
        handleInput()

        // Draw buttons
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        for (i in classes.indices) {
            val y = startY - i * (buttonHeight + buttonSpacing)
            val x = centerX - buttonWidth / 2f

            // Draw button background
            if (i == selectedIndex) {
                shapeRenderer.color = Color(0.3f, 0.5f, 0.8f, 0.8f) // Selected - bright blue
            } else {
                shapeRenderer.color = Color(0.2f, 0.2f, 0.3f, 0.6f) // Unselected - dark
            }
            shapeRenderer.rect(x, y, buttonWidth, buttonHeight)

            // Draw button border
            shapeRenderer.end()
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            if (i == selectedIndex) {
                shapeRenderer.color = Color.CYAN
            } else {
                shapeRenderer.color = Color.GRAY
            }
            shapeRenderer.rect(x, y, buttonWidth, buttonHeight)
            shapeRenderer.end()
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        }

        shapeRenderer.end()

        // Draw text
        batch.begin()
        font.color = Color.WHITE

        // Title
        val title = "AETHERIA: VOID HORIZON"
        val titleWidth = font.data.getGlyph('A').width * title.length * 1.5f
        font.draw(batch, title, centerX - titleWidth / 2f, Gdx.graphics.height - 50f)

        val subtitle = "Select Your Ascended Class"
        font.data.setScale(1.2f)
        font.draw(batch, subtitle, centerX - 150f, Gdx.graphics.height - 100f)
        font.data.setScale(1.5f)

        // Class names and descriptions
        for (i in classes.indices) {
            val y = startY - i * (buttonHeight + buttonSpacing)
            val className = classes[i]

            if (i == selectedIndex) {
                font.color = Color.CYAN
            } else {
                font.color = Color.WHITE
            }

            font.draw(batch, className, centerX - buttonWidth / 2f + 20f, y + buttonHeight - 15f)

            // Description
            font.data.setScale(0.8f)
            font.color = Color.LIGHT_GRAY
            font.draw(batch, classDescriptions[className], centerX - buttonWidth / 2f + 20f, y + 20f)
            font.data.setScale(1.5f)
        }

        // Instructions
        font.data.setScale(1f)
        font.color = Color.YELLOW
        font.draw(batch, "UP/DOWN: Select | ENTER: Confirm | ESC: Quit", centerX - 200f, 50f)
        font.data.setScale(1.5f)

        batch.end()
    }

    private fun handleInput() {
        // Navigate with arrow keys or number keys
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            selectedIndex = (selectedIndex - 1 + classes.size) % classes.size
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            selectedIndex = (selectedIndex + 1) % classes.size
        }

        // Number keys for direct selection
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) selectedIndex = 0
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) selectedIndex = 1
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) selectedIndex = 2
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) selectedIndex = 3

        // Confirm selection
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            val selectedClass = classes[selectedIndex]
            Gdx.app.log("CharacterSelect", "Selected: $selectedClass")
            game.screen = GameWorldScreen(game, selectedClass)
        }

        // Quit
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit()
        }
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        shapeRenderer.dispose()
    }
}
