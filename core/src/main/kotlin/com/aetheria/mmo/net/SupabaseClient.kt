package com.aetheria.mmo.net

import com.aetheria.mmo.utils.GameConstants
import io.github.jan.supabase.createSupabaseClient

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = GameConstants.SUPABASE_URL,
        supabaseKey = GameConstants.SUPABASE_KEY
    ) {
        // Use KotlinX Serializer for JSON
        defaultSerializer = KotlinXSerializer(Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        })

        // INSTALL PLUGINS (Note: 'Auth' instead of 'GoTrue')
        install(Postgrest)
        install(Realtime)

    }
}