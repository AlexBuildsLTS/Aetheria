package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PlayerComponent : Component, Pool.Poolable {
    // Tag component to identify the local player
    override fun reset() {
        // No data to reset
    }
}