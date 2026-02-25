package com.aetheria.mmo.managers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

/**
 * EMERGENCY ROBUST SKIN MANAGER
 * Procedurally generates ALL required UI assets to prevent crashes.
 */
object SkinManager {
    lateinit var skin: Skin
    private var isInitialized = false

    fun init() {
        if (isInitialized) return
        
        skin = Skin()
        
        // 1. Create a 1x1 White Pixel for ALL fallbacks
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        val whiteTex = Texture(pixmap)
        val whiteDrawable = TextureRegionDrawable(whiteTex)
        skin.add("white", whiteTex)
        skin.add("default", whiteDrawable)
        pixmap.dispose()

        // 2. High-Quality Font
        val font = BitmapFont()
        skin.add("default", font)
        skin.add("title", font)

        // 3. Register ALL potential names to "white" to prevent GdxRuntimeException
        val requiredNames = listOf(
            "button-up", "button-down", "button-over", 
            "window", "dialog", "field-bg", "selection", "cursor",
            "default-rect", "check-on", "check-off"
        )
        
        requiredNames.forEach { name ->
            skin.add(name, whiteDrawable)
        }

        // 4. Styles
        val lbStyle = Label.LabelStyle(font, Color.WHITE)
        skin.add("default", lbStyle)
        skin.add("title", lbStyle)

        val btnStyle = TextButton.TextButtonStyle()
        btnStyle.font = font
        btnStyle.up = whiteDrawable
        btnStyle.down = whiteDrawable
        btnStyle.over = whiteDrawable
        skin.add("default", btnStyle)

        val tfStyle = TextField.TextFieldStyle()
        tfStyle.font = font
        tfStyle.fontColor = Color.WHITE
        tfStyle.background = whiteDrawable
        tfStyle.cursor = whiteDrawable
        tfStyle.selection = whiteDrawable
        skin.add("default", tfStyle)

        val pbStyle = ProgressBar.ProgressBarStyle()
        pbStyle.background = whiteDrawable
        pbStyle.knobBefore = whiteDrawable
        skin.add("default-horizontal", pbStyle)

        isInitialized = true
    }
}
