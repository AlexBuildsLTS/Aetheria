package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.aetheria.mmo.components.*

/**
 * Input Handler System
 * Processes InputComponent and translates to game actions
 */
class InputHandlerSystem : IteratingSystem(
    Family.all(InputComponent::class.java, PlayerComponent::class.java).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val input = entity.getComponent(InputComponent::class.java)
        val moveEvt = entity.getComponent(MoveEvtComponent::class.java)
        val combat = entity.getComponent(CombatComponent::class.java)

        // Update input state from keyboard/gamepad
        updateMovementInput(input)
        updateCombatInput(input)
        updateAbilityInput(input)

        // Apply input to movement component
        moveEvt?.let {
            // Map 2D input to 3D world direction
            // moveInput.x -> X axis (Left/Right)
            // moveInput.y -> Z axis (Forward/Backward)
            // In LibGDX, -Z is forward, so we negate moveInput.y
            it.setMoveDirection(input.moveInput.x, 0f, -input.moveInput.y)
            it.isSprinting = input.isSprinting
        }

        // Handle combat actions
        if (combat != null) {
            if (input.abilityQ && combat.canUseAbility("Q")) combat.useAbility("Q")
            if (input.abilityE && combat.canUseAbility("E")) combat.useAbility("E")
            if (input.abilityR && combat.canUseAbility("R")) combat.useAbility("R")
            if (input.abilityF && combat.canUseAbility("F")) combat.useAbility("F")
        }
    }

    private fun updateMovementInput(input: InputComponent) {
        input.moveInput.setZero()

        // Keyboard WASD
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) input.moveInput.y = 1f
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) input.moveInput.y = -1f
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) input.moveInput.x = -1f
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) input.moveInput.x = 1f

        input.isSprinting = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)
        input.isJumping = Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
        input.isCrouching = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)
    }

    private fun updateCombatInput(input: InputComponent) {
        input.isPrimaryAttack = Gdx.input.isButtonPressed(Input.Buttons.LEFT)
        input.isSecondaryAttack = Gdx.input.isButtonPressed(Input.Buttons.RIGHT)
        input.isBlocking = Gdx.input.isKeyPressed(Input.Keys.F)
        input.isDodging = Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT)
    }

    private fun updateAbilityInput(input: InputComponent) {
        input.abilityQ = Gdx.input.isKeyJustPressed(Input.Keys.Q)
        input.abilityE = Gdx.input.isKeyJustPressed(Input.Keys.E)
        input.abilityR = Gdx.input.isKeyJustPressed(Input.Keys.R)
        input.abilityF = Gdx.input.isKeyJustPressed(Input.Keys.F)
        input.isInteracting = Gdx.input.isKeyJustPressed(Input.Keys.E)
        input.isReloading = Gdx.input.isKeyJustPressed(Input.Keys.R)
    }
}
