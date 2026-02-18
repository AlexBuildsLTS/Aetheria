package com.aetheria.mmo.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Disposable

/**
 * VFX Manager
 * Manages visual effects and particle systems
 * Handles effect pooling and lifecycle
 */
object VFXManager : Disposable {

    private val activeEffects = mutableListOf<ActiveEffect>()
    private val effectPools = mutableMapOf<String, MutableList<ParticleEffect>>()
    private val effectTemplates = mutableMapOf<String, String>()

    private fun ParticleEffect.render(batch: com.badlogic.gdx.graphics.g3d.ModelBatch) {
        // setTransform(com.badlogic.gdx.math.Matrix4().setTranslation(position)) // Position is set when effect is created or updated
        // update(0f) // Update is handled in the main update loop
        this.render(batch)
    }

    data class ActiveEffect(
        val effect: ParticleEffect,
        val position: Vector3,
        var lifetime: Float,
        var elapsed: Float = 0f,
        val followTarget: Vector3? = null
    )

    /**
     * Initializes the VFX manager
     */
    fun initialize() {
        // Register common effects
        registerEffect("hit_impact", "particles/hit_impact.pfx")
        registerEffect("explosion", "particles/explosion.pfx")
        registerEffect("heal", "particles/heal.pfx")
        registerEffect("buff", "particles/buff.pfx")
        registerEffect("debuff", "particles/debuff.pfx")
        registerEffect("teleport", "particles/teleport.pfx")
        registerEffect("level_up", "particles/level_up.pfx")

        Gdx.app.log("VFXManager", "VFX manager initialized")
    }

    /**
     * Registers an effect template
     */
    fun registerEffect(name: String, path: String) {
        effectTemplates[name] = path
        Gdx.app.log("VFXManager", "Effect registered: $name")
    }

    /**
     * Plays an effect at a position
     */
    fun playEffect(name: String, position: Vector3, lifetime: Float = 2f): Boolean {
        val effect = getPooledEffect(name) ?: return false

        effect.setTransform(com.badlogic.gdx.math.Matrix4().setTranslation(position))
        effect.start()

        activeEffects.add(ActiveEffect(effect, position.cpy(), lifetime))
        return true
    }

    /**
     * Plays an effect that follows a target
     */
    fun playEffectFollowing(name: String, target: Vector3, lifetime: Float = 2f): Boolean {
        val effect = getPooledEffect(name) ?: return false

        effect.setTransform(com.badlogic.gdx.math.Matrix4().setTranslation(target))
        effect.start()

        activeEffects.add(ActiveEffect(effect, target.cpy(), lifetime, followTarget = target))
        return true
    }

    /**
     * Plays a hit impact effect
     */
    fun playHitImpact(position: Vector3) {
        playEffect("hit_impact", position, 0.5f)
    }

    /**
     * Plays an explosion effect
     */
    fun playExplosion(position: Vector3, scale: Float = 1f) {
        playEffect("explosion", position, 1.5f)
    }

    /**
     * Plays a heal effect
     */
    fun playHealEffect(position: Vector3) {
        playEffect("heal", position, 1f)
    }

    /**
     * Plays a buff effect
     */
    fun playBuffEffect(target: Vector3) {
        playEffectFollowing("buff", target, 3f)
    }

    /**
     * Plays a debuff effect
     */
    fun playDebuffEffect(target: Vector3) {
        playEffectFollowing("debuff", target, 3f)
    }

    /**
     * Plays a teleport effect
     */
    fun playTeleportEffect(position: Vector3) {
        playEffect("teleport", position, 1f)
    }

    /**
     * Plays a level up effect
     */
    fun playLevelUpEffect(target: Vector3) {
        playEffectFollowing("level_up", target, 2f)
    }

    /**
     * Updates all active effects
     */
    fun update(deltaTime: Float) {
        val iterator = activeEffects.iterator()

        while (iterator.hasNext()) {
            val activeEffect = iterator.next()
            activeEffect.elapsed += deltaTime

            // Update position if following target
            activeEffect.followTarget?.let { target ->
                activeEffect.effect.setTransform(com.badlogic.gdx.math.Matrix4().setTranslation(target))
            }

            // Update effect
            activeEffect.effect.update(deltaTime)

            // Remove if lifetime expired
            if (activeEffect.elapsed >= activeEffect.lifetime) {
                activeEffect.effect.end()
                poolEffect(activeEffect.effect)
                iterator.remove()
            }
        }
    }

    /**
     * Renders all active effects
     */
    fun render(batch: com.badlogic.gdx.graphics.g3d.ModelBatch) {
 activeEffects.forEach { activeEffect ->
 activeEffect.effect.render(batch)
 }
    }

    /**
     * Gets a pooled effect or creates a new one
     */
    private fun getPooledEffect(name: String): ParticleEffect? {
        val pool = effectPools[name]

        return if (pool != null && pool.isNotEmpty()) {
            pool.removeAt(pool.size - 1)
        } else {
            createEffect(name)
        }
    }

    /**
     * Creates a new effect from template
     */
    private fun createEffect(name: String): ParticleEffect? {
        val path = effectTemplates[name]
        if (path == null) {
            Gdx.app.error("VFXManager", "Effect template not found: $name")
            return null
        }

        return try {
            // TODO: Load actual particle effect
            // For now, return a placeholder
            null
        } catch (e: Exception) {
            Gdx.app.error("VFXManager", "Failed to create effect: $name", e)
            null
        }
    }

    /**
     * Returns an effect to the pool
     */
    private fun poolEffect(effect: ParticleEffect) {
        // Determine which pool this effect belongs to
        // For now, just dispose it
        effect.dispose()
    }

    /**
     * Stops all effects
     */
    fun stopAllEffects() {
        activeEffects.forEach { it.effect.end() }
        activeEffects.clear()
    }

    /**
     * Gets the number of active effects
     */
    fun getActiveEffectCount(): Int = activeEffects.size

    override fun dispose() {
        stopAllEffects()

        effectPools.values.forEach { pool ->
            pool.forEach { it.dispose() }
        }
        effectPools.clear()

        Gdx.app.log("VFXManager", "VFX manager disposed")
    }
}


