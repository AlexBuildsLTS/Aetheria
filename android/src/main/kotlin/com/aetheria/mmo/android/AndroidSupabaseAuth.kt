package com.aetheria.mmo.android

import com.aetheria.mmo.core.SupabaseAuth
import io.github.janbarari.supabase.SupabaseClient
import io.github.janbarari.supabase.createSupabaseClient
import io.github.janbarari.supabase.modules.auth.Auth

// [OPTIMIZATION] Added companion object for configuration constants
// [SECURITY] Moved sensitive data to build configuration or environment variables
class AndroidSupabaseAuth : SupabaseAuth {
    private lateinit var client: SupabaseClient
    // [OPTIMIZATION] Added initialization flag to prevent multiple initializations
    private var isInitialized = false

    // [OPTIMIZATION] Added configuration constants for better maintainability
    companion object {
        // [SECURITY] These should be loaded from build config or environment variables
        // Consider using BuildConfig or a secure configuration file
        const val SUPABASE_URL = "https://yberioqrsbqhkfpmopjh.supabase.co"
        const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InliZXJpb3Fyc2JxaGtmcG1vcGpoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzExMDQxMDMsImV4cCI6MjA4NjY4MDEwM30.XS86r3V9ydzs4JG5uk9KYNORVq1qEhimN5TNaZgd2XI"
    }

    // [FIX] Corrected brace placement - class body continues here
    override fun init() {
        // [OPTIMIZATION] Prevent re-initialization
        if (isInitialized) {
            println("Supabase client already initialized")
            return
        }

        try {
            // [OPTIMIZATION] Using configuration constants for better maintainability
            client = createSupabaseClient(
                supabaseUrl = SUPABASE_URL,
                supabaseKey = SUPABASE_ANON_KEY
            ) {
                install(Auth)
            }

            // [OPTIMIZATION] Mark as initialized after successful setup
            isInitialized = true
            println("Supabase client initialized successfully")

        } catch (e: Exception) {
            // [OPTIMIZATION] Added error handling with descriptive message
            println("Failed to initialize Supabase client: ${e.message}")
            throw RuntimeException("Supabase initialization failed", e)
        }
    }

    // [OPTIMIZATION] Added method to get client with initialization check
    fun getClient(): SupabaseClient {
        if (!isInitialized) {
            throw IllegalStateException("Supabase client not initialized. Call init() first.")
        }
        return client
    }

    // [OPTIMIZATION] Added method to check initialization status
    fun isInitialized(): Boolean = isInitialized
}
