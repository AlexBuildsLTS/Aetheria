package com.aetheria.mmo.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

/**
 * Inventory Component
 * Manages entity inventory with slots
 */
class InventoryComponent : Component, Pool.Poolable {
    var slots: Array<InventorySlot?> = arrayOfNulls(30)
    var maxSlots: Int = 30
    var gold: Int = 0

    fun addItem(itemId: String, quantity: Int = 1, metadata: Map<String, Any> = emptyMap()): Boolean {
        // Try to stack with existing items first
        for (i in 0 until maxSlots) {
            val slot = slots[i]
            if (slot != null && slot.itemId == itemId && slot.isStackable) {
                slot.quantity += quantity
                return true
            }
        }

        // Find empty slot
        for (i in 0 until maxSlots) {
            if (slots[i] == null) {
                slots[i] = InventorySlot(itemId, quantity, metadata)
                return true
            }
        }

        return false // Inventory full
    }

    fun removeItem(itemId: String, quantity: Int = 1): Boolean {
        var remaining = quantity

        for (i in 0 until maxSlots) {
            val slot = slots[i]
            if (slot != null && slot.itemId == itemId) {
                if (slot.quantity >= remaining) {
                    slot.quantity -= remaining
                    if (slot.quantity <= 0) {
                        slots[i] = null
                    }
                    return true
                } else {
                    remaining -= slot.quantity
                    slots[i] = null
                }
            }
        }

        return remaining == 0
    }

    fun hasItem(itemId: String, quantity: Int = 1): Boolean {
        var count = 0
        for (slot in slots) {
            if (slot?.itemId == itemId) {
                count += slot.quantity
            }
        }
        return count >= quantity
    }

    fun getItemCount(itemId: String): Int {
        var count = 0
        for (slot in slots) {
            if (slot?.itemId == itemId) {
                count += slot.quantity
            }
        }
        return count
    }

    override fun reset() {
        slots = arrayOfNulls(30)
        maxSlots = 30
        gold = 0
    }
}

data class InventorySlot(
    var itemId: String,
    var quantity: Int = 1,
    var metadata: Map<String, Any> = emptyMap(),
    var isStackable: Boolean = true
)
