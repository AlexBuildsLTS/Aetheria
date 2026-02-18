package com.aetheria.mmo.physics

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Disposable

/**
 * Hitbox Manager
 * Manages hitboxes for combat, abilities, and interactions
 * Handles sphere, box, and capsule hitboxes
 */
class HitboxManager : Disposable {

    data class Hitbox(
        val shape: Any, // Simplified - will be btCollisionShape when physics is integrated
        val offset: Vector3 = Vector3.Zero,
        val isActive: Boolean = true,
        val damageMultiplier: Float = 1f,
        val type: HitboxType = HitboxType.BODY
    )

    enum class HitboxType {
        HEAD,      // Critical hits
        BODY,      // Normal damage
        LIMBS,     // Reduced damage
        WEAK_POINT // Extra damage
    }

    private val hitboxes = mutableMapOf<String, Hitbox>()
    private val activeHitboxes = mutableSetOf<String>()

    /**
     * Creates a sphere hitbox
     */
    fun createSphereHitbox(
        name: String,
        radius: Float,
        offset: Vector3 = Vector3.Zero,
        type: HitboxType = HitboxType.BODY
    ): Hitbox {
        val shape = "SphereShape:$radius" // Simplified placeholder
        val hitbox = Hitbox(shape, offset.cpy(), true, getDamageMultiplier(type), type)
        hitboxes[name] = hitbox
        activeHitboxes.add(name)
        return hitbox
    }

    /**
     * Creates a box hitbox
     */
    fun createBoxHitbox(
        name: String,
        halfExtents: Vector3,
        offset: Vector3 = Vector3.Zero,
        type: HitboxType = HitboxType.BODY
    ): Hitbox {
        val shape = "BoxShape:$halfExtents"
        val hitbox = Hitbox(shape, offset.cpy(), true, getDamageMultiplier(type), type)
        hitboxes[name] = hitbox
        activeHitboxes.add(name)
        return hitbox
    }

    /**
     * Creates a capsule hitbox
     */
    fun createCapsuleHitbox(
        name: String,
        radius: Float,
        height: Float,
        offset: Vector3 = Vector3.Zero,
        type: HitboxType = HitboxType.BODY
    ): Hitbox {
        val shape = "CapsuleShape:$radius:$height"
        val hitbox = Hitbox(shape, offset.cpy(), true, getDamageMultiplier(type), type)
        hitboxes[name] = hitbox
        activeHitboxes.add(name)
        return hitbox
    }

    /**
     * Creates a compound hitbox (multiple shapes)
     */
    fun createCompoundHitbox(name: String, childHitboxes: List<Pair<Any, Vector3>>): Hitbox {
        val compound = "CompoundShape:${childHitboxes.size}"
        val hitbox = Hitbox(compound, Vector3.Zero, true, 1f, HitboxType.BODY)
        hitboxes[name] = hitbox
        activeHitboxes.add(name)
        return hitbox
    }

    /**
     * Creates a standard humanoid hitbox set
     */
    fun createHumanoidHitboxes(baseName: String): Map<String, Hitbox> {
        val hitboxMap = mutableMapOf<String, Hitbox>()

        // Head
        hitboxMap["${baseName}_head"] = createSphereHitbox(
            "${baseName}_head",
            0.15f,
            Vector3(0f, 1.6f, 0f),
            HitboxType.HEAD
        )

        // Torso
        hitboxMap["${baseName}_torso"] = createCapsuleHitbox(
            "${baseName}_torso",
            0.3f,
            0.8f,
            Vector3(0f, 1.0f, 0f),
            HitboxType.BODY
        )

        // Legs
        hitboxMap["${baseName}_legs"] = createCapsuleHitbox(
            "${baseName}_legs",
            0.15f,
            0.8f,
            Vector3(0f, 0.4f, 0f),
            HitboxType.LIMBS
        )

        return hitboxMap
    }

    /**
     * Gets a hitbox by name
     */
    fun getHitbox(name: String): Hitbox? = hitboxes[name]

    /**
     * Activates a hitbox
     */
    fun activateHitbox(name: String) {
        if (hitboxes.containsKey(name)) {
            activeHitboxes.add(name)
        }
    }

    /**
     * Deactivates a hitbox
     */
    fun deactivateHitbox(name: String) {
        activeHitboxes.remove(name)
    }

    /**
     * Checks if a hitbox is active
     */
    fun isHitboxActive(name: String): Boolean = activeHitboxes.contains(name)

    /**
     * Gets all active hitboxes
     */
    fun getActiveHitboxes(): List<Pair<String, Hitbox>> {
        return activeHitboxes.mapNotNull { name ->
            hitboxes[name]?.let { name to it }
        }
    }

    /**
     * Removes a hitbox
     */
    fun removeHitbox(name: String) {
        hitboxes.remove(name)
        activeHitboxes.remove(name)
    }

    /**
     * Gets damage multiplier for hitbox type
     */
    private fun getDamageMultiplier(type: HitboxType): Float {
        return when (type) {
            HitboxType.HEAD -> 2.0f       // Headshot bonus
            HitboxType.BODY -> 1.0f        // Normal damage
            HitboxType.LIMBS -> 0.75f      // Reduced damage
            HitboxType.WEAK_POINT -> 3.0f  // Critical weak point
        }
    }

    /**
     * Clears all hitboxes
     */
    fun clear() {
        hitboxes.clear()
        activeHitboxes.clear()
    }

    override fun dispose() {
        clear()
    }
}
