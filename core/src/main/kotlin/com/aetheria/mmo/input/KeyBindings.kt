package com.aetheria.mmo.input

import com.badlogic.gdx.Input

/**
 * Key Bindings
 * Centralized keyboard/gamepad input configuration
 * Supports rebinding and multiple control schemes
 */
object KeyBindings {

    // Movement
    var MOVE_FORWARD = Input.Keys.W
    var MOVE_BACKWARD = Input.Keys.S
    var MOVE_LEFT = Input.Keys.A
    var MOVE_RIGHT = Input.Keys.D
    var JUMP = Input.Keys.SPACE
    var SPRINT = Input.Keys.SHIFT_LEFT
    var CROUCH = Input.Keys.CONTROL_LEFT
    var DODGE = Input.Keys.ALT_LEFT

    // Combat
    var ABILITY_1 = Input.Keys.Q
    var ABILITY_2 = Input.Keys.E
    var ABILITY_3 = Input.Keys.R
    var ABILITY_4 = Input.Keys.F
    var ULTIMATE = Input.Keys.T
    var BASIC_ATTACK = Input.Keys.NUM_1
    var HEAVY_ATTACK = Input.Keys.NUM_2
    var BLOCK = Input.Keys.V

    // Interaction
    var INTERACT = Input.Keys.F
    var USE_ITEM = Input.Keys.G
    var RELOAD = Input.Keys.R
    var PICKUP = Input.Keys.E

    // UI
    var INVENTORY = Input.Keys.I
    var CHARACTER_SHEET = Input.Keys.C
    var MAP = Input.Keys.M
    var QUEST_LOG = Input.Keys.L
    var SOCIAL = Input.Keys.O
    var SETTINGS = Input.Keys.ESCAPE
    var CHAT = Input.Keys.ENTER
    var SCOREBOARD = Input.Keys.TAB

    // Camera
    var CAMERA_LOCK = Input.Keys.Z
    var CAMERA_RESET = Input.Keys.X
    var ZOOM_IN = Input.Keys.EQUALS
    var ZOOM_OUT = Input.Keys.MINUS

    // Quick slots
    var QUICK_SLOT_1 = Input.Keys.NUM_1
    var QUICK_SLOT_2 = Input.Keys.NUM_2
    var QUICK_SLOT_3 = Input.Keys.NUM_3
    var QUICK_SLOT_4 = Input.Keys.NUM_4
    var QUICK_SLOT_5 = Input.Keys.NUM_5
    var QUICK_SLOT_6 = Input.Keys.NUM_6

    // Emotes
    var EMOTE_1 = Input.Keys.Y
    var EMOTE_2 = Input.Keys.U
    var EMOTE_WHEEL = Input.Keys.B

    // Debug (remove in production)
    var DEBUG_TOGGLE = Input.Keys.F3
    var DEBUG_PHYSICS = Input.Keys.F4
    var DEBUG_NETWORK = Input.Keys.F5

    /**
     * Resets all bindings to default
     */
    fun resetToDefaults() {
        MOVE_FORWARD = Input.Keys.W
        MOVE_BACKWARD = Input.Keys.S
        MOVE_LEFT = Input.Keys.A
        MOVE_RIGHT = Input.Keys.D
        JUMP = Input.Keys.SPACE
        SPRINT = Input.Keys.SHIFT_LEFT
        CROUCH = Input.Keys.CONTROL_LEFT
        DODGE = Input.Keys.ALT_LEFT

        ABILITY_1 = Input.Keys.Q
        ABILITY_2 = Input.Keys.E
        ABILITY_3 = Input.Keys.R
        ABILITY_4 = Input.Keys.F
        ULTIMATE = Input.Keys.T

        INTERACT = Input.Keys.F
        INVENTORY = Input.Keys.I
        CHARACTER_SHEET = Input.Keys.C
        MAP = Input.Keys.M
        SETTINGS = Input.Keys.ESCAPE
    }

    /**
     * Loads bindings from preferences
     */
    fun loadBindings(prefs: Map<String, Int>) {
        prefs["MOVE_FORWARD"]?.let { MOVE_FORWARD = it }
        prefs["MOVE_BACKWARD"]?.let { MOVE_BACKWARD = it }
        prefs["MOVE_LEFT"]?.let { MOVE_LEFT = it }
        prefs["MOVE_RIGHT"]?.let { MOVE_RIGHT = it }
        prefs["JUMP"]?.let { JUMP = it }
        prefs["SPRINT"]?.let { SPRINT = it }
        // Add more as needed
    }

    /**
     * Saves bindings to preferences
     */
    fun saveBindings(): Map<String, Int> {
        return mapOf(
            "MOVE_FORWARD" to MOVE_FORWARD,
            "MOVE_BACKWARD" to MOVE_BACKWARD,
            "MOVE_LEFT" to MOVE_LEFT,
            "MOVE_RIGHT" to MOVE_RIGHT,
            "JUMP" to JUMP,
            "SPRINT" to SPRINT,
            "ABILITY_1" to ABILITY_1,
            "ABILITY_2" to ABILITY_2,
            "ABILITY_3" to ABILITY_3,
            "ABILITY_4" to ABILITY_4,
            "INTERACT" to INTERACT,
            "INVENTORY" to INVENTORY
        )
    }

    /**
     * Checks if a key is bound to any action
     */
    fun isKeyBound(keycode: Int): Boolean {
        return keycode in listOf(
            MOVE_FORWARD, MOVE_BACKWARD, MOVE_LEFT, MOVE_RIGHT,
            JUMP, SPRINT, CROUCH, DODGE,
            ABILITY_1, ABILITY_2, ABILITY_3, ABILITY_4,
            INTERACT, INVENTORY, CHARACTER_SHEET, MAP
        )
    }

    /**
     * Gets the action name for a keycode
     */
    fun getActionName(keycode: Int): String? {
        return when (keycode) {
            MOVE_FORWARD -> "Move Forward"
            MOVE_BACKWARD -> "Move Backward"
            MOVE_LEFT -> "Move Left"
            MOVE_RIGHT -> "Move Right"
            JUMP -> "Jump"
            SPRINT -> "Sprint"
            ABILITY_1 -> "Ability 1"
            ABILITY_2 -> "Ability 2"
            ABILITY_3 -> "Ability 3"
            ABILITY_4 -> "Ability 4"
            INTERACT -> "Interact"
            INVENTORY -> "Inventory"
            else -> null
        }
    }
}
