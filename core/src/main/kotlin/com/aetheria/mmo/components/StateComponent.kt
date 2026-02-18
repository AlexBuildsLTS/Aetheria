package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class StateComponent : Component, Pool.Poolable {
    var current = IDLE
    var time = 0f

    // Additional state properties for systems
    var currentState = com.aetheria.mmo.systems.EntityState.IDLE
    var previousState = com.aetheria.mmo.systems.EntityState.IDLE
    var stateTime = 0f
    var isCasting = false
    var isDodging = false

    fun set(newState: Int) {
        if (current != newState) {
            current = newState
            time = 0f
        }
    }

    companion object {
        const val IDLE = 0
        const val WALKING = 1
        const val RUNNING = 2
        const val JUMPING = 3
        const val ATTACKING = 4
    }

    override fun reset() {
        current = IDLE
        time = 0f
        currentState = com.aetheria.mmo.systems.EntityState.IDLE
        previousState = com.aetheria.mmo.systems.EntityState.IDLE
        stateTime = 0f
        isCasting = false
        isDodging = false
    }
}