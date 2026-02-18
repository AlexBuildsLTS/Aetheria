package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

/**
 * Action Event Component
 * Stores queued actions/events for an entity to process
 * Used for ability triggers, damage events, status effects, etc.
 */
class ActionEvtComponent : Component, Pool.Poolable {

    enum class ActionType {
        ABILITY_CAST,
        TAKE_DAMAGE,
        HEAL,
        APPLY_BUFF,
        APPLY_DEBUFF,
        TELEPORT,
        KNOCKBACK,
        STUN,
        ROOT,
        SILENCE,
        DEATH,
        RESPAWN,
        INTERACT,
        PICKUP_ITEM,
        DROP_ITEM,
        EQUIP_ITEM,
        UNEQUIP_ITEM
    }

    data class Action(
        val type: ActionType,
        val data: Map<String, Any> = emptyMap(),
        val timestamp: Long = System.currentTimeMillis()
    )

    private val actionQueue = mutableListOf<Action>()

    /**
     * Adds an action to the queue
     */
    fun queueAction(type: ActionType, data: Map<String, Any> = emptyMap()) {
        actionQueue.add(Action(type, data))
    }

    /**
     * Gets all queued actions and clears the queue
     */
    fun consumeActions(): List<Action> {
        val actions = actionQueue.toList()
        actionQueue.clear()
        return actions
    }

    /**
     * Peeks at queued actions without consuming them
     */
    fun peekActions(): List<Action> = actionQueue.toList()

    /**
     * Checks if there are any pending actions
     */
    fun hasActions(): Boolean = actionQueue.isNotEmpty()

    /**
     * Gets the number of queued actions
     */
    fun getActionCount(): Int = actionQueue.size

    /**
     * Clears all queued actions
     */
    fun clearActions() {
        actionQueue.clear()
    }

    override fun reset() {
        actionQueue.clear()
    }
}

