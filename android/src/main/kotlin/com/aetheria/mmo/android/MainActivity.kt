
package com.aetheria.mmo.android

import com.aetheria.mmo.AetheriaGame
import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

class MainActivity : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(AetheriaGame(), AndroidApplicationConfiguration())
    }
}

