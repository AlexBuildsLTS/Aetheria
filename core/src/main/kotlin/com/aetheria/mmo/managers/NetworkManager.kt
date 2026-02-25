package com.aetheria.mmo.managers

import com.aetheria.mmo.components.HealthComponent
import com.aetheria.mmo.components.StaminaComponent
import com.aetheria.mmo.net.PlayerStats
import com.aetheria.mmo.net.Profile
import com.aetheria.mmo.net.SupabaseClient
import com.badlogic.ashley.core.Entity
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NetworkManager {
    
    suspend fun login(email: String, pass: String): Boolean {
        return try {
            SupabaseClient.client.auth.signInWith(Email) {
                this.email = email
                password = pass
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun fetchProfile(): Profile? {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return null
        return try {
            withContext(Dispatchers.IO) {
                SupabaseClient.client.from("profiles")
                    .select(columns = Columns.list("id", "username", "character_class", "level", "xp", "stats")) {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeSingleOrNull<Profile>()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun applyStatsToEntity(entity: Entity, stats: PlayerStats) {
        val healthComp = entity.getComponent(HealthComponent::class.java)
            ?: HealthComponent().also { entity.add(it) }
        
        val staminaComp = entity.getComponent(StaminaComponent::class.java) 
            ?: StaminaComponent().also { entity.add(it) }

        healthComp.current = stats.hp
        healthComp.max = stats.maxHp
        staminaComp.current = stats.stamina
        staminaComp.max = stats.stamina // Assuming max stamina is same as initial or need a field
        // Mana component if exists
    }
}
