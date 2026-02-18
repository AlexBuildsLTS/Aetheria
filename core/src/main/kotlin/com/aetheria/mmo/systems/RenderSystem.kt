package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.math.Matrix4
import com.aetheria.mmo.components.ModelComponent
import com.aetheria.mmo.components.TransformComponent
import com.aetheria.mmo.components.AnimationComponent

/**
 * AAA+ Tier Render System
 * Renders all 3D models with proper transform synchronization.
 * Uses the camera from CameraSystem for consistent view.
 */
class RenderSystem(
    private val batch: ModelBatch,
    val camera: PerspectiveCamera
) : IteratingSystem(
    Family.all(ModelComponent::class.java, TransformComponent::class.java).get()
) {
    val environment: Environment = Environment()
    private val tempMatrix = Matrix4()

    init {
        // Setup PBR-style lighting for realistic rendering
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.35f, 1f))

        // Main directional light (sun)
        environment.add(DirectionalLight().set(0.9f, 0.9f, 0.85f, -0.5f, -0.8f, -0.3f))

        // Fill light (softer, from opposite direction)
        environment.add(DirectionalLight().set(0.3f, 0.3f, 0.4f, 0.5f, -0.2f, 0.5f))
    }

    override fun update(deltaTime: Float) {
        // Begin rendering with the camera from CameraSystem
        batch.begin(camera)
        super.update(deltaTime) // Calls processEntity for each entity
        batch.end()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val modelComp = entity.getComponent(ModelComponent::class.java)
        val transformComp = entity.getComponent(TransformComponent::class.java)
        val animComp = entity.getComponent(AnimationComponent::class.java)

        // Skip hidden entities
        if (transformComp.isHidden || !modelComp.isVisible) {
            return
        }

        // Update animation controller
        if (animComp != null) {
            animComp.controller.update(deltaTime)
        }

        // Sync ModelInstance transform with TransformComponent
        tempMatrix.idt()
        tempMatrix.translate(transformComp.position)
        tempMatrix.rotate(transformComp.rotation)
        tempMatrix.scale(transformComp.scale.x, transformComp.scale.y, transformComp.scale.z)

        modelComp.modelInstance.transform.set(tempMatrix)

        // Render the model
        batch.render(modelComp.modelInstance, environment)
    }
}