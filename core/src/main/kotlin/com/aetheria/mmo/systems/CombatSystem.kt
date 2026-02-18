package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.aetheria.mmo.components.CombatComponent
import com.aetheria.mmo.components.PlayerComponent
import com.aetheria.mmo.components.StaminaComponent
import com.aetheria.mmo.components.StateComponent

/**
 * Combat System
 * Handles ability inputs and cooldowns
 */
class CombatSystem : IteratingSystem(
    Family.all(CombatComponent::class.java, PlayerComponent::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val combat = entity.getComponent(CombatComponent::class.java)
        val stamina = entity.getComponent(StaminaComponent::class.java)
        val state = entity.getComponent(StateComponent::class.java)

        // Update cooldowns
        combat.updateCooldowns(deltaTime)

        // Check ability inputs
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) && combat.canUseAbility("Q")) {
            useAbility(entity, "Q", 20f, "Basic Attack")
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && combat.canUseAbility("E")) {
            useAbility(entity, "E", 30f, "Special Ability")
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && combat.canUseAbility("R")) {
            useAbility(entity, "R", 50f, "Ultimate")
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F) && combat.canUseAbility("F")) {
            useAbility(entity, "F", 25f, "Utility")
        }
    }

    private fun useAbility(entity: Entity, key: String, staminaCost: Float, abilityName: String) {
        val combat = entity.getComponent(CombatComponent::class.java)
        val stamina = entity.getComponent(StaminaComponent::class.java)
        val state = entity.getComponent(StateComponent::class.java)

        // Check stamina
        if (stamina != null && !stamina.consume(staminaCost)) {
            Gdx.app.log("Combat", "Not enough stamina for $abilityName!")
            return
        }

        // Use ability
        combat.useAbility(key)

        // Set attacking state for animation
        if (state != null) {
            state.current = StateComponent.ATTACKING
        }

        Gdx.app.log("Combat", "Used $abilityName! Cooldown: ${combat.maxCooldowns[key]}s")
    }
}
