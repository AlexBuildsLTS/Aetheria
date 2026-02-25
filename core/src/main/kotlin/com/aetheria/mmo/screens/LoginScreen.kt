package com.aetheria.mmo.screens

import com.aetheria.mmo.AetheriaGame
import com.aetheria.mmo.managers.SkinManager
import com.aetheria.mmo.net.SupabaseClient
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * AAA GLITCH-PUNK LOGIN SCREEN
 * Uses SkinManager for robust UI rendering.
 */
class LoginScreen(private val game: AetheriaGame) : ScreenAdapter() {

    private val stage = Stage(FitViewport(1920f, 1080f))
    private val skin = SkinManager.skin
    private lateinit var glitchShader: ShaderProgram
    private var time = 0f
    
    private lateinit var statusLabel: Label
    private lateinit var usernameField: TextField
    private lateinit var passwordField: TextField

    private val scope = CoroutineScope(Dispatchers.Main)

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
        uniform sampler2D u_texture;
        uniform float u_time;
        
        float rand(vec2 co) {
            return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
        }

        void main() {
            vec2 uv = v_texCoords;
            float glitch = step(0.98, rand(vec2(u_time * 0.1, floor(uv.y * 50.0))));
            uv.x += glitch * sin(u_time * 100.0) * 0.01;
            
            float r = texture2D(u_texture, uv + vec2(0.003, 0.0)).r;
            float g = texture2D(u_texture, uv).g;
            float b = texture2D(u_texture, uv - vec2(0.003, 0.0)).b;
            
            vec3 color = vec3(r, g, b);
            float scanline = sin(uv.y * 1000.0) * 0.05;
            color -= scanline;
            
            gl_FragColor = v_color * vec4(color, 1.0);
        }
    """.trimIndent()

    override fun show() {
        Gdx.input.inputProcessor = stage
        glitchShader = ShaderProgram(vertexShader, fragmentShader)
        buildUI()
    }

    private fun buildUI() {
        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

        val mainTable = Table(skin)
        // mainTable.background = skin.getDrawable("dialog") // REMOVED PER DIRECTIVE
        mainTable.pad(80f)

        val titleLabel = Label("AETHERIA: REBORN", skin, "title")
        mainTable.add(titleLabel).padBottom(60f).row()

        usernameField = TextField("", skin).apply { messageText = "USER ID" }
        passwordField = TextField("", skin).apply { 
            messageText = "PASSKEY"
            isPasswordMode = true 
        }

        mainTable.add(usernameField).width(700f).height(100f).padBottom(30f).row()
        mainTable.add(passwordField).width(700f).height(100f).padBottom(50f).row()

        val loginBtn = TextButton("INITIALIZE LINK", skin)
        loginBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                attemptLogin()
            }
        })
        mainTable.add(loginBtn).width(700f).height(120f).padBottom(25f).row()

        val registerBtn = TextButton("NEW AGENT REGISTRATION", skin)
        registerBtn.color = Color.PINK
        registerBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                attemptRegister()
            }
        })
        mainTable.add(registerBtn).width(700f).height(100f).padBottom(25f).row()

        val bypassBtn = TextButton(">> BYPASS AUTH (DEVELOPER MODE) <<", skin)
        bypassBtn.color = Color.ORANGE
        bypassBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                // BYPASS AUTH: REWIRED TO DIRECTLY LAUNCH THE GAME WORLD
                game.screen = GameWorldScreen(game, "Vanguard") 
            }
        })
        mainTable.add(bypassBtn).width(800f).height(140f).padBottom(40f).row()

        statusLabel = Label("SYSTEM READY", skin)
        statusLabel.color = Color.GRAY
        mainTable.add(statusLabel).padTop(20f)

        root.add(mainTable)
    }

    private fun attemptLogin() {
        val email = usernameField.text
        val pass = passwordField.text
        if (email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("ERROR: INPUT REQUIRED")
            statusLabel.color = Color.RED
            return
        }
        statusLabel.setText("ATTEMPTING AUTH...")
        statusLabel.color = Color.CYAN
        scope.launch {
            try {
                SupabaseClient.client.auth.signInWith(Email) {
                    this.email = email
                    password = pass
                }
                Gdx.app.postRunnable { game.screen = CharacterSelectScreen(game) }
            } catch (e: Exception) {
                Gdx.app.postRunnable {
                    statusLabel.setText("AUTH FAILED: ${e.message}")
                    statusLabel.color = Color.RED
                }
            }
        }
    }

    private fun attemptRegister() {
        val email = usernameField.text
        val pass = passwordField.text
        if (email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("ERROR: INPUT REQUIRED")
            statusLabel.color = Color.RED
            return
        }
        statusLabel.setText("CREATING AGENT...")
        statusLabel.color = Color.PINK
        scope.launch {
            try {
                SupabaseClient.client.auth.signUpWith(Email) {
                    this.email = email
                    password = pass
                }
                Gdx.app.postRunnable {
                    statusLabel.setText("REGISTRATION SUCCESS! PLEASE LOG IN.")
                    statusLabel.color = Color.GREEN
                }
            } catch (e: Exception) {
                Gdx.app.postRunnable {
                    statusLabel.setText("REGISTRATION FAILED: ${e.message}")
                    statusLabel.color = Color.RED
                }
            }
        }
    }

    override fun render(delta: Float) {
        time += delta
        Gdx.gl.glClearColor(0f, 0f, 0.05f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        val batch = game.batch ?: SpriteBatch()
        batch.projectionMatrix = stage.viewport.camera.combined
        batch.shader = glitchShader
        batch.begin()
        glitchShader.setUniformf("u_time", time)
        batch.draw(skin.getRegion("white"), 0f, 0f, 1920f, 1080f)
        batch.end()
        batch.shader = null
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) = stage.viewport.update(width, height, true)
    override fun dispose() {
        stage.dispose()
        glitchShader.dispose()
    }
}
