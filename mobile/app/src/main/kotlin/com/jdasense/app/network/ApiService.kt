package com.jdasense.app.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class PredictionResponse(
    val result: String,
    val is_anomaly: Boolean
)

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

data class User(
    val email: String,
    val role: String,
    val name: String,
    val hospital_id: String,
    val is_deleted: Boolean
)

data class UserCreateRequest(
    val email: String,
    val role: String,
    val name: String,
    val password: String,
    val hospital_id: String? = null
)

data class AuditLog(
    val log_id: String,
    val actor_email: String,
    val action: String,
    val target_id: String,
    val timestamp: String
)

interface ApiService {
    @Multipart
    @POST("/predict")
    suspend fun predict(
        @Part audio: MultipartBody.Part
    ): Response<PredictionResponse>

    @GET("/users")
    suspend fun listUsers(): Response<List<User>>

    @POST("/users")
    suspend fun createUser(@Body request: UserCreateRequest): Response<User>

    @DELETE("/users/{email}")
    suspend fun deleteUser(@Path("email") email: String): Response<Unit>

    @GET("/audit")
    suspend fun getAuditLogs(): Response<List<AuditLog>>
}
