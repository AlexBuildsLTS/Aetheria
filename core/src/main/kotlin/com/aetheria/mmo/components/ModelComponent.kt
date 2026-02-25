package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.utils.Pool

class ModelComponent : Component, Pool.Poolable {
    var modelInstance: ModelInstance? = null
    var isVisible = true

    override fun reset() {
        modelInstance = null
        isVisible = true
    }
}
