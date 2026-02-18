package com.aetheria.mmo.physics

/**
 * Collision Filters
 * Helper functions for setting up collision filtering
 */
object CollisionFilters {

    /**
     * Creates a collision filter for a player character
     */
    fun createPlayerFilter(): Pair<Short, Short> {
        return Pair(
            PhysicsConstants.COL_PLAYER.toShort(),
            PhysicsConstants.MASK_PLAYER.toShort()
        )
    }

    /**
     * Creates a collision filter for an enemy
     */
    fun createEnemyFilter(): Pair<Short, Short> {
        return Pair(
            PhysicsConstants.COL_ENEMY.toShort(),
            PhysicsConstants.MASK_ENEMY.toShort()
        )
    }

    /**
     * Creates a collision filter for a projectile
     */
    fun createProjectileFilter(): Pair<Short, Short> {
        return Pair(
            PhysicsConstants.COL_PROJECTILE.toShort(),
            PhysicsConstants.MASK_PROJECTILE.toShort()
        )
    }

    /**
     * Creates a collision filter for ground/terrain
     */
    fun createGroundFilter(): Pair<Short, Short> {
        return Pair(
            PhysicsConstants.COL_GROUND.toShort(),
            PhysicsConstants.MASK_GROUND.toShort()
        )
    }

    /**
     * Creates a collision filter for static objects
     */
    fun createStaticFilter(): Pair<Short, Short> {
        return Pair(
            PhysicsConstants.COL_STATIC.toShort(),
            PhysicsConstants.MASK_STATIC.toShort()
        )
    }

    /**
     * Creates a collision filter for dynamic objects
     */
    fun createDynamicFilter(): Pair<Short, Short> {
        return Pair(
            PhysicsConstants.COL_DYNAMIC.toShort(),
            PhysicsConstants.MASK_DYNAMIC.toShort()
        )
    }

    /**
     * Creates a collision filter for trigger volumes
     */
    fun createTriggerFilter(): Pair<Short, Short> {
        return Pair(
            PhysicsConstants.COL_TRIGGER.toShort(),
            PhysicsConstants.MASK_TRIGGER.toShort()
        )
    }

    /**
     * Creates a collision filter for ragdoll parts
     */
    fun createRagdollFilter(): Pair<Short, Short> {
        return Pair(
            PhysicsConstants.COL_RAGDOLL.toShort(),
            PhysicsConstants.MASK_RAGDOLL.toShort()
        )
    }

    /**
     * Creates a custom collision filter
     */
    fun createCustomFilter(group: Int, mask: Int): Pair<Short, Short> {
        return Pair(group.toShort(), mask.toShort())
    }

    /**
     * Checks if two collision groups should collide
     */
    fun shouldCollide(groupA: Short, maskA: Short, groupB: Short, maskB: Short): Boolean {
        return (groupA.toInt() and maskB.toInt()) != 0 && (groupB.toInt() and maskA.toInt()) != 0
    }
}
