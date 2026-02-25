package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.aetheria.mmo.components.ModelComponent
import com.aetheria.mmo.components.TransformComponent
import net.mgsx.gltf.scene3d.scene.SceneManager
import net.mgsx.gltf.scene3d.scene.Scene

class RenderSystem(
    private val sceneManager: SceneManager,
    private val camera: PerspectiveCamera
) : IteratingSystem(
    Family.all(ModelComponent::class.java, TransformComponent::class.java).get()
) {
    private val entityScenes = mutableMapOf<Entity, Scene>()

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        // Note: sceneManager.update and render are called in GameWorldScreen
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val modelComp = entity.getComponent(ModelComponent::class.java)
        val transformComp = entity.getComponent(TransformComponent::class.java)

        if (modelComp.modelInstance == null) return

        var scene = entityScenes[entity]
        if (scene == null) {
            scene = Scene(modelComp.modelInstance)
            sceneManager.addScene(scene)
            entityScenes[entity] = scene
        }

        // Sync transforms
        scene.modelInstance.transform.set(
            transformComp.position,
            transformComp.rotation,
            transformComp.scale
        )
        
        if (transformComp.isHidden || !modelComp.isVisible) {
            sceneManager.removeScene(scene)
            entityScenes.remove(entity)
        }
    }
}
