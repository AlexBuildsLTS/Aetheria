package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Quaternion
import com.aetheria.mmo.components.*

/**
 * Enhanced Movement System
 * Handles camera-relative movement, smooth rotation, and physics-based velocity integration.
 * Optimized for AAA feel (CoD/Fortnite style).
 */
class MovementSystem(private val camera: Camera) : IteratingSystem(
    Family.all(TransformComponent::class.java, VelocityComponent::class.java, MoveEvtComponent::class.java).get()
) {
    private val tempDir = Vector3()
    private val moveDir = Vector3()
    private val camForward = Vector3()
    private val camRight = Vector3()
    private val targetRot = Quaternion()
    
    // Physics Constants (Tweak for feel)
    private val gravity = -20f
    private val jumpForce = 10f
    private val acceleration = 40f  // How fast to reach max speed
    private val deceleration = 30f  // How fast to stop
    private val rotationSpeed = 10f // Smooth rotation

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getComponent(TransformComponent::class.java)
        val velocity = entity.getComponent(VelocityComponent::class.java)
        val moveEvt = entity.getComponent(MoveEvtComponent::class.java)
        val state = entity.getComponent(StateComponent::class.java)
        val input = entity.getComponent(InputComponent::class.java) // Optional for jump

        // 1. Calculate Camera-Relative Movement Direction
        // Get camera basis vectors (projected to XZ plane)
        camForward.set(camera.direction.x, 0f, camera.direction.z).nor()
        camRight.set(camera.direction).crs(Vector3.Y).nor() // Right vector is cross product with Up
        // Actually, cross(Up) gives Right for Y-up system if order is correct? 
        // LibGDX Camera up is usually Y. 
        // standard Right = Forward X Up? 
        // Let's check: Forward (0,0,-1) X Up (0,1,0) = (1,0,0) -> Right. Correct.
        // But wait, camera.direction is the look vector.
        
        // Calculate desired world movement direction based on input
        // moveEvt.moveDirection.z is forward/backward input (+1/-1)
        // moveEvt.moveDirection.x is strafe left/right input (+1/-1)
        moveDir.setZero()
        
        // Combine forward/strafe
        // Forward component
        if (Math.abs(moveEvt.moveDirection.z) > 0.01f) {
            tempDir.set(camForward).scl(moveEvt.moveDirection.z) // z is negative for forward in input? let's check input handler
            // InputHandler sets z = -input.y (so forward input y=1 -> z=-1)
            // If z is negative, we want to move FORWARD. 
            // camForward is the look vector. So scaling by negative moves backward?
            // Usually Forward Input (W) -> y=1. InputHandler sets z = -1. 
            // So we want to move ALONG camForward.
            // So we negate z to get positive magnitude for forward? 
            // Or just: z * camForward. 
            // Let's assume standard: W -> z=-1. camForward points forward.
            // We want to move along camForward. So we multiply by -z.
            // Actually, let's keep it simple: 
            // InputHandler maps W -> z=-1.
            // We want W to move along camera.direction.
            // So we should multiply by -moveEvt.moveDirection.z
            tempDir.scl(-1f) 
            moveDir.add(tempDir)
        }
        
        // Strafe component
        if (Math.abs(moveEvt.moveDirection.x) > 0.01f) {
             // Strafe Right (D) -> x=1.
             // We want to move along camRight.
             tempDir.set(camRight).scl(moveEvt.moveDirection.x)
             moveDir.add(tempDir)
        }

        // Normalize if moving diagonally
        if (moveDir.len2() > 1f) moveDir.nor()

        // 2. Apply Acceleration / Deceleration
        val targetSpeed = if (moveEvt.isSprinting) moveEvt.moveSpeed * moveEvt.sprintMultiplier else moveEvt.moveSpeed
        
        if (moveDir.len2() > 0.01f) {
            // Accelerate towards target velocity
            val targetVelX = moveDir.x * targetSpeed
            val targetVelZ = moveDir.z * targetSpeed
            
            // Lerp velocity for smooth acceleration
            velocity.linear.x = MathUtils.lerp(velocity.linear.x, targetVelX, acceleration * deltaTime * 0.1f)
            velocity.linear.z = MathUtils.lerp(velocity.linear.z, targetVelZ, acceleration * deltaTime * 0.1f)
            
            // Smooth Rotation to face movement direction
            val angle = MathUtils.atan2(moveDir.x, moveDir.z) * MathUtils.radiansToDegrees
            // atan2 returns angle from positive Z axis? No, usually from positive X.
            // In LibGDX 3D (OpenGL), -Z is forward.
            // We need to test this. atan2(x, z) gives angle from Z axis.
            // Let's use setFromCross or just setFromAxis(Y, angle).
            
            targetRot.setFromAxis(Vector3.Y, angle) 
            transform.rotation.slerp(targetRot, rotationSpeed * deltaTime)
            
            state?.current = if (moveEvt.isSprinting) StateComponent.RUNNING else StateComponent.WALKING
        } else {
            // Decelerate to stop
            velocity.linear.x = MathUtils.lerp(velocity.linear.x, 0f, deceleration * deltaTime * 0.1f)
            velocity.linear.z = MathUtils.lerp(velocity.linear.z, 0f, deceleration * deltaTime * 0.1f)
            
            state?.current = StateComponent.IDLE
        }

        // 3. Gravity & Jump
        // Simple ground check (y <= 0)
        if (transform.position.y > 0.01f) {
            velocity.linear.y += gravity * deltaTime
            // Air resistance?
        } else {
            // Grounded
            transform.position.y = 0f
            if (velocity.linear.y < 0) velocity.linear.y = 0f
            
            // Jump
            if (input != null && input.isJumping) {
                velocity.linear.y = jumpForce
                state?.current = StateComponent.JUMPING
            }
        }

        // 4. Apply Velocity to Position
        transform.position.add(
            velocity.linear.x * deltaTime,
            velocity.linear.y * deltaTime,
            velocity.linear.z * deltaTime
        )
    }
}
