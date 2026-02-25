package com.aetheria.mmo.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.utils.Disposable
import net.mgsx.gltf.loaders.glb.GLBAssetLoader
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader
import net.mgsx.gltf.scene3d.scene.SceneAsset

/**
 * AAA Resource Manager
 * Enforces strict model loading and robust fallbacks for missing assets.
 */
object ResourceManager : Disposable {
    val assetManager = AssetManager()
    private val modelBuilder = ModelBuilder()
    private var missingTexture: Texture? = null

    val CHAR_VANGUARD = "models/characters/char_vanguard_base.glb"
    val CHAR_MEDIC = "models/characters/char_medic_base.glb"
    val CHAR_STRIDER = "models/characters/char_strider_base.glb"
    val CHAR_WEAVER = "models/characters/char_weaver_base.glb"

    private val VALID_MODELS = setOf(
        CHAR_VANGUARD,
        CHAR_MEDIC,
        CHAR_STRIDER,
        CHAR_WEAVER
    )

    init {
        val resolver = InternalFileHandleResolver()
        assetManager.setLoader(SceneAsset::class.java, ".gltf", GLTFAssetLoader(resolver))
        assetManager.setLoader(SceneAsset::class.java, ".glb", GLBAssetLoader(resolver))
        
        createMissingTexture()
    }

    private fun createMissingTexture() {
        val pixmap = Pixmap(32, 32, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.MAGENTA)
        pixmap.fill()
        pixmap.setColor(Color.BLACK)
        pixmap.fillRectangle(0, 0, 16, 16)
        pixmap.fillRectangle(16, 16, 16, 16)
        missingTexture = Texture(pixmap)
        pixmap.dispose()
    }

    fun loadAll() {
        VALID_MODELS.forEach { path ->
            try {
                // Ensure directory exists for safety
                if (Gdx.files.internal(path).exists()) {
                    assetManager.load(path, SceneAsset::class.java)
                }
            } catch (e: Exception) {
                Gdx.app.error("ResourceManager", "Failed to load $path")
            }
        }
    }

    fun getSceneAsset(path: String): SceneAsset? {
        if (!VALID_MODELS.contains(path)) return null
        if (assetManager.isLoaded(path)) {
            return assetManager.get(path, SceneAsset::class.java)
        }
        return null
    }

    fun createPlaceholderModel(color: Color? = null, width: Float = 1f, height: Float = 2f, type: String = "box"): Model {
        val material = if (color != null) {
            Material(ColorAttribute.createDiffuse(color))
        } else {
            Material(TextureAttribute.createDiffuse(missingTexture))
        }
        
        val attributes = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()
        
        return when(type) {
            "box" -> modelBuilder.createBox(width, height, width, material, attributes)
            "sphere" -> modelBuilder.createSphere(width, width, width, 16, 16, material, attributes)
            else -> modelBuilder.createCapsule(width, height, 16, material, attributes)
        }
    }

    fun update(): Boolean = assetManager.update()
    fun getProgress(): Float = assetManager.progress
    fun finishLoading() = assetManager.finishLoading()

    override fun dispose() {
        assetManager.dispose()
        missingTexture?.dispose()
    }
}
