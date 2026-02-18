package com.aetheria.mmo.systems

import com.badlogic.ashley.core.EntitySystem
import com.aetheria.mmo.events.EventQueue

/**
 * Event Cleanup System
 * Processes the event queue each frame
 * Should run LAST in the system priority order
 */
class EventCleanupSystem : EntitySystem() {
    override fun update(deltaTime: Float) {
        // Process all queued events
        EventQueue.process()
    }
}
