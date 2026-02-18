package com.aetheria.mmo.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.aetheria.mmo.ui.Tooltip
import com.aetheria.mmo.utils.ColorUtils
import com.aetheria.mmo.utils.Constants
import com.aetheria.mmo.utils.Logger
import com.aetheria.mmo.utils.StringHelpers

/**
 * Inventory Screen
 * Displays player inventory with drag-and-drop functionality
 * Shows item stats, equipment, and gold
 */
class InventoryScreen : ScreenAdapter() {

    private lateinit var stage: Stage
    private lateinit var skin: Skin
    private lateinit var tooltip: Tooltip

    // UI Elements
    private lateinit var inventoryTable: Table
    private lateinit var equipmentTable: Table
    private lateinit var statsTable: Table
    private lateinit var goldLabel: Label

    // Inventory data (mock data for now)
    private val inventorySlots = Array(Constants.INVENTORY_SLOTS) { InventorySlot() }
    private var currentGold = 1250

    override fun show() {
        stage = Stage(ScreenViewport())
        Gdx.input.inputProcessor = stage

        skin = Skin(Gdx.files.internal("ui/uiskin.json"))
        tooltip = Tooltip(skin)

        buildUI()

        Logger.info("InventoryScreen", "Inventory screen loaded")
    }

    private fun buildUI() {
        val mainTable = Table()
        mainTable.setFillParent(true)
        stage.addActor(mainTable)

        // Title
        val titleLabel = Label("Inventory", skin).apply {
            setFontScale(1.5f)
            color = ColorUtils.CYBER_BLUE
        }

        // Create sections
        buildInventorySection()
        buildEquipmentSection()
        buildStatsSection()

        // Gold display
        goldLabel = Label("Gold: ${StringHelpers.formatNumber(currentGold)}", skin).apply {
            color = ColorUtils.LEGENDARY
        }

        // Layout
        mainTable.add(titleLabel).colspan(3).padBottom(20f).row()

        val contentTable = Table()
        contentTable.add(equipmentTable).width(200f).padRight(20f)
        contentTable.add(inventoryTable).width(400f).padRight(20f)
        contentTable.add(statsTable).width(200f)

        mainTable.add(contentTable).grow().row()
        mainTable.add(goldLabel).colspan(3).padTop(20f).row()

        // Close button
        val closeButton = TextButton("Close", skin).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    // Close inventory
                    Logger.debug("InventoryScreen", "Closing inventory")
                }
            })
        }
        mainTable.add(closeButton).width(150f).padTop(20f)

        // Add tooltip to stage
        stage.addActor(tooltip)
    }

    private fun buildInventorySection() {
        inventoryTable = Table()
        inventoryTable.background = skin.getDrawable("default-rect")

        val titleLabel = Label("Backpack", skin).apply {
            setAlignment(Align.center)
        }
        inventoryTable.add(titleLabel).colspan(6).padBottom(10f).row()

        // Create grid of inventory slots
        val slotsPerRow = 6
        for (i in inventorySlots.indices) {
            val slotButton = createInventorySlotButton(i)
            inventoryTable.add(slotButton).size(60f).pad(2f)

            if ((i + 1) % slotsPerRow == 0) {
                inventoryTable.row()
            }
        }

        // Add some mock items
        inventorySlots[0] = InventorySlot("Void Sword", "rare", 1)
        inventorySlots[1] = InventorySlot("Health Potion", "common", 5)
        inventorySlots[5] = InventorySlot("Cyber Shield", "epic", 1)
    }

    private fun buildEquipmentSection() {
        equipmentTable = Table()
        equipmentTable.background = skin.getDrawable("default-rect")

        val titleLabel = Label("Equipment", skin).apply {
            setAlignment(Align.center)
        }
        equipmentTable.add(titleLabel).padBottom(10f).row()

        // Equipment slots
        val slots = listOf("Head", "Chest", "Legs", "Weapon", "Shield", "Accessory")
        slots.forEach { slotName ->
            val slotLabel = Label(slotName, skin)
            val slotButton = TextButton("", skin).apply {
                setSize(50f, 50f)
            }
            equipmentTable.add(slotLabel).left().padRight(10f)
            equipmentTable.add(slotButton).size(50f).row()
        }
    }

    private fun buildStatsSection() {
        statsTable = Table()
        statsTable.background = skin.getDrawable("default-rect")

        val titleLabel = Label("Stats", skin).apply {
            setAlignment(Align.center)
        }
        statsTable.add(titleLabel).padBottom(10f).row()

        // Player stats
        val stats = mapOf(
            "Attack" to "45",
            "Defense" to "32",
            "Speed" to "18",
            "Crit Chance" to "15%",
            "Health" to "100/100",
            "Shield" to "50/50"
        )

        stats.forEach { (stat, value) ->
            val statLabel = Label("$stat:", skin)
            val valueLabel = Label(value, skin).apply {
                color = ColorUtils.CYBER_BLUE
            }
            statsTable.add(statLabel).left().padRight(10f)
            statsTable.add(valueLabel).right().row()
        }
    }

    private fun createInventorySlotButton(index: Int): TextButton {
        val slot = inventorySlots[index]
        val button = TextButton("", skin).apply {
            if (slot.itemName != null) {
                setText(slot.quantity.toString())
            }
        }

        // Add click listener
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onSlotClicked(index)
            }

            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                if (slot.itemName != null) {
                    showItemTooltip(slot, x, y)
                }
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                tooltip.hide()
            }
        })

        return button
    }

    private fun onSlotClicked(index: Int) {
        val slot = inventorySlots[index]
        if (slot.itemName != null) {
            Logger.debug("InventoryScreen", "Clicked item: ${slot.itemName}")
            // Handle item use/equip
        }
    }

    private fun showItemTooltip(slot: InventorySlot, x: Float, y: Float) {
        val stats = mapOf(
            "Type" to "Weapon",
            "Damage" to "+25",
            "Durability" to "100/100"
        )

        tooltip.showItem(
            itemName = slot.itemName ?: "",
            rarity = slot.rarity,
            description = "A powerful weapon forged in the void.",
            stats = stats,
            x = x,
            y = y
        )
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 0.95f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        tooltip.update(delta)

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

    /**
     * Inventory Slot Data
     */
    private data class InventorySlot(
        val itemName: String? = null,
        val rarity: String = "common",
        val quantity: Int = 0
    )
}
