package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.badlogic.gdx.utils.Pool

class AnimationComponent : Component, Pool.Poolable {
    var animationTime: Float = 0f
    var controller: AnimationController? = null
    var currentAnimation: String = ""

    override fun reset() {
        currentAnimation = ""
        controller = null
        animationTime = 0f
    }
}
