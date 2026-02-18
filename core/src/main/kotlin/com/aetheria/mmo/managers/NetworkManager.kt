package com.aetheria.mmo.managers

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


/**
 * Network Manager
 * Handles all network operations including authentication, data synchronization, and API calls
 * Optimized with coroutines for asynchronous operations and proper error handling
 */
class NetworkManager(
    private val baseUrl: String = "https://api.aetheria.com/v1",
    private val timeoutSeconds: Long = 30,
) {
    // HTTP client
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = timeoutSeconds * 1000
            connectTimeoutMillis = timeoutSeconds * 1000
            socketTimeoutMillis = timeoutSeconds * 1000
        }
    }

    // Authentication state
    private var authToken: String? = null
    private var refreshToken: String? = null

    // Coroutine scope for network operations
    private val networkScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Data classes for API requests/responses
     */
    @Serializable
    data class LoginRequest(
        val email: String,
        val password: String
    )

    @Serializable
    data class LoginResponse(
        val success: Boolean,
        val token: String? = null,
        val refreshToken: String? = null,
        val message: String? = null,
        val data: UserData? = null
    )

    @Serializable
    data class UserData(
        val id: String,
        val email: String,
        val username: String,
        val createdAt: String,
        val lastLogin: String? = null
    )

    @Serializable
    data class ApiResponse<T>(
        val success: Boolean,
        val message: String? = null,
        val data: T? = null
    )

    /**
     * Authenticate user with email and password
     * @param email User's email address
     * @param password User's password
     * @return LoginResponse with authentication result
     */
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = post<LoginResponse>("auth/login", request)

            if (response.isSuccess) {
                val loginData = response.getOrThrow()
                if (loginData.success) {
                    authToken = loginData.token
                    refreshToken = loginData.refreshToken
                }
            }

            response
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Register new user
     * @param email User's email address
     * @param password User's password
     * @param username Desired username
     * @return ApiResponse with registration result
     */
    suspend fun register(email: String, password: String, username: String): Result<ApiResponse<UserData>> {
        return try {
            val requestData = mapOf(
                "email" to email,
                "password" to password,
                "username" to username
            )
            post<ApiResponse<UserData>>("auth/register", requestData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generic POST request method
     * @param endpoint API endpoint
     * @param data Request data
     * @return Result with parsed response
     */
    private suspend inline fun <reified T> post(endpoint: String, data: Any): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = client.post("$baseUrl/$endpoint") {
                    contentType(ContentType.Application.Json)
                    setBody(data)
                    header("Accept", "application/json")
                    authToken?.let {
                        header("Authorization", "Bearer $it")
                    }
                }
                Result.success(response.body<T>())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Generic GET request method
     * @param endpoint API endpoint
     * @param queryParams Optional query parameters
     * @return Result with parsed response
     */
    private suspend inline fun <reified T> get(
        endpoint: String,
        queryParams: Map<String, String> = emptyMap()
    ): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = client.get("$baseUrl/$endpoint") {
                    queryParams.forEach { (key, value) ->
                        parameter(key, value)
                    }
                    header("Accept", "application/json")
                    authToken?.let {
                        header("Authorization", "Bearer $it")
                    }
                }
                Result.success(response.body<T>())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Refresh authentication token
     * @return Boolean indicating success
     */
    suspend fun refreshToken(): Boolean {
        return try {
            val refreshToken = this.refreshToken ?: return false

            val requestData = mapOf("refreshToken" to refreshToken)
            val response = post<ApiResponse<Map<String, String>>>("auth/refresh", requestData)
                .getOrThrow()

            if (response.success) {
                authToken = response.data?.get("token")
                this.refreshToken = response.data?.get("refreshToken")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Logout current user
     */
    fun logout() {
        authToken = null
        refreshToken = null
    }

    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return authToken != null
    }

    /**
     * Get current authentication token
     */
    fun getAuthToken(): String? {
        return authToken
    }

    /**
     * Clean up resources
     */
    fun dispose() {
        networkScope.cancel()
        client.close()
    }
}
