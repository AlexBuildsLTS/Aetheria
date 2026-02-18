package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.badlogic.gdx.utils.Pool

class AnimationComponent : Component, Pool.Poolable {
    lateinit var controller: AnimationController
    var currentAnimation: String = ""
    var loopCount: Int = -1 // -1 = Loop forever, 1 = Play once
    var animationTime: Float = 0f

    override fun reset() {
        currentAnimation = ""
        loopCount = -1
        animationTime = 0f
        // Controller is reset when model is re-created
    }
}