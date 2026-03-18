package com.jdasense.app.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class PredictionResponse(
    val result: String,
    val is_anomaly: Boolean
)

interface ApiService {
    @Multipart
    @POST("/predict")
    suspend fun predict(
        @Part audio: MultipartBody.Part
    ): Response<PredictionResponse>
}
