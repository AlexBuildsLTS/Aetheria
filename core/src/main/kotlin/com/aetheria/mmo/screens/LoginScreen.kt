package com.aetheria.mmo.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.aetheria.mmo.utils.ColorUtils
import com.aetheria.mmo.utils.Constants
import com.aetheria.mmo.utils.Logger
import com.aetheria.mmo.utils.StringHelpers

/**
 * Login Screen
 * Handles user authentication and account creation
 * Connects to Supabase for authentication
 */
class LoginScreen : ScreenAdapter() {

    private lateinit var stage: Stage
    private lateinit var skin: Skin
    private lateinit var table: Table

    // UI Elements
    private lateinit var usernameField: TextField
    private lateinit var passwordField: TextField
    private lateinit var loginButton: TextButton
    private lateinit var registerButton: TextButton
    private lateinit var statusLabel: Label

    private var isLoggingIn = false

    override fun show() {
        stage = Stage(ScreenViewport())
        Gdx.input.inputProcessor = stage

        // Load skin (you'll need to provide a skin file)
        skin = Skin(Gdx.files.internal("ui/uiskin.json"))

        buildUI()

        Logger.info("LoginScreen", "Login screen loaded")
    }

    private fun buildUI() {
        table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        // Title
        val titleLabel = Label(Constants.GAME_NAME, skin).apply {
            setFontScale(2f)
            color = ColorUtils.CYBER_BLUE
        }

        // Username field
        val usernameLabel = Label("Username:", skin)
        usernameField = TextField("", skin).apply {
            messageText = "Enter username"
        }

        // Password field
        val passwordLabel = Label("Password:", skin)
        passwordField = TextField("", skin).apply {
            messageText = "Enter password"
            isPasswordMode = true
            setPasswordCharacter('*')
        }

        // Status label
        statusLabel = Label("", skin).apply {
            color = ColorUtils.UI_TEXT
        }

        // Login button
        loginButton = TextButton("Login", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    attemptLogin()
                }
            })
        }

        // Register button
        registerButton = TextButton("Register", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    attemptRegister()
                }
            })
        }

        // Guest button
        val guestButton = TextButton("Play as Guest", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    loginAsGuest()
                }
            })
        }

        // Layout
        table.add(titleLabel).colspan(2).padBottom(50f).row()
        table.add(usernameLabel).right().padRight(10f)
        table.add(usernameField).width(300f).padBottom(10f).row()
        table.add(passwordLabel).right().padRight(10f)
        table.add(passwordField).width(300f).padBottom(20f).row()
        table.add(statusLabel).colspan(2).padBottom(10f).row()
        table.add(loginButton).width(150f).padRight(10f)
        table.add(registerButton).width(150f).row()
        table.add(guestButton).colspan(2).width(310f).padTop(20f).row()

        // Version label
        val versionLabel = Label("v${Constants.GAME_VERSION}", skin).apply {
            setFontScale(0.8f)
            color = ColorUtils.UI_TEXT_DISABLED
        }
        table.add(versionLabel).colspan(2).padTop(30f)
    }

    private fun attemptLogin() {
        if (isLoggingIn) return

        val username = usernameField.text.trim()
        val password = passwordField.text

        // Validate input
        if (!validateInput(username, password)) {
            return
        }

        isLoggingIn = true
        setStatus("Logging in...", ColorUtils.CYBER_BLUE)
        disableButtons()

        // TODO: Implement Supabase authentication
        // For now, simulate login
        Gdx.app.postRunnable {
            Thread.sleep(1000) // Simulate network delay

            // Simulate successful login
            Logger.info("LoginScreen", "Login successful for user: $username")
            setStatus("Login successful!", ColorUtils.HEALTH_COLOR)

            // Transition to main menu or game
            // game.setScreen(MainMenuScreen())

            isLoggingIn = false
            enableButtons()
        }
    }

    private fun attemptRegister() {
        if (isLoggingIn) return

        val username = usernameField.text.trim()
        val password = passwordField.text

        // Validate input
        if (!validateInput(username, password)) {
            return
        }

        isLoggingIn = true
        setStatus("Creating account...", ColorUtils.CYBER_BLUE)
        disableButtons()

        // TODO: Implement Supabase registration
        // For now, simulate registration
        Gdx.app.postRunnable {
            Thread.sleep(1000) // Simulate network delay

            Logger.info("LoginScreen", "Registration successful for user: $username")
            setStatus("Account created! Please login.", ColorUtils.HEALTH_COLOR)

            isLoggingIn = false
            enableButtons()
        }
    }

    private fun loginAsGuest() {
        Logger.info("LoginScreen", "Logging in as guest")
        setStatus("Logging in as guest...", ColorUtils.CYBER_BLUE)

        // Generate random guest username
        val guestName = "Guest_${StringHelpers.randomString(6)}"

        // TODO: Create guest session
        // game.setScreen(MainMenuScreen())
    }

    private fun validateInput(username: String, password: String): Boolean {
        if (username.isEmpty()) {
            setStatus("Please enter a username", ColorUtils.MYTHIC)
            return false
        }

        if (!StringHelpers.isValidUsername(username)) {
            setStatus("Username must be 3-16 alphanumeric characters", ColorUtils.MYTHIC)
            return false
        }

        if (password.isEmpty()) {
            setStatus("Please enter a password", ColorUtils.MYTHIC)
            return false
        }

        if (!StringHelpers.isValidPassword(password)) {
            setStatus("Password must be at least 8 characters with letters and numbers", ColorUtils.MYTHIC)
            return false
        }

        return true
    }

    private fun setStatus(message: String, color: com.badlogic.gdx.graphics.Color) {
        statusLabel.setText(message)
        statusLabel.color = color
    }

    private fun disableButtons() {
        loginButton.isDisabled = true
        registerButton.isDisabled = true
    }

    private fun enableButtons() {
        loginButton.isDisabled = false
        registerButton.isDisabled = false
    }

    override fun render(delta: Float) {
        // Clear screen with void blue
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update and draw stage
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
