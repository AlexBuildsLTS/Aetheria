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
            it.setMoveDirection(input.moveInput.x, 0f, input.moveInput.y)
            it.isSprinting = input.isSprinting
        }

        // Handle combat actions
        if (input.isPrimaryAttack && combat != null) {
            if (combat.canUseAbility("Q")) {
                combat.useAbility("Q")
                // Fire event or trigger attack
            }
        }
    }

    private fun updateMovementInput(input: InputComponent) {
        input.moveInput.setZero()

        if (Gdx.input.isKeyPressed(Input.Keys.W)) input.moveInput.y = 1f
        if (Gdx.input.isKeyPressed(Input.Keys.S)) input.moveInput.y = -1f
        if (Gdx.input.isKeyPressed(Input.Keys.A)) input.moveInput.x = -1f
        if (Gdx.input.isKeyPressed(Input.Keys.D)) input.moveInput.x = 1f

        input.isSprinting = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
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
