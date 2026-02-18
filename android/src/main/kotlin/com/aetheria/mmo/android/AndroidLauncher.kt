
package com.aetheria.mmo.android

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.aetheria.mmo.AetheriaGame

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(AetheriaGame(), AndroidApplicationConfiguration())
    }
}
