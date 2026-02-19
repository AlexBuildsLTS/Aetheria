package com.aetheria.mmo.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.utils.Disposable
import net.mgsx.gltf.loaders.glb.GLBAssetLoader
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader
import net.mgsx.gltf.scene3d.scene.SceneAsset

/**
 * AAA+ Tier Resource Manager
 * Handles loading and caching of all game assets using gdx-gltf for PBR 3D models.
 */
object ResourceManager : Disposable {
    val assetManager = AssetManager()

    // Character model paths
    private val characterModels = listOf(
        "models/characters/char_vanguard_base.glb",
        "models/characters/char_weaver_base.glb",
        "models/characters/char_strider_base.glb",
        "models/characters/char_medic_base.glb"
    )

    // Environment model paths
    private val environmentModels = listOf(
        "models/environment/env_ground_tile_hex.glb",
        "models/environment/env_building_ruined.glb",
        "models/environment/env_data_fall.glb",
        "models/environment/env_firewall.glb",
        "models/environment/env_rock_floating.glb",
        "models/environment/env_tree_neon.glb"
    )

    // Enemy model paths
    private val enemyModels = listOf(
        "models/enemies/boss_leviathan.glb",
        "models/enemies/boss_null_pointer.glb",
        "models/enemies/mob_boar_armored.glb",
        "models/enemies/mob_neon_stalker.glb",
        "models/enemies/mob_rat_robot.glb",
        "models/enemies/mob_spider_phase.glb"
    )

    init {
        // Register GLTF/GLB loaders for 3D model support
        val resolver = InternalFileHandleResolver()
        assetManager.setLoader(SceneAsset::class.java, ".gltf", GLTFAssetLoader(resolver))
        assetManager.setLoader(SceneAsset::class.java, ".glb", GLBAssetLoader(resolver))

        Gdx.app.log("ResourceManager", "GLTF loaders registered successfully")
    }

    /**
     * Loads all game assets asynchronously.
     */
    fun loadAll() {
        Gdx.app.log("ResourceManager", "Starting asset loading...")

        // Load characters
        for (modelPath in characterModels) {
            if (Gdx.files.internal(modelPath).exists()) {
                assetManager.load(modelPath, SceneAsset::class.java)
                Gdx.app.log("ResourceManager", "Queued: $modelPath")
            }
        }

        // Load environment
        for (modelPath in environmentModels) {
            if (Gdx.files.internal(modelPath).exists()) {
                assetManager.load(modelPath, SceneAsset::class.java)
                Gdx.app.log("ResourceManager", "Queued: $modelPath")
            }
        }

        // Load enemies
        for (modelPath in enemyModels) {
            if (Gdx.files.internal(modelPath).exists()) {
                assetManager.load(modelPath, SceneAsset::class.java)
                Gdx.app.log("ResourceManager", "Queued: $modelPath")
            }
        }
    }

    fun update(): Boolean = assetManager.update()

    fun getProgress(): Float = assetManager.progress

    fun finishLoading() = assetManager.finishLoading()

    /**
     * Retrieves a loaded SceneAsset by filename.
     */
    fun getSceneAsset(name: String): SceneAsset {
        val path = when {
            name.startsWith("char_") -> "models/characters/$name"
            name.startsWith("mob_") || name.startsWith("boss_") -> "models/enemies/$name"
            else -> "models/environment/$name"
        }
        if (!assetManager.isLoaded(path, SceneAsset::class.java)) {
            Gdx.app.log("ResourceManager", "Warning: Asset $path not preloaded. Loading synchronously.")
            assetManager.load(path, SceneAsset::class.java)
            assetManager.finishLoading()
        }
        return assetManager.get(path, SceneAsset::class.java)
    }

    override fun dispose() {
        assetManager.dispose()
        Gdx.app.log("ResourceManager", "Resources disposed")
    }
}
