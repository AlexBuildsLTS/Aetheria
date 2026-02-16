package com.aetheria.mmo.managers

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.utils.Disposable

object ResourceManager : Disposable {
    val assets = AssetManager()

    // EXACT paths matching your directory structure
    const val MODEL_STRIDER = "models/characters/char_strider_base.glb"
    const val MODEL_VANGUARD = "models/characters/char_vanguard_base.glb"
    const val MODEL_WEAVER = "models/characters/char_weaver_base.glb"
    const val MODEL_MEDIC = "models/characters/char_medic_base.glb"

    fun loadAll() {
        // Load all character base models
        assets.load(MODEL_STRIDER, Model::class.java)
        assets.load(MODEL_VANGUARD, Model::class.java)
        assets.load(MODEL_WEAVER, Model::class.java)
        assets.load(MODEL_MEDIC, Model::class.java)
    }

    fun getModel(path: String): Model {
        if (!assets.isLoaded(path)) {
            throw RuntimeException("Asset not loaded: $path")
        }
        return assets.get(path, Model::class.java)
    }

    override fun dispose() {
        assets.dispose()
    }
}