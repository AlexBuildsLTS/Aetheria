package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

/**
 * Heat Component
 * Tracks weapon/ability heat buildup (overheating mechanic)
 * Prevents spam by requiring cooldown when overheated
 */
class HeatComponent : Component, Pool.Poolable {
    var currentHeat: Float = 0f
    var maxHeat: Float = 100f
    var heatPerShot: Float = 10f
    var cooldownRate: Float = 20f // Heat dissipated per second
    var isOverheated: Boolean = false
    var overheatPenaltyTime: Float = 2f // Seconds before cooling starts
    var timeSinceOverheat: Float = 0f

    fun addHeat(amount: Float) {
        currentHeat = minOf(currentHeat + amount, maxHeat)
        if (currentHeat >= maxHeat) {
            isOverheated = true
            timeSinceOverheat = 0f
        }
    }

    fun update(deltaTime: Float) {
        if (isOverheated) {
            timeSinceOverheat += deltaTime
            if (timeSinceOverheat >= overheatPenaltyTime) {
                // Start cooling down
                currentHeat = maxOf(0f, currentHeat - cooldownRate * deltaTime)
                if (currentHeat <= 0f) {
                    isOverheated = false
                }
            }
        } else {
            // Normal cooling
            currentHeat = maxOf(0f, currentHeat - cooldownRate * deltaTime)
        }
    }

    fun canFire(): Boolean = !isOverheated && currentHeat < maxHeat

    fun getHeatPercent(): Float = if (maxHeat > 0f) currentHeat / maxHeat else 0f

    override fun reset() {
        currentHeat = 0f
        maxHeat = 100f
        heatPerShot = 10f
        cooldownRate = 20f
        isOverheated = false
        overheatPenaltyTime = 2f
        timeSinceOverheat = 0f
    }
}
