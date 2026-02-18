package com.aetheria.mmo.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.aetheria.mmo.components.*
import com.aetheria.mmo.utils.Logger

/**
 * State System
 * Manages entity state transitions (idle, walking, running, attacking, etc.)
 * Updates animation states based on entity behavior
 */
class StateSystem : IteratingSystem(
    Family.all(StateComponent::class.java).get()
) {

    private val stateMapper = ComponentMapper.getFor(StateComponent::class.java)
    private val velocityMapper = ComponentMapper.getFor(VelocityComponent::class.java)
    private val animationMapper = ComponentMapper.getFor(AnimationComponent::class.java)
    private val combatMapper = ComponentMapper.getFor(CombatComponent::class.java)
    private val healthMapper = ComponentMapper.getFor(HealthComponent::class.java)
    private val moveMapper = ComponentMapper.getFor(MoveEvtComponent::class.java)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val state = stateMapper.get(entity)
        val velocity = velocityMapper.get(entity)
        val animation = animationMapper.get(entity)
        val combat = combatMapper.get(entity)
        val health = healthMapper.get(entity)
        val move = moveMapper.get(entity)

        // Update state time
        state.stateTime += deltaTime

        // Determine new state based on entity conditions
        val newState = determineState(entity, state, velocity, combat, health, move)

        // Transition to new state if different
        if (newState != state.currentState) {
            transitionState(state, animation, newState)
        }
    }

    /**
     * Determine entity state based on components
     */
    private fun determineState(
        entity: Entity,
        state: StateComponent,
        velocity: VelocityComponent?,
        combat: CombatComponent?,
        health: HealthComponent?,
        move: MoveEvtComponent?
    ): EntityState {
        // Priority order: Dead > Attacking > Moving > Idle

        // Check if dead
        if (health != null && health.current <= 0f) {
            return EntityState.DEAD
        }

        // Check if attacking
        if (combat != null && combat.isAttacking) {
            return EntityState.ATTACKING
        }

        // Check if casting
        if (state.isCasting) {
            return EntityState.CASTING
        }

        // Check if dodging
        if (state.isDodging) {
            return EntityState.DODGING
        }

        // Check if jumping
        if (velocity != null && !velocity.isGrounded && velocity.linear.y > 0.1f) {
            return EntityState.JUMPING
        }

        // Check if falling
        if (velocity != null && !velocity.isGrounded && velocity.linear.y < -0.1f) {
            return EntityState.FALLING
        }

        // Check if moving
        if (velocity != null) {
            val horizontalSpeed = kotlin.math.sqrt(
                velocity.linear.x * velocity.linear.x + velocity.linear.z * velocity.linear.z
            )

            if (horizontalSpeed > 0.1f) {
                return if (move?.isSprinting == true) {
                    EntityState.RUNNING
                } else {
                    EntityState.WALKING
                }
            }
        }

        // Default to idle
        return EntityState.IDLE
    }

    /**
     * Transition to new state
     */
    private fun transitionState(state: StateComponent, animation: AnimationComponent?, newState: EntityState) {
        val previousState = state.currentState

        state.previousState = previousState
        state.currentState = newState
        state.stateTime = 0f

        // Update animation if available
        animation?.let {
            it.currentAnimation = getAnimationForState(newState)
            it.animationTime = 0f
        }

        Logger.debug("StateSystem", "State transition: $previousState -> $newState")
    }

    /**
     * Get animation name for state
     */
    private fun getAnimationForState(state: EntityState): String {
        return when (state) {
            EntityState.IDLE -> "idle"
            EntityState.WALKING -> "walk"
            EntityState.RUNNING -> "run"
            EntityState.JUMPING -> "jump"
            EntityState.FALLING -> "fall"
            EntityState.ATTACKING -> "attack"
            EntityState.CASTING -> "cast"
            EntityState.DODGING -> "dodge"
            EntityState.DEAD -> "death"
            EntityState.STUNNED -> "stunned"
        }
    }

    /**
     * Force state change
     */
    fun setState(entity: Entity, newState: EntityState) {
        val state = stateMapper.get(entity) ?: return
        val animation = animationMapper.get(entity)
        transitionState(state, animation, newState)
    }

    /**
     * Check if entity is in state
     */
    fun isInState(entity: Entity, state: EntityState): Boolean {
        val stateComp = stateMapper.get(entity) ?: return false
        return stateComp.currentState == state
    }
}

/**
 * Entity State Enum
 */
enum class EntityState {
    IDLE,
    WALKING,
    RUNNING,
    JUMPING,
    FALLING,
    ATTACKING,
    CASTING,
    DODGING,
    DEAD,
    STUNNED
}
