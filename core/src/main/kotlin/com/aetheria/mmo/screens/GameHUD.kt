package com.aetheria.mmo.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.Application
import com.badlogic.gdx.utils.Align
import com.aetheria.mmo.managers.SkinManager
import com.aetheria.mmo.events.EventQueue
import com.aetheria.mmo.events.SkillCastEvent
import com.badlogic.ashley.core.Entity

/**
 * AAA COMBAT HUD (CoD Mobile Style)
 * Dual Joysticks + 5 Action Buttons + Status Bars
 * Uses Procedural Assets if files are missing.
 */
class GameHUD : InputAdapter() {
    val stage = Stage(FitViewport(1920f, 1080f))
    private val skin = SkinManager.skin 
    
    private var moveStick: Touchpad? = null
    private var aimStick: Touchpad? = null
    
    private lateinit var hpBar: ProgressBar
    private lateinit var staminaBar: ProgressBar
    
    // Player entity reference for firing events
    var playerEntity: Entity? = null
    
    private val isDesktop = Gdx.app.type == Application.ApplicationType.Desktop

    init {
        buildLayout()
    }

    private fun buildLayout() {
        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

        // --- TOP LEFT: STATUS BARS ---
        val statusTable = Table()
        statusTable.defaults().pad(5f).left()
        
        // Procedural Health Bar Style
        val hpStyle = ProgressBar.ProgressBarStyle(
            createDrawable(Color.BLACK, 300, 25),
            createDrawable(Color.RED, 0, 25) 
        )
        hpStyle.knobBefore = createDrawable(Color.RED, 300, 25)
        
        hpBar = ProgressBar(0f, 100f, 1f, false, hpStyle)
        hpBar.value = 100f
        statusTable.add(Label("HP", skin)).width(50f)
        statusTable.add(hpBar).width(400f).height(30f).row()
        
        // Procedural Stamina Bar Style
        val staStyle = ProgressBar.ProgressBarStyle(
            createDrawable(Color.BLACK, 300, 15),
            createDrawable(Color.YELLOW, 0, 15)
        )
        staStyle.knobBefore = createDrawable(Color.YELLOW, 300, 15)
        
        staminaBar = ProgressBar(0f, 100f, 1f, false, staStyle)
        staminaBar.value = 100f
        statusTable.add(Label("STA", skin)).width(50f)
        statusTable.add(staminaBar).width(300f).height(15f).row()
        
        root.add(statusTable).expand().top().left().pad(30f)
        
        // --- CONTROLS LAYER ---
        val controls = Table()
        
        // Joystick Style 
        val joyBg = createCircleDrawable(Color(0f, 0f, 0f, 0.5f), 200)
        val joyKnob = createCircleDrawable(Color(1f, 1f, 1f, 0.8f), 60)
        val joyStyle = Touchpad.TouchpadStyle(joyBg, joyKnob)
        
        // LEFT STICK (Movement)
        val move = Touchpad(10f, joyStyle)
        moveStick = move
        if (!isDesktop) controls.add(move).size(250f).bottom().left().pad(50f)
        else controls.add().size(250f).bottom().left().pad(50f) // Spacer
        
        controls.add().expandX() // Spacer
        
        // RIGHT STICK (Aiming)
        val aim = Touchpad(10f, joyStyle)
        aimStick = aim
        if (!isDesktop) controls.add(aim).size(250f).bottom().right().pad(50f)
        
        root.add(controls).grow().bottom()

        // --- SKILL BUTTONS (Bottom Right Overlay) ---
        val skillTable = Table()
        // Skills: Kinetic Swing, Gravity Slam, Shield Throw, Rewind, Event Horizon
        val skills: List<Triple<String, String, Color>> = listOf(
            Triple("Kinetic Swing", "Q", Color.CYAN),
            Triple("Gravity Slam", "E", Color.MAGENTA),
            Triple("Shield Throw", "R", Color.ORANGE),
            Triple("Rewind", "F", Color.LIME),
            Triple("Event Horizon", "SPACE", Color.WHITE)
        )
        
        for ((name, key, color) in skills) {
            val btnStyle = ImageButton.ImageButtonStyle()
            btnStyle.up = createCircleDrawable(color, 100)
            btnStyle.down = createCircleDrawable(Color.WHITE, 100)
            
            val btn = ImageButton(btnStyle)
            // Center label on button
            val lbl = Label(key, skin)
            lbl.setFontScale(1.5f)
            lbl.setAlignment(Align.center)
            btn.add(lbl).center()
            
            btn.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    playerEntity?.let {
                        EventQueue.post(SkillCastEvent(it, name))
                    }
                }
            })
            
            skillTable.add(btn).size(90f).pad(10f).bottom()
        }
        
        // Position skills relative to aim stick or screen corner
        // Adjusted position to not overlap with right stick
        root.addActor(skillTable)
        skillTable.setPosition(Gdx.graphics.width - 700f, 50f) 
        skillTable.pack()
    }
    
    private fun createDrawable(color: Color, width: Int, height: Int): TextureRegionDrawable {
        val w = if (width > 0) width else 1
        val h = if (height > 0) height else 1
        val pixmap = Pixmap(w, h, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fill()
        val tex = Texture(pixmap)
        pixmap.dispose()
        return TextureRegionDrawable(tex)
    }

    private fun createCircleDrawable(color: Color, size: Int): TextureRegionDrawable {
        val pixmap = Pixmap(size, size, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fillCircle(size/2, size/2, size/2)
        val tex = Texture(pixmap)
        pixmap.dispose()
        return TextureRegionDrawable(tex)
    }

    fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }
    
    fun updatePlayerStats(hp: Float, maxHp: Float, sta: Float, maxSta: Float) {
        hpBar.setRange(0f, maxHp)
        hpBar.value = hp
        staminaBar.setRange(0f, maxSta)
        staminaBar.value = sta
    }
    
    fun getMovementInput(): Vector2 = Vector2(moveStick?.knobPercentX ?: 0f, moveStick?.knobPercentY ?: 0f)
    fun getAimInput(): Vector2 = Vector2(aimStick?.knobPercentX ?: 0f, aimStick?.knobPercentY ?: 0f)
    
    fun resize(width: Int, height: Int) = stage.viewport.update(width, height, true)
    
    fun dispose() {
        stage.dispose()
    }
}
