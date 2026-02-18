package com.aetheria.mmo.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.utils.Disposable
import net.mgsx.gltf.loaders.glb.GLBAssetLoader
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader
import net.mgsx.gltf.scene3d.scene.SceneAsset

/**
 * AAA+ Tier Resource Manager
 * Handles loading and caching of all game assets using gdx-gltf for 3D models.
 */
object ResourceManager : Disposable {
    private val assetManager = AssetManager()

    // Character model paths
    private val characterModels = listOf(
        "models/characters/char_vanguard_base.glb",
        "models/characters/char_weaver_base.glb",
        "models/characters/char_strider_base.glb",
        "models/characters/char_medic_base.glb"
    )

    init {
        // Register GLTF/GLB loaders for 3D model support
        assetManager.setLoader(SceneAsset::class.java, ".gltf", GLTFAssetLoader())
        assetManager.setLoader(SceneAsset::class.java, ".glb", GLBAssetLoader())

        Gdx.app.log("ResourceManager", "GLTF loaders registered successfully")
    }

    /**
     * Loads all game assets asynchronously.
     * Call update() in a loop until it returns true.
     */
    fun loadAll() {
        Gdx.app.log("ResourceManager", "Starting asset loading...")

        // Queue all character models for loading
        for (modelPath in characterModels) {
            if (Gdx.files.internal(modelPath).exists()) {
                assetManager.load(modelPath, SceneAsset::class.java)
                Gdx.app.log("ResourceManager", "Queued: $modelPath")
            } else {
                Gdx.app.error("ResourceManager", "File not found: $modelPath")
            }
        }
    }

    /**
     * Updates the asset manager. Returns true when all assets are loaded.
     */
    fun update(): Boolean {
        return assetManager.update()
    }

    /**
     * Gets the loading progress (0.0 to 1.0)
     */
    fun getProgress(): Float {
        return assetManager.progress
    }

    /**
     * Blocks until all assets are loaded. Use for synchronous loading.
     */
    fun finishLoading() {
        assetManager.finishLoading()
        Gdx.app.log("ResourceManager", "All assets loaded successfully!")
    }

    /**
     * Retrieves a loaded 3D model by filename.
     * @param name The filename (e.g., "char_vanguard_base.glb")
     * @return The Model extracted from the SceneAsset
     */
    fun getModel(name: String): Model {
        val path = "models/characters/$name"

        if (!assetManager.isLoaded(path, SceneAsset::class.java)) {
            throw RuntimeException("Model not loaded: $path. Call loadAll() and wait for completion.")
        }

        val sceneAsset = assetManager.get(path, SceneAsset::class.java)
        return sceneAsset.scene.model
    }

    /**
     * Gets the full SceneAsset (includes model, animations, materials)
     */
    fun getSceneAsset(name: String): SceneAsset {
        val path = "models/characters/$name"

        if (!assetManager.isLoaded(path, SceneAsset::class.java)) {
            throw RuntimeException("SceneAsset not loaded: $path")
        }

        return assetManager.get(path, SceneAsset::class.java)
    }

    override fun dispose() {
        assetManager.dispose()
        Gdx.app.log("ResourceManager", "Resources disposed")
    }
}