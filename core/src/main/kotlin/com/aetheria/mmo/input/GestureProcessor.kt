package com.aetheria.mmo.input

import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2

/**
 * Gesture Processor
 * Handles touch gestures for mobile controls
 * Supports tap, long press, fling, pinch, and pan
 */
class GestureProcessor : GestureDetector.GestureListener {

    interface GestureCallback {
        fun onTap(x: Float, y: Float)
        fun onDoubleTap(x: Float, y: Float)
        fun onLongPress(x: Float, y: Float)
        fun onFling(velocityX: Float, velocityY: Float)
        fun onPinch(initialDistance: Float, distance: Float)
        fun onPan(deltaX: Float, deltaY: Float)
    }

    private val callbacks = mutableListOf<GestureCallback>()
    private var isPanning = false
    private var isPinching = false

    /**
     * Adds a gesture callback
     */
    fun addCallback(callback: GestureCallback) {
        callbacks.add(callback)
    }

    /**
     * Removes a gesture callback
     */
    fun removeCallback(callback: GestureCallback) {
        callbacks.remove(callback)
    }

    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        if (count == 1) {
            callbacks.forEach { it.onTap(x, y) }
        } else if (count == 2) {
            callbacks.forEach { it.onDoubleTap(x, y) }
        }
        return true
    }

    override fun longPress(x: Float, y: Float): Boolean {
        callbacks.forEach { it.onLongPress(x, y) }
        return true
    }

    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        callbacks.forEach { it.onFling(velocityX, velocityY) }
        return true
    }

    override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean {
        isPanning = true
        callbacks.forEach { it.onPan(deltaX, deltaY) }
        return true
    }

    override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        isPanning = false
        return true
    }

    override fun zoom(initialDistance: Float, distance: Float): Boolean {
        return true
    }

    override fun pinch(initialPointer1: Vector2, initialPointer2: Vector2,
                       pointer1: Vector2, pointer2: Vector2): Boolean {
        val initialDistance = initialPointer1.dst(initialPointer2)
        val currentDistance = pointer1.dst(pointer2)
        isPinching = true
        callbacks.forEach { it.onPinch(initialDistance, currentDistance) }
        return true
    }

    override fun pinchStop() {
        isPinching = false
    }

    fun isPanning(): Boolean = isPanning
    fun isPinching(): Boolean = isPinching

    fun reset() {
        isPanning = false
        isPinching = false
    }
}
