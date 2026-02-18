package com.aetheria.mmo.physics

/**
 * Physics Constants
 * Centralized physics configuration for the game
 */
object PhysicsConstants {
    // Gravity
    const val GRAVITY = 20f // m/s²
    const val GRAVITY_SCALE = 1f

    // Movement limits
    const val MAX_FALL_SPEED = 50f
    const val MAX_JUMP_SPEED = 15f
    const val MAX_HORIZONTAL_SPEED = 20f

    // Character physics
    const val CHARACTER_MASS = 75f // kg
    const val CHARACTER_HEIGHT = 1.8f // meters
    const val CHARACTER_RADIUS = 0.4f // meters
    const val CHARACTER_STEP_HEIGHT = 0.3f // meters

    // Collision groups (bit masks)
    const val COL_NOTHING = 0
    const val COL_GROUND = 1 shl 0      // 1
    const val COL_PLAYER = 1 shl 1      // 2
    const val COL_ENEMY = 1 shl 2       // 4
    const val COL_PROJECTILE = 1 shl 3  // 8
    const val COL_TRIGGER = 1 shl 4     // 16
    const val COL_STATIC = 1 shl 5      // 32
    const val COL_DYNAMIC = 1 shl 6     // 64
    const val COL_RAGDOLL = 1 shl 7     // 128

    // Collision masks (what each group collides with)
    const val MASK_GROUND = COL_PLAYER or COL_ENEMY or COL_PROJECTILE or COL_DYNAMIC or COL_RAGDOLL
    const val MASK_PLAYER = COL_GROUND or COL_ENEMY or COL_STATIC or COL_DYNAMIC or COL_TRIGGER
    const val MASK_ENEMY = COL_GROUND or COL_PLAYER or COL_STATIC or COL_DYNAMIC or COL_TRIGGER
    const val MASK_PROJECTILE = COL_GROUND or COL_PLAYER or COL_ENEMY or COL_STATIC
    const val MASK_TRIGGER = COL_PLAYER or COL_ENEMY
    const val MASK_STATIC = COL_PLAYER or COL_ENEMY or COL_PROJECTILE or COL_DYNAMIC or COL_RAGDOLL
    const val MASK_DYNAMIC = COL_GROUND or COL_PLAYER or COL_ENEMY or COL_STATIC or COL_DYNAMIC
    const val MASK_RAGDOLL = COL_GROUND or COL_STATIC

    // Physics simulation
    const val FIXED_TIME_STEP = 1f / 60f // 60 FPS physics
    const val MAX_SUB_STEPS = 10
    const val SOLVER_ITERATIONS = 10

    // Damping
    const val LINEAR_DAMPING = 0.1f
    const val ANGULAR_DAMPING = 0.8f

    // Friction and restitution
    const val DEFAULT_FRICTION = 0.5f
    const val DEFAULT_RESTITUTION = 0.0f
    const val ICE_FRICTION = 0.05f
    const val RUBBER_RESTITUTION = 0.9f

    // Raycast
    const val MAX_RAYCAST_DISTANCE = 1000f
    const val INTERACTION_DISTANCE = 3f

    // Projectile physics
    const val PROJECTILE_SPEED = 30f
    const val PROJECTILE_GRAVITY_SCALE = 0.5f
    const val PROJECTILE_LIFETIME = 5f // seconds

    // Ragdoll
    const val RAGDOLL_MASS = 10f
    const val RAGDOLL_DAMPING = 0.5f
    const val RAGDOLL_SLEEP_THRESHOLD = 0.1f

    // Performance
    const val DEACTIVATION_TIME = 2f // seconds before objects sleep
    const val SLEEP_LINEAR_VELOCITY = 0.8f
    const val SLEEP_ANGULAR_VELOCITY = 1.0f
}
