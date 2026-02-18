package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pool

/**
 * Light Component
 * Adds dynamic lighting to entities
 */
class LightComponent : Component, Pool.Poolable {
    var color: Color = Color.WHITE.cpy()
    var intensity: Float = 1f
    var radius: Float = 10f
    var isFlickering: Boolean = false
    var flickerSpeed: Float = 5f
    var castsShadows: Boolean = true
    var lightType: LightType = LightType.POINT

    // Internal state for flickering
    var flickerTime: Float = 0f
    var baseIntensity: Float = 1f

    override fun reset() {
        color.set(Color.WHITE)
        intensity = 1f
        radius = 10f
        isFlickering = false
        flickerSpeed = 5f
        castsShadows = true
        lightType = LightType.POINT
        flickerTime = 0f
        baseIntensity = 1f
    }
}

enum class LightType {
    POINT,
    SPOT,
    DIRECTIONAL,
    AMBIENT
}
