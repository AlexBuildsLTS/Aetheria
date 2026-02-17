package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.aetheria.mmo.components.ModelComponent
import com.aetheria.mmo.components.TransformComponent

class RenderSystem(private val camera: Camera, private val environment: Environment) : IteratingSystem(
    // This looks for entities that have BOTH a Model AND a Position
    Family.all(ModelComponent::class.java, TransformComponent::class.java).get()
) {
    private val batch = ModelBatch()

    override fun update(deltaTime: Float) {
        batch.begin(camera)
        super.update(deltaTime)
        batch.end()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val modelComp = entity.getComponent(ModelComponent::class.java)
        val transform = entity.getComponent(TransformComponent::class.java)

        if (modelComp.isVisible && !transform.isHidden) {
            // SYNC: Move the 3D model to where the logic says it should be
            // We use 'modelInstance' here because that is what we defined in Step 2
            modelComp.modelInstance.transform.set(transform.position, transform.rotation)

            // RENDER: Draw it
            batch.render(modelComp.modelInstance, environment)
        }
    }
}