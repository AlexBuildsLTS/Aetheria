package com.aetheria.mmo.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray
import com.aetheria.mmo.components.*
import com.aetheria.mmo.events.DamageEvent
import com.aetheria.mmo.events.DamageType
import com.aetheria.mmo.events.EventQueue

/**
 * AAA Combat System
 * Handles Action Combat (Hitscan & Projectiles)
 * CoD/Fortnite style aiming and shooting.
 */
class CombatSystem(private val camera: Camera) : IteratingSystem(
    Family.all(CombatComponent::class.java, PlayerComponent::class.java).get()
) {
    private val combatMapper = ComponentMapper.getFor(CombatComponent::class.java)
    private val transformMapper = ComponentMapper.getFor(TransformComponent::class.java)
    private val staminaMapper = ComponentMapper.getFor(StaminaComponent::class.java)
    private val stateMapper = ComponentMapper.getFor(StateComponent::class.java)
    private val inputMapper = ComponentMapper.getFor(InputComponent::class.java)
    private val collisionMapper = ComponentMapper.getFor(CollisionComponent::class.java)
    private val healthMapper = ComponentMapper.getFor(HealthComponent::class.java)

    // Raycast helpers
    private val ray = Ray(Vector3(), Vector3())
    private val intersection = Vector3()
    private val enemyPos = Vector3()
    
    // Weapon Stats (Should be in WeaponComponent, but hardcoded for now)
    private val fireRate = 0.1f // 10 shots/sec
    private var fireTimer = 0f
    private val maxRange = 100f

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val combat = combatMapper.get(entity)
        val stamina = staminaMapper.get(entity)
        val state = stateMapper.get(entity)
        val input = inputMapper.get(entity)

        // Update cooldowns
        combat.updateCooldowns(deltaTime)
        
        // Weapon Cycling
        if (fireTimer > 0) fireTimer -= deltaTime

        // 1. Primary Fire (Hitscan / Shooting)
        val isFiring = (input != null && input.isPrimaryAttack) || Gdx.input.isButtonPressed(Input.Buttons.LEFT)
        // Auto-fire if holding button (CoD style)
        if (isFiring && fireTimer <= 0) {
            fireHitscan(entity, combat)
            fireTimer = fireRate
        }

        // 2. Secondary Fire / Aiming (Handled by CameraSystem for zoom, here for logic)
        // if (input.isSecondaryAttack) { ... reduce spread, zoom fov ... }

        // 3. Abilities
        val useQ = input?.abilityQ ?: Gdx.input.isKeyJustPressed(Input.Keys.Q)
        val useE = input?.abilityE ?: Gdx.input.isKeyJustPressed(Input.Keys.E)
        val useR = input?.abilityR ?: Gdx.input.isKeyJustPressed(Input.Keys.R)
        val useF = input?.abilityF ?: Gdx.input.isKeyJustPressed(Input.Keys.F)

        if (useQ && combat.canUseAbility("Q")) useAbility(entity, "Q", 20f, "Strike")
        if (useE && combat.canUseAbility("E")) useAbility(entity, "E", 30f, "Dash")
        if (useR && combat.canUseAbility("R")) useAbility(entity, "R", 50f, "Ultimate")
        if (useF && combat.canUseAbility("F")) useAbility(entity, "F", 25f, "Heal")
    }

    /**
     * Hitscan Shooting Logic (CoD Style)
     * Raycasts from camera center.
     */
    private fun fireHitscan(entity: Entity, combat: CombatComponent) {
        // 1. Setup Ray from Camera
        ray.origin.set(camera.position)
        ray.direction.set(camera.direction).nor()

        // 2. Check collisions with enemies
        val enemies = engine.getEntitiesFor(Family.all(HealthComponent::class.java, CollisionComponent::class.java).exclude(PlayerComponent::class.java).get())
        
        var closestDist = Float.MAX_VALUE
        var closestEnemy: Entity? = null
        var hitPoint = Vector3()

        for (enemy in enemies) {
            val transform = transformMapper.get(enemy)
            val collision = collisionMapper.get(enemy)
            
            enemyPos.set(transform.position)
            // Adjust sphere center for height (collision component usually centers at bottom or mid?)
            // Let's assume position is bottom, center is +radius up? 
            // For now, assume position is center.
            
            val radius = collision.radius * transform.scale.x // Scale radius
            
            if (Intersector.intersectRaySphere(ray, enemyPos, radius, intersection)) {
                val dist = camera.position.dst2(intersection)
                if (dist < closestDist && dist < maxRange * maxRange) {
                    closestDist = dist
                    closestEnemy = enemy
                    hitPoint.set(intersection)
                }
            }
        }

        // 3. Apply Damage
        if (closestEnemy != null) {
            val isCritical = Math.random() < combat.critChance
            var damage = combat.attackPower
            if (isCritical) damage *= combat.critDamage

            EventQueue.post(
                DamageEvent(
                    target = closestEnemy,
                    source = entity,
                    amount = damage,
                    damageType = DamageType.PHYSICAL,
                    position = hitPoint.cpy(),
                    isCritical = isCritical
                )
            )
            
            // Spawn Hit Effect (Blood/Sparks)
             // com.aetheria.mmo.managers.VFXManager.playHitEffect(hitPoint)
        } else {
            // Hit nothing or environment
            // Logic for environment raycast would go here (using PhysicsSystem or Bullet)
        }

        // 4. Visuals (Muzzle Flash / Tracer)
        // Spawn a tracer from weapon position (approximate) to hit point or max range
        // For now, let's just log
        // Gdx.app.log("Combat", "Pew! Hit: ${closestEnemy != null}")
    }

    private fun useAbility(entity: Entity, key: String, staminaCost: Float, abilityName: String) {
        val combat = combatMapper.get(entity)
        val stamina = staminaMapper.get(entity)
        val state = stateMapper.get(entity)
        val transform = transformMapper.get(entity)

        // Check stamina
        if (stamina != null && !stamina.consume(staminaCost)) {
            return
        }

        // Use ability
        combat.useAbility(key)

        // Set attacking state
        if (state != null) {
            state.current = StateComponent.ATTACKING
        }

        // Spawn Projectile
        val engine = this.engine as? com.badlogic.ashley.core.PooledEngine
        if (engine != null && transform != null) {
            val spawnPos = transform.position.cpy().add(0f, 1.5f, 0f) // Eye level
            val direction = camera.direction.cpy()

            when (key) {
                "Q" -> com.aetheria.mmo.entities.ProjectileFactory.createPlasmaBolt(engine, spawnPos, direction, entity)
                "E" -> com.aetheria.mmo.entities.ProjectileFactory.createFireball(engine, spawnPos, direction, entity)
                "R" -> com.aetheria.mmo.entities.ProjectileFactory.createLightningBolt(engine, spawnPos, direction, entity)
                "F" -> com.aetheria.mmo.entities.ProjectileFactory.createVoidMissile(engine, spawnPos, direction, entity)
            }
        }
    }
}
