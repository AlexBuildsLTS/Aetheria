package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

class TransformComponent : Component, Pool.Poolable {
    val position = Vector3()
    val scale = Vector3(1f, 1f, 1f)
    val rotation = Quaternion()
    var isHidden = false

    override fun reset() {
        position.setZero()
        scale.set(1f, 1f, 1f)
        rotation.idt()
        isHidden = false
    }
}