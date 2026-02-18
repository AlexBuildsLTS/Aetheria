package com.aetheria.mmo.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.aetheria.mmo.components.*
import com.aetheria.mmo.utils.Constants
import com.aetheria.mmo.utils.Logger

/**
 * Buff System
 * Manages buffs and debuffs on entities
 * Handles buff application, duration, stacking, and removal
 */
class BuffSystem : IteratingSystem(
    Family.all(HealthComponent::class.java).get()
) {

    private val healthMapper = ComponentMapper.getFor(HealthComponent::class.java)
    private val combatMapper = ComponentMapper.getFor(CombatComponent::class.java)
    private val moveMapper = ComponentMapper.getFor(MoveEvtComponent::class.java)

    // Active buffs per entity
    private val entityBuffs = mutableMapOf<Entity, MutableList<Buff>>()

    private var tickAccumulator = 0f

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        // Update buff durations and tick effects
        tickAccumulator += deltaTime

        if (tickAccumulator >= Constants.BUFF_TICK_RATE) {
            tickBuffs(tickAccumulator)
            tickAccumulator = 0f
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val buffs = entityBuffs[entity] ?: return

        // Update buff durations
        buffs.forEach { buff ->
            buff.remainingDuration -= deltaTime

            // Apply continuous effects
            if (buff.tickDamage != 0f) {
                // Will be applied in tickBuffs
            }
        }

        // Remove expired buffs
        buffs.removeAll { buff ->
            if (buff.remainingDuration <= 0f) {
                removeBuff(entity, buff)
                true
            } else {
                false
            }
        }
    }

    /**
     * Tick buff effects (damage over time, healing over time, etc.)
     */
    private fun tickBuffs(deltaTime: Float) {
        entityBuffs.forEach { (entity, buffs) ->
            val health = healthMapper.get(entity)

            buffs.forEach { buff ->
                // Apply tick damage/healing
                if (buff.tickDamage != 0f) {
                    health.current -= buff.tickDamage * deltaTime
                    health.current = health.current.coerceIn(0f, health.max)
                }

                if (buff.tickHealing != 0f) {
                    health.current += buff.tickHealing * deltaTime
                    health.current = health.current.coerceIn(0f, health.max)
                }
            }
        }
    }

    /**
     * Apply buff to entity
     */
    fun applyBuff(entity: Entity, buff: Buff) {
        val buffs = entityBuffs.getOrPut(entity) { mutableListOf() }

        // Check if buff already exists
        val existingBuff = buffs.find { it.id == buff.id }

        if (existingBuff != null) {
            // Handle stacking
            when (buff.stackType) {
                StackType.REFRESH -> {
                    // Refresh duration
                    existingBuff.remainingDuration = buff.duration
                }
                StackType.STACK -> {
                    // Increase stacks
                    if (existingBuff.stacks < buff.maxStacks) {
                        existingBuff.stacks++
                        existingBuff.remainingDuration = buff.duration
                        applyBuffEffects(entity, existingBuff)
                    }
                }
                StackType.REPLACE -> {
                    // Remove old, add new
                    removeBuff(entity, existingBuff)
                    buffs.remove(existingBuff)
                    buffs.add(buff)
                    applyBuffEffects(entity, buff)
                }
                StackType.IGNORE -> {
                    // Do nothing
                }
            }
        } else {
            // Add new buff
            buffs.add(buff)
            applyBuffEffects(entity, buff)
            Logger.debug("BuffSystem", "Applied buff ${buff.name} to entity")
        }
    }

    /**
     * Apply buff stat modifiers
     */
    private fun applyBuffEffects(entity: Entity, buff: Buff) {
        val combat = combatMapper.get(entity)
        val move = moveMapper.get(entity)
        val health = healthMapper.get(entity)

        // Apply stat modifiers
        combat?.let {
            it.attackPower += buff.attackPowerBonus * buff.stacks
            it.attackSpeed += buff.attackSpeedBonus * buff.stacks
            it.critChance += buff.critChanceBonus * buff.stacks
        }

        move?.let {
            it.moveSpeed += buff.moveSpeedBonus * buff.stacks
        }

        health?.let {
            it.max += buff.maxHealthBonus * buff.stacks
        }
    }

    /**
     * Remove buff effects
     */
    private fun removeBuff(entity: Entity, buff: Buff) {
        val combat = combatMapper.get(entity)
        val move = moveMapper.get(entity)
        val health = healthMapper.get(entity)

        // Remove stat modifiers
        combat?.let {
            it.attackPower -= buff.attackPowerBonus * buff.stacks
            it.attackSpeed -= buff.attackSpeedBonus * buff.stacks
            it.critChance -= buff.critChanceBonus * buff.stacks
        }

        move?.let {
            it.moveSpeed -= buff.moveSpeedBonus * buff.stacks
        }

        health?.let {
            it.max -= buff.maxHealthBonus * buff.stacks
            it.current = it.current.coerceAtMost(it.max)
        }

        Logger.debug("BuffSystem", "Removed buff ${buff.name} from entity")
    }

    /**
     * Remove all buffs from entity
     */
    fun clearBuffs(entity: Entity) {
        val buffs = entityBuffs[entity] ?: return
        buffs.forEach { removeBuff(entity, it) }
        buffs.clear()
    }

    /**
     * Get active buffs for entity
     */
    fun getBuffs(entity: Entity): List<Buff> {
        return entityBuffs[entity]?.toList() ?: emptyList()
    }

    /**
     * Check if entity has buff
     */
    fun hasBuff(entity: Entity, buffId: String): Boolean {
        return entityBuffs[entity]?.any { it.id == buffId } ?: false
    }

    /**
     * Remove specific buff
     */
    fun removeBuffById(entity: Entity, buffId: String) {
        val buffs = entityBuffs[entity] ?: return
        val buff = buffs.find { it.id == buffId } ?: return
        removeBuff(entity, buff)
        buffs.remove(buff)
    }
}

/**
 * Buff Data Class
 */
data class Buff(
    val id: String,
    val name: String,
    val description: String,
    val duration: Float,
    var remainingDuration: Float = duration,
    val stackType: StackType = StackType.REFRESH,
    val maxStacks: Int = 1,
    var stacks: Int = 1,
    val isDebuff: Boolean = false,

    // Stat modifiers
    val attackPowerBonus: Float = 0f,
    val attackSpeedBonus: Float = 0f,
    val critChanceBonus: Float = 0f,
    val moveSpeedBonus: Float = 0f,
    val maxHealthBonus: Float = 0f,

    // Damage/Healing over time
    val tickDamage: Float = 0f,
    val tickHealing: Float = 0f,

    // Visual
    val iconPath: String? = null
)

/**
 * Buff Stack Type
 */
enum class StackType {
    REFRESH,  // Refresh duration
    STACK,    // Increase stacks
    REPLACE,  // Replace old buff
    IGNORE    // Ignore new buff
}
