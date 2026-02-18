package com.aetheria.mmo.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter

/**
 * Save Manager
 * Handles saving and loading game data
 * Supports multiple save slots and auto-save
 */
object SaveManager : Disposable {

    private val json = Json().apply {
        setOutputType(JsonWriter.OutputType.json)
        setUsePrototypes(false)
    }

    private const val SAVE_DIRECTORY = "saves/"
    private const val AUTO_SAVE_FILE = "autosave.json"
    private const val MAX_SAVE_SLOTS = 5

    data class SaveData(
        var playerName: String = "",
        var level: Int = 1,
        var xp: Long = 0,
        var positionX: Float = 0f,
        var positionY: Float = 0f,
        var positionZ: Float = 0f,
        var currentZone: String = "rust_lands",
        var playTime: Long = 0,
        var lastSaved: Long = System.currentTimeMillis(),
        var inventory: MutableMap<String, Int> = mutableMapOf(),
        var equippedItems: MutableMap<String, String> = mutableMapOf(),
        var completedQuests: MutableList<String> = mutableListOf(),
        var activeQuests: MutableList<String> = mutableListOf(),
        var unlockedAbilities: MutableList<String> = mutableListOf(),
        var stats: MutableMap<String, Float> = mutableMapOf()
    )

    private var currentSaveData: SaveData? = null
    private var autoSaveInterval = 300f // 5 minutes
    private var timeSinceLastSave = 0f

    /**
     * Initializes the save manager
     */
    fun initialize() {
        // Ensure save directory exists
        val saveDir = Gdx.files.local(SAVE_DIRECTORY)
        if (!saveDir.exists()) {
            saveDir.mkdirs()
        }

        Gdx.app.log("SaveManager", "Save manager initialized")
    }

    /**
     * Creates a new save
     */
    fun createNewSave(playerName: String): SaveData {
        currentSaveData = SaveData(playerName = playerName)
        return currentSaveData!!
    }

    /**
     * Saves game data to a slot
     */
    fun saveGame(slot: Int = 0): Boolean {
        if (currentSaveData == null) {
            Gdx.app.error("SaveManager", "No save data to save")
            return false
        }

        return try {
            val fileName = if (slot == 0) AUTO_SAVE_FILE else "save_$slot.json"
            val file = Gdx.files.local(SAVE_DIRECTORY + fileName)

            currentSaveData!!.lastSaved = System.currentTimeMillis()
            val jsonData = json.toJson(currentSaveData)

            file.writeString(jsonData, false)
            Gdx.app.log("SaveManager", "Game saved to slot $slot")
            true
        } catch (e: Exception) {
            Gdx.app.error("SaveManager", "Failed to save game", e)
            false
        }
    }

    /**
     * Loads game data from a slot
     */
    fun loadGame(slot: Int = 0): SaveData? {
        return try {
            val fileName = if (slot == 0) AUTO_SAVE_FILE else "save_$slot.json"
            val file = Gdx.files.local(SAVE_DIRECTORY + fileName)

            if (!file.exists()) {
                Gdx.app.log("SaveManager", "Save file not found: $fileName")
                return null
            }

            val jsonData = file.readString()
            currentSaveData = json.fromJson(SaveData::class.java, jsonData)

            Gdx.app.log("SaveManager", "Game loaded from slot $slot")
            currentSaveData
        } catch (e: Exception) {
            Gdx.app.error("SaveManager", "Failed to load game", e)
            null
        }
    }

    /**
     * Deletes a save slot
     */
    fun deleteSave(slot: Int): Boolean {
        return try {
            val fileName = if (slot == 0) AUTO_SAVE_FILE else "save_$slot.json"
            val file = Gdx.files.local(SAVE_DIRECTORY + fileName)

            if (file.exists()) {
                file.delete()
                Gdx.app.log("SaveManager", "Save deleted: slot $slot")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Gdx.app.error("SaveManager", "Failed to delete save", e)
            false
        }
    }

    /**
     * Gets all available save slots
     */
    fun getAvailableSaves(): List<SaveSlotInfo> {
        val saves = mutableListOf<SaveSlotInfo>()

        for (slot in 0..MAX_SAVE_SLOTS) {
            val fileName = if (slot == 0) AUTO_SAVE_FILE else "save_$slot.json"
            val file = Gdx.files.local(SAVE_DIRECTORY + fileName)

            if (file.exists()) {
                try {
                    val jsonData = file.readString()
                    val saveData = json.fromJson(SaveData::class.java, jsonData)

                    saves.add(SaveSlotInfo(
                        slot = slot,
                        playerName = saveData.playerName,
                        level = saveData.level,
                        playTime = saveData.playTime,
                        lastSaved = saveData.lastSaved,
                        exists = true
                    ))
                } catch (e: Exception) {
                    Gdx.app.error("SaveManager", "Failed to read save slot $slot", e)
                }
            } else {
                saves.add(SaveSlotInfo(slot = slot, exists = false))
            }
        }

        return saves
    }

    /**
     * Updates auto-save timer
     */
    fun update(deltaTime: Float) {
        timeSinceLastSave += deltaTime

        if (timeSinceLastSave >= autoSaveInterval) {
            autoSave()
            timeSinceLastSave = 0f
        }
    }

    /**
     * Performs auto-save
     */
    fun autoSave() {
        if (currentSaveData != null) {
            saveGame(0)
            Gdx.app.log("SaveManager", "Auto-save completed")
        }
    }

    /**
     * Gets current save data
     */
    fun getCurrentSave(): SaveData? = currentSaveData

    /**
     * Sets current save data
     */
    fun setCurrentSave(saveData: SaveData) {
        currentSaveData = saveData
    }

    /**
     * Checks if a save exists
     */
    fun saveExists(slot: Int): Boolean {
        val fileName = if (slot == 0) AUTO_SAVE_FILE else "save_$slot.json"
        return Gdx.files.local(SAVE_DIRECTORY + fileName).exists()
    }

    override fun dispose() {
        autoSave()
        Gdx.app.log("SaveManager", "Save manager disposed")
    }

    data class SaveSlotInfo(
        val slot: Int,
        val playerName: String = "",
        val level: Int = 0,
        val playTime: Long = 0,
        val lastSaved: Long = 0,
        val exists: Boolean = false
    )
}
