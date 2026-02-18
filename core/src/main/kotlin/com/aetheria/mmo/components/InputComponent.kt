package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

/**
 * Input Component
 * Stores player input state for processing by InputHandlerSystem
 */
class InputComponent : Component, Pool.Poolable {
    // Movement input
    val moveInput = Vector2()
    var isSprinting: Boolean = false
    var isJumping: Boolean = false
    var isCrouching: Boolean = false

    // Combat input
    var isPrimaryAttack: Boolean = false
    var isSecondaryAttack: Boolean = false
    var isBlocking: Boolean = false
    var isDodging: Boolean = false

    // Ability keys
    var abilityQ: Boolean = false
    var abilityE: Boolean = false
    var abilityR: Boolean = false
    var abilityF: Boolean = false

    // Interaction
    var isInteracting: Boolean = false
    var isReloading: Boolean = false

    // Mouse/Touch
    val lookDirection = Vector2()
    var aimX: Float = 0f
    var aimY: Float = 0f

    override fun reset() {
        moveInput.setZero()
        isSprinting = false
        isJumping = false
        isCrouching = false
        isPrimaryAttack = false
        isSecondaryAttack = false
        isBlocking = false
        isDodging = false
        abilityQ = false
        abilityE = false
        abilityR = false
        abilityF = false
        isInteracting = false
        isReloading = false
        lookDirection.setZero()
        aimX = 0f
        aimY = 0f
    }
}
