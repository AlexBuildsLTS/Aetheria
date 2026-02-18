package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

class VelocityComponent : Component, Pool.Poolable {
    val linear = Vector3()
    val angular = Vector3()
    var speed = 5f
    var friction = 0.8f
    var isGrounded = false

    override fun reset() {
        linear.setZero()
        angular.setZero()
        speed = 5f
        friction = 0.8f
        isGrounded = false
    }
}