package com.aetheria.mmo.screens

import com.aetheria.mmo.AetheriaGame
import com.aetheria.mmo.screens.GameWorldScreen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align

// Keep your original imports
import com.aetheria.mmo.utils.Constants
import com.aetheria.mmo.utils.Logger

class LoginScreen(private val game: AetheriaGame) : ScreenAdapter() {

    private val stage = Stage(ScreenViewport())
    private lateinit var skin: Skin
    private lateinit var backgroundShader: ShaderProgram
    private var time = 0f

    // UI Elements
    private lateinit var usernameField: TextField
    private lateinit var passwordField: TextField
    private lateinit var loginButton: TextButton
    private lateinit var registerButton: TextButton
    private lateinit var statusLabel: Label
    private var isLoggingIn = false

    // --- SHADER CODE (VISUALS) ---
    private val vertexShader = """
        attribute vec4 a_position;
        attribute vec4 a_color;
        attribute vec2 a_texCoord0;
        uniform mat4 u_projTrans;
        varying vec4 v_color;
        varying vec2 v_texCoords;
        void main() {
            v_color = a_color;
            v_texCoords = a_texCoord0;
            gl_Position = u_projTrans * a_position;
        }
    """.trimIndent()

    private val fragmentShader = """
        #ifdef GL_ES
        precision mediump float;
        #endif
        varying vec4 v_color;
        varying vec2 v_texCoords;
        uniform float u_time;
        
        void main() {
            vec2 uv = v_texCoords;
            // Create a flowing void energy effect
            float wave = sin(uv.x * 10.0 + u_time) * 0.05;
            float wave2 = cos(uv.y * 10.0 + u_time * 0.5) * 0.05;
            
            // Base Color: Deep Void (Dark Purple/Black)
            vec3 color = vec3(0.05, 0.0, 0.1); 
            
            // Neon Lines (Cyber Blue & Purple)
            color += vec3(0.0, 0.8, 1.0) * (0.01 / abs(uv.y - 0.5 + wave)); 
            color += vec3(0.6, 0.0, 1.0) * (0.01 / abs(uv.x - 0.5 + wave2));
            
            gl_FragColor = v_color * vec4(color, 1.0);
        }
    """.trimIndent()

    override fun show() {
        Gdx.input.inputProcessor = stage

        // Load Skin
        skin = Skin(Gdx.files.internal("ui/skin/metalui.json"))

        // Compile Shader
        backgroundShader = ShaderProgram(vertexShader, fragmentShader)
        if (!backgroundShader.isCompiled) {
            Gdx.app.error("Shader", backgroundShader.log)
        }

        buildUI()
        Logger.info("LoginScreen", "AAA Login Screen Loaded")
    }

    private fun buildUI() {
        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

        // Create a semi-transparent "Glass" background for the login box
        val glassPixmap = createGlassPixmap()
        val glassTexture = Texture(glassPixmap)
        val glassStyle = TextureRegionDrawable(glassTexture)
        glassPixmap.dispose()

        val container = Table()
        container.background = glassStyle
        container.pad(40f)

        // Title (Your Logic + My Styling)
        val titleLabel = Label(Constants.GAME_NAME.uppercase(), Label.LabelStyle(skin.getFont("default-font"), Color.CYAN)).apply {
            setFontScale(2.5f)
        }
        val subtitleLabel = Label("VOID HORIZON", Label.LabelStyle(skin.getFont("default-font"), Color.PURPLE)).apply {
            setFontScale(1.2f)
        }

        // Inputs
        usernameField = TextField("", skin).apply { messageText = "AGENT ID" }
        passwordField = TextField("", skin).apply {
            messageText = "PASSCODE"
            isPasswordMode = true
            setPasswordCharacter('•') // Modern dot instead of asterisk
        }

        statusLabel = Label("SYSTEM READY", skin).apply {
            color = Color.GRAY
            setAlignment(Align.center)
        }

        // Buttons
        loginButton = TextButton("INITIALIZE LINK", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    attemptLogin()
                }
            })
        }

        registerButton = TextButton("NEW AGENT REGISTRATION", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    attemptRegister()
                }
            })
        }

        val guestButton = TextButton("[ BYPASS AUTH ]", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    loginAsGuest()
                }
            })
        }

        // --- LAYOUT ---
        container.add(titleLabel).padBottom(5f).row()
        container.add(subtitleLabel).padBottom(40f).row()

        container.add(usernameField).width(350f).height(50f).padBottom(15f).row()
        container.add(passwordField).width(350f).height(50f).padBottom(20f).row()

        container.add(loginButton).width(350f).height(60f).padBottom(10f).row()
        container.add(registerButton).width(350f).height(40f).padBottom(10f).row()
        container.add(guestButton).width(200f).padTop(10f).row()
        container.add(statusLabel).padTop(20f).row()

        root.add(container)
    }

    private fun attemptLogin() {
        if (isLoggingIn) return
        val username = usernameField.text.trim()
        val password = passwordField.text

        if (!validateInput(username, password)) return

        isLoggingIn = true
        setStatus("AUTHENTICATING...", Color.CYAN)
        disableButtons()

        // Simulate Net Code
        Gdx.app.postRunnable {
            setStatus("ACCESS GRANTED", Color.GREEN) // Simulate successful login
            game.screen = CharacterSelectScreen(game) // Transition to character selection
        }
    }

    private fun attemptRegister() {
        setStatus("CONNECTING TO REGISTRY...", Color.YELLOW)
    }

    private fun loginAsGuest() {
        setStatus("GUEST ACCESS GRANTED", Color.ORANGE)
        game.screen = CharacterSelectScreen(game) // Transition to character selection
    }

    private fun validateInput(u: String, p: String): Boolean {
        if (u.isEmpty()) { setStatus("ERROR: ID REQUIRED", Color.RED); return false }
        if (p.isEmpty()) { setStatus("ERROR: PASSCODE REQUIRED", Color.RED); return false }
        return true
    }

    private fun setStatus(msg: String, color: Color) {
        statusLabel.setText(msg)
        statusLabel.color = color
    }

    private fun disableButtons() {
        loginButton.isDisabled = true
        registerButton.isDisabled = true
    }

    private fun createGlassPixmap(): Pixmap {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(0f, 0f, 0f, 0.7f) // 70% opacity black
        pixmap.fill()
        return pixmap
    }

    override fun render(delta: Float) {
        time += delta

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (::backgroundShader.isInitialized) {
            game.batch?.shader = backgroundShader
            game.batch?.begin()
            backgroundShader.setUniformf("u_time", time)
            game.batch?.draw(skin.getRegion("white"), 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            game.batch?.end()
            game.batch?.shader = null
        }

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        if (::skin.isInitialized) skin.dispose()
        if (::backgroundShader.isInitialized) backgroundShader.dispose()
    }
}
