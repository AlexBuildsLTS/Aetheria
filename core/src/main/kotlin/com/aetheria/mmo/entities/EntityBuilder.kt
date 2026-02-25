package com.aetheria.mmo.entities

import com.aetheria.mmo.components.*
import com.aetheria.mmo.managers.ResourceManager
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.ModelInstance

object EntityBuilder {

    fun spawnPlayer(engine: PooledEngine, archetypeId: String): Entity {
        val entity = engine.createEntity()
        
        val classMap = mapOf(
            "Vanguard" to ResourceManager.CHAR_VANGUARD,
            "Medic" to ResourceManager.CHAR_MEDIC,
            "Strider" to ResourceManager.CHAR_STRIDER,
            "Weaver" to ResourceManager.CHAR_WEAVER
        )
        
        val modelPath = classMap[archetypeId] ?: ResourceManager.CHAR_VANGUARD
        
        var modelInstance: ModelInstance? = null
        try {
            val sceneAsset = ResourceManager.getSceneAsset(modelPath)
            if (sceneAsset != null) {
                modelInstance = ModelInstance(sceneAsset.scene.model)
            }
        } catch (e: Exception) {
            // Logged in ResourceManager
        }

        if (modelInstance == null) {
            val fallback = ResourceManager.createPlaceholderModel(Color.CYAN, 1f, 2f, "capsule")
            modelInstance = ModelInstance(fallback)
        }

        entity.add(TransformComponent().apply { position.set(0f, 0f, 0f) })
        entity.add(VelocityComponent())
        entity.add(MoveEvtComponent().apply { moveSpeed = 10f })
        entity.add(InputComponent())
        entity.add(PlayerComponent())
        
        // SUPABASE FALLBACK / DEFAULT STATS PER DIRECTIVE
        // {"hp": 1000, "mana": 500, "stamina": 100}
        entity.add(HealthComponent().apply { 
            max = 1000f
            current = 1000f 
        })
        entity.add(StaminaComponent().apply { 
            max = 100f
            current = 100f 
        })
        
        entity.add(CombatComponent())
        entity.add(CameraFollowComponent())
        entity.add(ModelComponent().apply { this.modelInstance = modelInstance })
        
        engine.addEntity(entity)
        return entity
    }
}
