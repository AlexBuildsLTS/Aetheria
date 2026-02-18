package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

/**
 * Time Debt Component
 * Tracks accumulated time debt for time-manipulation mechanics
 * Used for time rewind, slow-motion, and temporal abilities
 */
class TimeDebtComponent : Component, Pool.Poolable {
    var debtAmount: Float = 0f // Seconds of time debt
    var maxDebt: Float = 10f
    var repaymentRate: Float = 1f // Debt repaid per second
    var isInDebt: Boolean = false
    var penaltyMultiplier: Float = 1.5f // Damage/effects multiplier while in debt

    fun addDebt(amount: Float) {
        debtAmount = minOf(debtAmount + amount, maxDebt)
        isInDebt = debtAmount > 0f
    }

    fun repayDebt(deltaTime: Float) {
        if (debtAmount > 0f) {
            debtAmount = maxOf(0f, debtAmount - repaymentRate * deltaTime)
            isInDebt = debtAmount > 0f
        }
    }

    fun getDebtPercent(): Float = if (maxDebt > 0f) debtAmount / maxDebt else 0f

    override fun reset() {
        debtAmount = 0f
        maxDebt = 10f
        repaymentRate = 1f
        isInDebt = false
        penaltyMultiplier = 1.5f
    }
}
