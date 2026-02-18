package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.aetheria.mmo.components.*
import kotlin.math.sin

/**
 * Lighting System
 * Updates dynamic lights (flickering, pulsing, etc.)
 */
class LightingSystem : IteratingSystem(
    Family.all(LightComponent::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val light = entity.getComponent(LightComponent::class.java)

        // Handle flickering
        if (light.isFlickering) {
            light.flickerTime += deltaTime * light.flickerSpeed

            // Use sine wave for smooth flickering
            val flicker = sin(light.flickerTime) * 0.2f + 0.8f
            light.intensity = light.baseIntensity * flicker
        }

        // TODO: Add more dynamic lighting effects
        // - Pulsing
        // - Color shifting
        // - Distance-based intensity
    }
}
