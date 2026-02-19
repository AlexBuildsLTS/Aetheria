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
import com.aetheria.mmo.components.InputComponent

/**
 * Combat System
 * Handles ability inputs and cooldowns
 */
class CombatSystem : IteratingSystem(
    Family.all(CombatComponent::class.java, PlayerComponent::class.java).get()
) {
    // Simple timer to reset attack state
    private var attackTimer = 0f
    private val attackDuration = 0.8f // Approximate animation length

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val combat = entity.getComponent(CombatComponent::class.java)
        val stamina = entity.getComponent(StaminaComponent::class.java)
        val state = entity.getComponent(StateComponent::class.java)
        val input = entity.getComponent(InputComponent::class.java)

        // Update cooldowns
        combat.updateCooldowns(deltaTime)

        // Handle attack state reset
        if (state != null && state.current == StateComponent.ATTACKING) {
            attackTimer += deltaTime
            if (attackTimer >= attackDuration) {
                state.current = StateComponent.IDLE
                attackTimer = 0f
            }
        }

        // Check ability inputs from InputComponent (preferred) or direct polling
        // Using InputComponent allows InputHandlerSystem to manage input mapping
        
        // Use safe call for input component as it might be null if not added yet
        val useQ = input?.abilityQ ?: Gdx.input.isKeyJustPressed(Input.Keys.Q)
        val useE = input?.abilityE ?: Gdx.input.isKeyJustPressed(Input.Keys.E)
        val useR = input?.abilityR ?: Gdx.input.isKeyJustPressed(Input.Keys.R)
        val useF = input?.abilityF ?: Gdx.input.isKeyJustPressed(Input.Keys.F)
        
        // Map abilities to keys
        if (useQ && combat.canUseAbility("Q")) {
            useAbility(entity, "Q", 20f, "Strike")
        }

        if (useE && combat.canUseAbility("E")) {
            useAbility(entity, "E", 30f, "Dash")
        }

        if (useR && combat.canUseAbility("R")) {
            useAbility(entity, "R", 50f, "Ultimate")
        }

        if (useF && combat.canUseAbility("F")) {
            useAbility(entity, "F", 25f, "Heal")
        }
    }

    private fun useAbility(entity: Entity, key: String, staminaCost: Float, abilityName: String) {
        val combat = entity.getComponent(CombatComponent::class.java)
        val stamina = entity.getComponent(StaminaComponent::class.java)
        val state = entity.getComponent(StateComponent::class.java)
        val transform = entity.getComponent(com.aetheria.mmo.components.TransformComponent::class.java)
        val player = entity.getComponent(PlayerComponent::class.java)

        // Check stamina
        if (stamina != null && !stamina.consume(staminaCost)) {
            return
        }

        // Use ability
        combat.useAbility(key)

        // Set attacking state for animation
        if (state != null) {
            state.current = StateComponent.ATTACKING
            attackTimer = 0f // Reset timer
        }

        // Spawn Projectile / Spell Effect
        val engine = this.engine as? com.badlogic.ashley.core.PooledEngine
        if (engine != null && transform != null) {
            val spawnPos = transform.position.cpy().add(0f, 1.5f, 0f)
            val direction = transform.rotation.transform(com.badlogic.gdx.math.Vector3(0f, 0f, -1f))

            when (key) {
                "Q" -> com.aetheria.mmo.entities.ProjectileFactory.createPlasmaBolt(engine, spawnPos, direction, entity)
                "E" -> com.aetheria.mmo.entities.ProjectileFactory.createFireball(engine, spawnPos, direction, entity)
                "R" -> com.aetheria.mmo.entities.ProjectileFactory.createLightningBolt(engine, spawnPos, direction, entity)
                "F" -> com.aetheria.mmo.entities.ProjectileFactory.createVoidMissile(engine, spawnPos, direction, entity)
            }
        }

        Gdx.app.log("Combat", "Used $abilityName! Cooldown: ${combat.maxCooldowns[key]}s")
    }
}
