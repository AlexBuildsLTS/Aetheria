package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.TransformComponent
import com.aetheria.mmo.components.PlayerComponent

class CameraSystem(private val camera: PerspectiveCamera) : IteratingSystem(
    Family.all(TransformComponent::class.java, PlayerComponent::class.java).get()
) {
    private var distance = 12f
    private var angleX = 0f
    private var angleY = 30f
    private val targetPos = Vector3()
    private val offset = Vector3()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getComponent(TransformComponent::class.java)

        // Rotation input
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            angleX -= Gdx.input.deltaX * 0.3f
            angleY = MathUtils.clamp(angleY + Gdx.input.deltaY * 0.3f, 5f, 85f)
        }

        // Calculate position
        val hDist = distance * MathUtils.cos(angleY * MathUtils.degreesToRadians)
        val vDist = distance * MathUtils.sin(angleY * MathUtils.degreesToRadians)
        
        offset.set(
            hDist * MathUtils.sin(angleX * MathUtils.degreesToRadians),
            vDist,
            hDist * MathUtils.cos(angleX * MathUtils.degreesToRadians)
        )

        targetPos.set(transform.position).add(offset)
        camera.position.lerp(targetPos, 10f * deltaTime)
        camera.lookAt(transform.position.x, transform.position.y + 1.5f, transform.position.z)
        camera.update()
    }
}
