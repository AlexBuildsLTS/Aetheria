package com.aetheria.mmo.systems

import com.aetheria.mmo.components.ModelComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight

class RenderSystem(private val camera: PerspectiveCamera) : IteratingSystem(
    Family.all(ModelComponent::class.java).get()
) {
    private val modelBatch = ModelBatch()
    private val environment = Environment()

    init {
        // Simple lighting setup
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
    }

    // Called once per frame, before processing entities
    override fun update(deltaTime: Float) {
        modelBatch.begin(camera)
        super.update(deltaTime) // This calls processEntity for every object
        modelBatch.end()
    }

    // Called for every single entity that has a ModelComponent
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val modelComp = entity.getComponent(ModelComponent::class.java)
        if (modelComp.isVisible) {
            modelBatch.render(modelComp.modelInstance, environment)
        }
    }
}