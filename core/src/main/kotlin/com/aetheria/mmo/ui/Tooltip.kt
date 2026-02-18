package com.aetheria.mmo.ui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.Align
import com.aetheria.mmo.utils.ColorUtils
import com.aetheria.mmo.utils.Constants

/**
 * Tooltip Widget
 * Displays contextual information when hovering over UI elements or game objects
 * Supports rich text formatting, item stats, and ability descriptions
 */
class Tooltip(skin: Skin) : Window("", skin) {

    private val contentTable = Table()
    private val titleLabel: Label
    private val descriptionLabel: Label
    private val statsTable = Table()

    private var showDelay = Constants.TOOLTIP_DELAY
    private var currentDelay = 0f
    private var isVisible = false

    init {
        // Configure window
        isMovable = false
        isModal = false
        setKeepWithinStage(true)

        // Title label
        titleLabel = Label("", skin).apply {
            setAlignment(Align.center)
            setFontScale(1.2f)
        }

        // Description label
        descriptionLabel = Label("", skin).apply {
            setAlignment(Align.left)
            wrap = true
        }

        // Build layout
        contentTable.pad(10f)
        contentTable.add(titleLabel).growX().row()
        contentTable.add(descriptionLabel).width(300f).pad(5f).row()
        contentTable.add(statsTable).growX().row()

        add(contentTable)
        pack()
        isVisible = false
    }

    /**
     * Show tooltip with simple text
     */
    fun show(text: String, x: Float, y: Float) {
        titleLabel.setText("")
        descriptionLabel.setText(text)
        statsTable.clear()

        setPosition(x, y)
        pack()
        isVisible = true
        currentDelay = 0f
    }

    /**
     * Show tooltip with title and description
     */
    fun show(title: String, description: String, x: Float, y: Float) {
        titleLabel.setText(title)
        descriptionLabel.setText(description)
        statsTable.clear()

        setPosition(x, y)
        pack()
        isVisible = true
        currentDelay = 0f
    }

    /**
     * Show item tooltip with stats
     */
    fun showItem(
        itemName: String,
        rarity: String,
        description: String,
        stats: Map<String, String>,
        x: Float,
        y: Float
    ) {
        // Set title with rarity color
        val rarityColor = ColorUtils.getRarityColor(rarity)
        titleLabel.setText(itemName)
        titleLabel.color = rarityColor

        // Set description
        descriptionLabel.setText(description)

        // Build stats table
        statsTable.clear()
        stats.forEach { (stat, value) ->
            val statLabel = Label("$stat: $value", skin).apply {
                setFontScale(0.9f)
            }
            statsTable.add(statLabel).left().row()
        }

        setPosition(x, y)
        pack()
        isVisible = true
        currentDelay = 0f
    }

    /**
     * Show ability tooltip
     */
    fun showAbility(
        abilityName: String,
        description: String,
        cooldown: Float,
        manaCost: Int,
        damage: String?,
        x: Float,
        y: Float
    ) {
        titleLabel.setText(abilityName)
        titleLabel.color = ColorUtils.CYBER_BLUE

        descriptionLabel.setText(description)

        statsTable.clear()

        if (damage != null) {
            statsTable.add(Label("Damage: $damage", skin)).left().row()
        }

        statsTable.add(Label("Cooldown: ${cooldown}s", skin)).left().row()
        statsTable.add(Label("Mana Cost: $manaCost", skin)).left().row()

        setPosition(x, y)
        pack()
        isVisible = true
        currentDelay = 0f
    }

    /**
     * Show buff/debuff tooltip
     */
    fun showBuff(
        buffName: String,
        description: String,
        duration: Float,
        stacks: Int,
        x: Float,
        y: Float
    ) {
        titleLabel.setText(buffName)
        descriptionLabel.setText(description)

        statsTable.clear()
        statsTable.add(Label("Duration: ${String.format("%.1f", duration)}s", skin)).left().row()

        if (stacks > 1) {
            statsTable.add(Label("Stacks: $stacks", skin)).left().row()
        }

        setPosition(x, y)
        pack()
        isVisible = true
        currentDelay = 0f
    }

    /**
     * Hide tooltip
     */
    fun hide() {
        isVisible = false
        currentDelay = 0f
    }

    /**
     * Update tooltip (handle delay)
     */
    fun update(deltaTime: Float) {
        if (isVisible && currentDelay < showDelay) {
            currentDelay += deltaTime
        }
    }

    /**
     * Check if tooltip should be rendered
     */
    fun shouldRender(): Boolean {
        return isVisible && currentDelay >= showDelay
    }

    /**
     * Set show delay
     */
    fun setShowDelay(delay: Float) {
        this.showDelay = delay
    }

    /**
     * Position tooltip near mouse cursor
     */
    fun positionNearCursor(mouseX: Float, mouseY: Float, screenWidth: Float, screenHeight: Float) {
        var x = mouseX + 15f
        var y = mouseY - height - 15f

        // Keep within screen bounds
        if (x + width > screenWidth) {
            x = mouseX - width - 15f
        }
        if (y < 0) {
            y = mouseY + 15f
        }

        setPosition(x, y)
    }

    /**
     * Clear tooltip content
     */
    override fun clear() {
        titleLabel.setText("")
        descriptionLabel.setText("")
        statsTable.clear()
        hide()
    }
}
