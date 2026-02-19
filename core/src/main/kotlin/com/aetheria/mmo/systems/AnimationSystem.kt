package com.aetheria.mmo.systems

import com.aetheria.mmo.components.AnimationComponent
import com.aetheria.mmo.components.StateComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx

class AnimationSystem : IteratingSystem(
    Family.all(AnimationComponent::class.java, StateComponent::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val anim = entity.getComponent(AnimationComponent::class.java)
        val state = entity.getComponent(StateComponent::class.java)
        val controller = anim.controller ?: return

        val targetAnim = when (state.current) {
            StateComponent.IDLE -> findAnimation(anim, listOf("Idle", "Standing", "Armature|Idle"))
            StateComponent.WALKING -> findAnimation(anim, listOf("Walk", "Walking", "Run", "Armature|Walk"))
            StateComponent.ATTACKING -> findAnimation(anim, listOf("Attack", "Punch", "Armature|Attack"))
            else -> findAnimation(anim, listOf("Idle", "Armature|Idle"))
        }

        if (anim.currentAnimation != targetAnim && targetAnim.isNotEmpty()) {
            try {
                controller.animate(targetAnim, -1, 1f, null, 0.2f)
                anim.currentAnimation = targetAnim
            } catch (e: Exception) {
                // Gdx.app.error("AnimationSystem", "Failed to set animation: $targetAnim")
            }
        }
    }

    private fun findAnimation(anim: AnimationComponent, candidates: List<String>): String {
        val controller = anim.controller ?: return ""
        val available = controller.target.animations
        for (candidate in candidates) {
            for (a in available) {
                if (a.id.equals(candidate, true) || a.id.contains(candidate, true)) return a.id
            }
        }
        return if (available.size > 0) available[0].id else ""
    }
}
