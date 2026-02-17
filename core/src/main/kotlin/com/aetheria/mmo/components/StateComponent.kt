package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class StateComponent : Component, Pool.Poolable {
    var current = IDLE
    var time = 0f

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
    }
}