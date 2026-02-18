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

        // Map Game State to actual available animations
        // Using fallback system to prevent crashes
        val targetAnim = when (state.current) {
            StateComponent.IDLE -> findAnimation(anim, listOf("Idle", "Climb_Stairs", "Standing_Idle"))
            StateComponent.WALKING -> findAnimation(anim, listOf("Walk", "Walking", "Run"))
            StateComponent.RUNNING -> findAnimation(anim, listOf("Run", "Running", "Walk"))
            StateComponent.JUMPING -> findAnimation(anim, listOf("Jump_Loop", "Jump", "Climb_Stairs"))
            StateComponent.ATTACKING -> findAnimation(anim, listOf("Attack_Melee", "Attack", "Punch"))
            else -> findAnimation(anim, listOf("Idle", "Climb_Stairs", "Standing_Idle"))
        }

        // Switch Animation if Changed
        if (anim.currentAnimation != targetAnim) {
            try {
                anim.controller.animate(targetAnim, -1, 0.2f, null, 0f)
                anim.currentAnimation = targetAnim
            } catch (e: Exception) {
                // Animation not found, keep current
            }
        }

        // Update the Controller
        anim.controller.update(deltaTime)
    }

    /**
     * Find first available animation from a list of candidates
     */
    private fun findAnimation(anim: AnimationComponent, candidates: List<String>): String {
        val modelInstance = anim.controller.target
        val availableAnims = modelInstance.animations.map { it.id }

        // Try each candidate
        for (candidate in candidates) {
            val found = availableAnims.find { it.equals(candidate, ignoreCase = true) }
            if (found != null) return found
        }

        // Fallback to first available animation
        return if (availableAnims.isNotEmpty()) availableAnims[0] else ""
    }
}