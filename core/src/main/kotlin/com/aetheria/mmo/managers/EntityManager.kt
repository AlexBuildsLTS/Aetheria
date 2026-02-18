package com.aetheria.mmo.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable

/**
 * Entity Manager
 * Manages entity lifecycle and pooling
 * Provides factory methods for creating common entities
 */
object EntityManager : Disposable {

    private lateinit var engine: Engine
    private val entityPools = mutableMapOf<String, MutableList<Entity>>()
    private val activeEntities = mutableMapOf<String, Entity>()

    // Entity counters
    private var playerCount = 0
    private var enemyCount = 0
    private var projectileCount = 0
    private var itemCount = 0

    /**
     * Initializes the entity manager
     */
    fun initialize(ecsEngine: Engine) {
        engine = ecsEngine
        Gdx.app.log("EntityManager", "Entity manager initialized")
    }

    /**
     * Creates a new entity
     */
    fun createEntity(type: String): Entity {
        val entity = engine.createEntity()
        activeEntities[generateEntityId(type)] = entity
        incrementCounter(type)
        return entity
    }

    /**
     * Removes an entity
     */
    fun removeEntity(entityId: String) {
        val entity = activeEntities[entityId]
        if (entity != null) {
            engine.removeEntity(entity)
            activeEntities.remove(entityId)
            decrementCounter(getEntityType(entityId))
        }
    }

    /**
     * Gets an entity by ID
     */
    fun getEntity(entityId: String): Entity? {
        return activeEntities[entityId]
    }

    /**
     * Gets all entities of a type
     */
    fun getEntitiesByType(type: String): List<Entity> {
        return activeEntities.filter { it.key.startsWith(type) }.values.toList()
    }

    /**
     * Gets all active entities
     */
    fun getAllEntities(): List<Entity> {
        return activeEntities.values.toList()
    }

    /**
     * Pools an entity for reuse
     */
    fun poolEntity(type: String, entity: Entity) {
        engine.removeEntity(entity)

        if (!entityPools.containsKey(type)) {
            entityPools[type] = mutableListOf()
        }

        entityPools[type]?.add(entity)
    }

    /**
     * Gets a pooled entity or creates a new one
     */
    fun getPooledEntity(type: String): Entity {
        val pool = entityPools[type]
        return if (pool != null && pool.isNotEmpty()) {
            pool.removeAt(pool.size - 1)
        } else {
            createEntity(type)
        }
    }

    /**
     * Clears all pooled entities
     */
    fun clearPools() {
        entityPools.clear()
    }

    /**
     * Removes all entities
     */
    fun removeAllEntities() {
        activeEntities.values.forEach { engine.removeEntity(it) }
        activeEntities.clear()
        resetCounters()
    }

    /**
     * Generates a unique entity ID
     */
    private fun generateEntityId(type: String): String {
        val count = when (type) {
            "player" -> playerCount
            "enemy" -> enemyCount
            "projectile" -> projectileCount
            "item" -> itemCount
            else -> activeEntities.size
        }
        return "${type}_${count}_${System.currentTimeMillis()}"
    }

    /**
     * Gets entity type from ID
     */
    private fun getEntityType(entityId: String): String {
        return entityId.substringBefore("_")
    }

    /**
     * Increments entity counter
     */
    private fun incrementCounter(type: String) {
        when (type) {
            "player" -> playerCount++
            "enemy" -> enemyCount++
            "projectile" -> projectileCount++
            "item" -> itemCount++
        }
    }

    /**
     * Decrements entity counter
     */
    private fun decrementCounter(type: String) {
        when (type) {
            "player" -> playerCount--
            "enemy" -> enemyCount--
            "projectile" -> projectileCount--
            "item" -> itemCount--
        }
    }

    /**
     * Resets all counters
     */
    private fun resetCounters() {
        playerCount = 0
        enemyCount = 0
        projectileCount = 0
        itemCount = 0
    }

    /**
     * Gets entity statistics
     */
    fun getStats(): EntityStats {
        return EntityStats(
            totalEntities = activeEntities.size,
            playerCount = playerCount,
            enemyCount = enemyCount,
            projectileCount = projectileCount,
            itemCount = itemCount,
            pooledEntities = entityPools.values.sumOf { it.size }
        )
    }

    override fun dispose() {
        removeAllEntities()
        clearPools()
        Gdx.app.log("EntityManager", "Entity manager disposed")
    }

    data class EntityStats(
        val totalEntities: Int,
        val playerCount: Int,
        val enemyCount: Int,
        val projectileCount: Int,
        val itemCount: Int,
        val pooledEntities: Int
    )
}
