package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

/**
 * Weapon Component
 * Stores weapon stats and state
 */
class WeaponComponent : Component, Pool.Poolable {
    var weaponId: String = ""
    var damage: Float = 10f
    var attackSpeed: Float = 1f // Attacks per second
    var range: Float = 2f
    var critChance: Float = 0.05f // 5%
    var critMultiplier: Float = 2f
    var weaponType: WeaponType = WeaponType.SWORD
    var isEquipped: Boolean = false
    var durability: Float = 100f
    var maxDurability: Float = 100f

    // Attack timing
    var timeSinceLastAttack: Float = 0f

    fun canAttack(): Boolean {
        return timeSinceLastAttack >= (1f / attackSpeed) && durability > 0f
    }

    fun resetAttackTimer() {
        timeSinceLastAttack = 0f
    }

    override fun reset() {
        weaponId = ""
        damage = 10f
        attackSpeed = 1f
        range = 2f
        critChance = 0.05f
        critMultiplier = 2f
        weaponType = WeaponType.SWORD
        isEquipped = false
        durability = 100f
        maxDurability = 100f
        timeSinceLastAttack = 0f
    }
}

enum class WeaponType {
    SWORD,
    AXE,
    SPEAR,
    BOW,
    STAFF,
    WAND,
    DAGGER,
    HAMMER,
    RIFLE,
    PISTOL
}
