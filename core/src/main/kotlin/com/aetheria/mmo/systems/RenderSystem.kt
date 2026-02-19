package com.aetheria.mmo.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.aetheria.mmo.components.ModelComponent
import com.aetheria.mmo.components.TransformComponent
import com.aetheria.mmo.components.AnimationComponent
import net.mgsx.gltf.scene3d.scene.SceneManager
import net.mgsx.gltf.scene3d.scene.Scene

/**
 * AAA+ Tier Render System using gdx-gltf SceneManager.
 * Provides PBR lighting, shadows, and efficient rendering.
 */
class RenderSystem(
    val sceneManager: SceneManager,
    val camera: PerspectiveCamera
) : IteratingSystem(
    Family.all(ModelComponent::class.java, TransformComponent::class.java).get()
) {
    private val entityScenes = mutableMapOf<Entity, Scene>()
    private val entityLights = mutableMapOf<Entity, net.mgsx.gltf.scene3d.lights.PointLightEx>()

    init {
        // Setup PBR lighting
        val environment = sceneManager.environment
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.45f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
        
        sceneManager.setCamera(camera)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        sceneManager.update(deltaTime)
        sceneManager.render()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val modelComp = entity.getComponent(ModelComponent::class.java)
        val transformComp = entity.getComponent(TransformComponent::class.java)
        val animComp = entity.getComponent(AnimationComponent::class.java)
        val lightComp = entity.getComponent(com.aetheria.mmo.components.LightComponent::class.java)

        // Get or create Scene for this entity
        var scene = entityScenes[entity]
        if (scene == null) {
            scene = Scene(modelComp.modelInstance)
            sceneManager.addScene(scene)
            entityScenes[entity] = scene
        }

        // Sync transform
        scene.modelInstance.transform.set(
            transformComp.position,
            transformComp.rotation,
            transformComp.scale
        )
        
        // Link animation controller if it exists
        if (animComp != null && animComp.controller == null) {
            animComp.controller = scene.animationController
        }

        // Handle Dynamic Lighting
        if (lightComp != null) {
            var pointLight = entityLights[entity]
            if (pointLight == null) {
                pointLight = net.mgsx.gltf.scene3d.lights.PointLightEx()
                sceneManager.environment.add(pointLight)
                entityLights[entity] = pointLight
            }
            pointLight.color.set(lightComp.color)
            pointLight.intensity = lightComp.intensity
            pointLight.range = lightComp.radius
            pointLight.position.set(transformComp.position)
        }
        
        // Visibility
        if (transformComp.isHidden || !modelComp.isVisible) {
            sceneManager.removeScene(scene)
            entityScenes.remove(entity)
            
            entityLights[entity]?.let {
                sceneManager.environment.remove(it)
                entityLights.remove(entity)
            }
        }
    }
}
