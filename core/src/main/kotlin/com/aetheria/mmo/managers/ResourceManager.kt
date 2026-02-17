package com.aetheria.mmo.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.utils.Disposable

/**
 * Manages the loading and disposal of game assets.
 * Includes error checking to prevent crashes if files are missing.
 */
object ResourceManager : Disposable {
    val assets = AssetManager()

    // List of model files we expect to find in assets/models/characters/
    private val characterModels = listOf(
        "models/characters/char_vanguard_base.glb",
        "models/characters/char_weaver_base.glb",
        "models/characters/char_strider_base.glb",
        "models/characters/char_medic_base.glb"
    )

    fun loadAll() {
        println("ResourceManager: Starting asset load...")

        for (fileName in characterModels) {
            // CHECK: Does the file actually exist at this path?
            if (Gdx.files.internal(fileName).exists()) {
                assets.load(fileName, Model::class.java)
                println("ResourceManager: Queued $fileName")
            } else {
                // This prints a clear error instead of crashing silently
                println("ResourceManager ERROR: File not found: $fileName")
                println("   -> Check your 'assets' folder location.")
            }
        }

        try {
            // Attempt to load all queued assets
            assets.finishLoading()
            println("ResourceManager: Loading complete.")
        } catch (e: Exception) {
            // If it crashes here, print the real reason
            println("ResourceManager CRASH: ${e.message}")
            e.printStackTrace()
        }
    }

    fun getModel(name: String): Model {
        val path = "models/characters/$name"

        // Safety check: Is this specific model actually loaded?
        if (assets.isLoaded(path, Model::class.java)) {
            return assets.get(path, Model::class.java)
        } else {
            // Return a dummy model or throw a clear error to help debug
            throw RuntimeException("ResourceManager: Requesting unloaded model: $path. Did loadAll() find it?")
        }
    }

    override fun dispose() {
        assets.dispose()
    }
}