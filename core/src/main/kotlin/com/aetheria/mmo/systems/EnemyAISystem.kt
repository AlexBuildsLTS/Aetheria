package com.aetheria.mmo.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*
import com.aetheria.mmo.events.DamageEvent
import com.aetheria.mmo.events.DamageType
import com.aetheria.mmo.events.EventQueue

/**
 * Enemy AI System
 * Handles enemy behavior, targeting, and combat AI
 *
 * AI States:
 * - IDLE: Standing still, scanning for targets
 * - PATROL: Wandering around
 * - CHASE: Moving towards player
 * - ATTACK: In range, attacking player
 * - FLEE: Low health, running away
 */
class EnemyAISystem : IteratingSystem(
    Family.all(
        SteeringComponent::class.java,
        TransformComponent::class.java,
        CombatComponent::class.java,
        HealthComponent::class.java
    ).exclude(PlayerComponent::class.java).get()
) {

    private val steeringMapper = ComponentMapper.getFor(SteeringComponent::class.java)
    private val transformMapper = ComponentMapper.getFor(TransformComponent::class.java)
    private val combatMapper = ComponentMapper.getFor(CombatComponent::class.java)
    private val healthMapper = ComponentMapper.getFor(HealthComponent::class.java)
    private val stateMapper = ComponentMapper.getFor(StateComponent::class.java)
    private val velocityMapper = ComponentMapper.getFor(VelocityComponent::class.java)

    // AI Configuration
    private val detectionRange = 15f
    private val attackRange = 2.5f
    private val fleeHealthPercent = 0.25f
    private val attackCooldown = 1.5f

    private var playerEntity: Entity? = null
    private val tempVec = Vector3()

    override fun update(deltaTime: Float) {
        // Find player entity (cache it)
        if (playerEntity == null) {
            val playerFamily = Family.all(PlayerComponent::class.java).get()
            val players = engine.getEntitiesFor(playerFamily)
            if (players.size() > 0) {
                playerEntity = players.first()
            }
        }

        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val steering = steeringMapper.get(entity)
        val transform = transformMapper.get(entity)
        val combat = combatMapper.get(entity)
        val health = healthMapper.get(entity)
        val state = stateMapper.get(entity)

        // Update combat cooldowns
        combat.updateCooldowns(deltaTime)

        // Get player position
        val player = playerEntity ?: return
        val playerTransform = transformMapper.get(player) ?: return
        val playerPos = playerTransform.position

        // Calculate distance to player
        val distanceToPlayer = transform.position.dst(playerPos)

        // Determine AI state based on health and distance
        val healthPercent = health.current / health.max

        when {
            // FLEE: Low health, run away
            healthPercent < fleeHealthPercent -> {
                handleFlee(entity, steering, transform, playerPos, state)
            }

            // ATTACK: In range and can attack
            distanceToPlayer <= attackRange -> {
                handleAttack(entity, steering, combat, transform, playerPos, player, state)
            }

            // CHASE: Player detected, move towards them
            distanceToPlayer <= detectionRange -> {
                handleChase(entity, steering, transform, playerPos, state)
            }

            // IDLE/PATROL: No player nearby, wander
            else -> {
                handleIdle(entity, steering, state)
            }
        }
    }

    /**
     * FLEE State - Run away from player
     */
    private fun handleFlee(
        entity: Entity,
        steering: SteeringComponent,
        transform: TransformComponent,
        playerPos: Vector3,
        state: StateComponent?
    ) {
        // Set flee target
        steering.fleeTarget = playerPos.cpy()
        steering.seekTarget = null
        steering.arriveTarget = null
        steering.isWandering = false

        state?.currentState = EntityState.RUNNING

        Gdx.app.log("EnemyAI", "Enemy fleeing!")
    }

    /**
     * CHASE State - Move towards player
     */
    private fun handleChase(
        entity: Entity,
        steering: SteeringComponent,
        transform: TransformComponent,
        playerPos: Vector3,
        state: StateComponent?
    ) {
        // Set seek target
        steering.seekTarget = playerPos.cpy()
        steering.fleeTarget = null
        steering.arriveTarget = null
        steering.isWandering = false

        state?.currentState = EntityState.RUNNING
    }

    /**
     * ATTACK State - Attack the player
     */
    private fun handleAttack(
        entity: Entity,
        steering: SteeringComponent,
        combat: CombatComponent,
        transform: TransformComponent,
        playerPos: Vector3,
        player: Entity,
        state: StateComponent?
    ) {
        // Stop moving
        steering.seekTarget = null
        steering.fleeTarget = null
        steering.arriveTarget = playerPos.cpy()
        steering.arriveRadius = attackRange * 0.8f
        steering.isWandering = false

        // Face the player
        val direction = tempVec.set(playerPos).sub(transform.position).nor()
        val angle = kotlin.math.atan2(direction.x, direction.z) * (180f / Math.PI.toFloat())
        transform.rotation.setFromAxis(Vector3.Y, angle)

        // Attack if cooldown is ready
        if (combat.canUseAbility("basic_attack")) {
            performAttack(entity, player, combat, transform)
            combat.useAbility("basic_attack")
            state?.currentState = EntityState.ATTACKING

            Gdx.app.log("EnemyAI", "Enemy attacking player!")
        } else {
            state?.currentState = EntityState.IDLE
        }
    }

    /**
     * IDLE State - Wander around
     */
    private fun handleIdle(
        entity: Entity,
        steering: SteeringComponent,
        state: StateComponent?
    ) {
        steering.seekTarget = null
        steering.fleeTarget = null
        steering.arriveTarget = null
        steering.isWandering = true

        state?.currentState = EntityState.WALKING
    }

    /**
     * Perform melee attack on target
     */
    private fun performAttack(attacker: Entity, target: Entity, combat: CombatComponent, transform: TransformComponent) {
        // Calculate damage
        var damage = combat.attackPower

        // Critical hit chance
        val isCritical = Math.random() < combat.critChance
        if (isCritical) {
            damage *= combat.critDamage
        }

        // Post damage event
        EventQueue.post(
            DamageEvent(
                target = target,
                source = attacker,
                amount = damage,
                damageType = DamageType.PHYSICAL,
                position = transform.position.cpy(),
                isCritical = isCritical
            )
        )
    }
}
