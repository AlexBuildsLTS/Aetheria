package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.utils.Pool

class ModelComponent : Component, Pool.Poolable {
    lateinit var modelInstance: ModelInstance // This matches your old code
    var isVisible = true

    override fun reset() {
        isVisible = true
        // modelInstance is usually not nulled out, but re-assigned
    }
}