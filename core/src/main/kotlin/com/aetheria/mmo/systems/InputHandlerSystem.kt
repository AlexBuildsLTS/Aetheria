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
    var joystickProvider: com.aetheria.mmo.screens.GameHUD? = null

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
        val keyboardX = getKeyboardX()
        val keyboardY = getKeyboardY()
        
        var joystickX = 0f
        var joystickY = 0f
        
        joystickProvider?.let { hud ->
            val stick = hud.getMovementInput()
            joystickX = stick.x
            joystickY = stick.y
        }

        // Blend: Take the one with higher magnitude or just add and clamp
        // Adding and clamping to 1.0 allows both to work
        input.moveInput.set(keyboardX + joystickX, keyboardY + joystickY)
        
        // Normalize so diagonal movement isn't faster
        if (input.moveInput.len() > 1f) {
            input.moveInput.nor()
        }

        input.isSprinting = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)
        input.isJumping = Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
        input.isCrouching = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)
    }

    private fun getKeyboardX(): Float {
        var x = 0f
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) x -= 1f
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) x += 1f
        return x
    }

    private fun getKeyboardY(): Float {
        var y = 0f
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) y += 1f
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) y -= 1f
        return y
    }

    private fun updateCombatInput(input: InputComponent) {
        input.isPrimaryAttack = Gdx.input.isButtonPressed(Input.Buttons.LEFT)
        input.isSecondaryAttack = Gdx.input.isButtonPressed(Input.Buttons.RIGHT)
        
        // Joystick Aiming
        joystickProvider?.let { hud ->
            val aimInput = hud.getAimInput()
            if (aimInput.len() > 0.1f) {
                input.aimX = aimInput.x
                input.aimY = aimInput.y
            } else {
                input.aimX = 0f
                input.aimY = 0f
            }
        }

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
