package com.aetheria.mmo.systems

import com.aetheria.mmo.components.AnimationComponent
import com.aetheria.mmo.components.StateComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem

class AnimationSystem : IteratingSystem(
    Family.all(AnimationComponent::class.java, StateComponent::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val anim = entity.getComponent(AnimationComponent::class.java)
        val state = entity.getComponent(StateComponent::class.java)

        // 1. Map Game State to Meshy Animation Names
        // NOTE: These strings MUST match what you saw in Meshy (Case Sensitive!)
        val targetAnim = when (state.current) {
            StateComponent.IDLE -> "Idle"
            StateComponent.WALKING -> "Walk"
            StateComponent.RUNNING -> "Run"
            StateComponent.JUMPING -> "Jump_Loop"
            StateComponent.ATTACKING -> "Attack_Melee"
            else -> "Idle"
        }

        // 2. Switch Animation if Changed
        if (anim.currentAnimation != targetAnim) {
            // "0.2f" is the blend time. It blends the old animation into the new one
            // over 0.2 seconds. This creates the "AAA Smoothness" you want.
            anim.controller.animate(targetAnim, -1, 0.2f, null, 0f)
            anim.currentAnimation = targetAnim
        }

        // 3. Update the Controller
        anim.controller.update(deltaTime)
    }
}