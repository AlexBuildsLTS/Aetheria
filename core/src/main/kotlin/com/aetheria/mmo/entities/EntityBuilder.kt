package com.aetheria.mmo.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.aetheria.mmo.managers.ResourceManager
import com.aetheria.mmo.components.ModelComponent
import com.aetheria.mmo.components.AnimationComponent
import com.aetheria.mmo.components.TransformComponent
import com.aetheria.mmo.components.VelocityComponent
import com.aetheria.mmo.components.StateComponent
import com.aetheria.mmo.components.PlayerComponent

object EntityBuilder {

    fun createPlayer(classType: String): Entity {
        val entity = Entity()

        // 1. Visuals
        val model = ResourceManager.getModel(classType)
        val instance = ModelInstance(model)

        val modelComp = ModelComponent()
        modelComp.modelInstance = instance // FIXED: matches ModelComponent definition
        entity.add(modelComp)

        // 2. Animation
        val animComp = AnimationComponent()
        animComp.controller = AnimationController(instance)
        animComp.controller.setAnimation("Idle", -1)
        animComp.currentAnimation = "Idle"
        entity.add(animComp)

        // 3. Logic
        val transform = TransformComponent()
        transform.position.set(0f, 2f, 0f)
        entity.add(transform)

        entity.add(VelocityComponent())
        entity.add(StateComponent())
        entity.add(PlayerComponent())

        return entity
    }
}