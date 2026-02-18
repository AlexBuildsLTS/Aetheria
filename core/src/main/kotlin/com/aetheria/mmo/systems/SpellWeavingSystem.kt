package com.aetheria.mmo.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.aetheria.mmo.components.*
import com.aetheria.mmo.utils.Constants
import com.aetheria.mmo.utils.Logger
import com.aetheria.mmo.utils.TimeUtils

/**
 * Spell Weaving System
 * Handles spell combo system where players can chain spells together
 * for enhanced effects (e.g., Fire + Ice = Steam Explosion)
 */
class SpellWeavingSystem : IteratingSystem(
    Family.all(PlayerComponent::class.java, CombatComponent::class.java).get()
) {

    private val combatMapper = ComponentMapper.getFor(CombatComponent::class.java)
    private val stateMapper = ComponentMapper.getFor(StateComponent::class.java)

    // Spell weaving state per entity
    private val weavingState = mutableMapOf<Entity, SpellWeave>()

    // Spell combo definitions
    private val combos = mutableMapOf<String, SpellCombo>()

    init {
        // Define spell combos
        registerCombos()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val weave = weavingState[entity] ?: return

        // Update weave window timer
        weave.timeSinceLastCast += deltaTime

        // Clear weave if window expired
        if (weave.timeSinceLastCast > Constants.SPELL_WEAVING_WINDOW) {
            clearWeave(entity)
        }
    }

    /**
     * Cast spell and check for combos
     */
    fun castSpell(entity: Entity, spellId: String): SpellCastResult {
        val combat = combatMapper.get(entity) ?: return SpellCastResult.FAILED
        val state = stateMapper.get(entity)

        // Get or create weave state
        val weave = weavingState.getOrPut(entity) { SpellWeave() }

        // Add spell to sequence
        weave.spellSequence.add(spellId)
        weave.timeSinceLastCast = 0f

        // Limit sequence length
        if (weave.spellSequence.size > Constants.MAX_SPELL_COMBO_LENGTH) {
            weave.spellSequence.removeAt(0)
        }

        // Check for combo
        val combo = checkForCombo(weave.spellSequence)

        if (combo != null) {
            // Execute combo
            Logger.info("SpellWeaving", "Combo activated: ${combo.name}")
            executeCombo(entity, combo)
            clearWeave(entity)
            return SpellCastResult.COMBO(combo)
        } else {
            // Cast normal spell
            Logger.debug("SpellWeaving", "Cast spell: $spellId")
            return SpellCastResult.SUCCESS(spellId)
        }
    }

    /**
     * Check if spell sequence matches any combo
     */
    private fun checkForCombo(sequence: List<String>): SpellCombo? {
        // Check all combos
        for (combo in combos.values) {
            if (matchesCombo(sequence, combo.sequence)) {
                return combo
            }
        }
        return null
    }

    /**
     * Check if sequence matches combo pattern
     */
    private fun matchesCombo(sequence: List<String>, pattern: List<String>): Boolean {
        if (sequence.size < pattern.size) return false

        // Check last N spells match pattern
        val startIndex = sequence.size - pattern.size
        for (i in pattern.indices) {
            if (sequence[startIndex + i] != pattern[i]) {
                return false
            }
        }
        return true
    }

    /**
     * Execute combo effect
     */
    private fun executeCombo(entity: Entity, combo: SpellCombo) {
        val combat = combatMapper.get(entity) ?: return

        // Apply combo effects
        // This would trigger VFX, damage, buffs, etc.
        Logger.info("SpellWeaving", "Executing combo: ${combo.name} - ${combo.description}")

        // Example: Apply damage multiplier
        // This would be handled by other systems based on the combo type
    }

    /**
     * Clear spell weave
     */
    fun clearWeave(entity: Entity) {
        weavingState[entity]?.spellSequence?.clear()
        weavingState[entity]?.timeSinceLastCast = 0f
    }

    /**
     * Get current spell sequence
     */
    fun getSpellSequence(entity: Entity): List<String> {
        return weavingState[entity]?.spellSequence?.toList() ?: emptyList()
    }

    /**
     * Register spell combos
     */
    private fun registerCombos() {
        // Fire + Ice = Steam Explosion
        combos["steam_explosion"] = SpellCombo(
            id = "steam_explosion",
            name = "Steam Explosion",
            description = "Combines fire and ice to create a devastating steam explosion",
            sequence = listOf("fireball", "ice_shard"),
            damageMultiplier = 2.5f,
            effectType = ComboEffectType.AOE_DAMAGE
        )

        // Lightning + Water = Chain Lightning
        combos["chain_lightning"] = SpellCombo(
            id = "chain_lightning",
            name = "Chain Lightning",
            description = "Electrifies water to create chain lightning",
            sequence = listOf("lightning_bolt", "water_wave"),
            damageMultiplier = 2.0f,
            effectType = ComboEffectType.CHAIN_DAMAGE
        )

        // Fire + Fire + Fire = Inferno
        combos["inferno"] = SpellCombo(
            id = "inferno",
            name = "Inferno",
            description = "Triple fire spell creates a massive inferno",
            sequence = listOf("fireball", "fireball", "fireball"),
            damageMultiplier = 3.0f,
            effectType = ComboEffectType.DOT
        )

        // Void + Void = Void Collapse
        combos["void_collapse"] = SpellCombo(
            id = "void_collapse",
            name = "Void Collapse",
            description = "Dual void spells create a collapsing singularity",
            sequence = listOf("void_bolt", "void_bolt"),
            damageMultiplier = 2.8f,
            effectType = ComboEffectType.PULL
        )

        // Light + Dark = Twilight Burst
        combos["twilight_burst"] = SpellCombo(
            id = "twilight_burst",
            name = "Twilight Burst",
            description = "Merges light and dark energies",
            sequence = listOf("holy_light", "shadow_bolt"),
            damageMultiplier = 2.2f,
            effectType = ComboEffectType.STUN
        )
    }

    /**
     * Register custom combo
     */
    fun registerCombo(combo: SpellCombo) {
        combos[combo.id] = combo
        Logger.info("SpellWeaving", "Registered combo: ${combo.name}")
    }
}

/**
 * Spell Weave State
 */
private data class SpellWeave(
    val spellSequence: MutableList<String> = mutableListOf(),
    var timeSinceLastCast: Float = 0f
)

/**
 * Spell Combo Definition
 */
data class SpellCombo(
    val id: String,
    val name: String,
    val description: String,
    val sequence: List<String>,
    val damageMultiplier: Float = 1.5f,
    val effectType: ComboEffectType = ComboEffectType.DAMAGE
)

/**
 * Combo Effect Types
 */
enum class ComboEffectType {
    DAMAGE,
    AOE_DAMAGE,
    CHAIN_DAMAGE,
    DOT,
    STUN,
    PULL,
    PUSH,
    HEAL,
    BUFF
}

/**
 * Spell Cast Result
 */
sealed class SpellCastResult {
    data class SUCCESS(val spellId: String) : SpellCastResult()
    data class COMBO(val combo: SpellCombo) : SpellCastResult()
    object FAILED : SpellCastResult()
}
