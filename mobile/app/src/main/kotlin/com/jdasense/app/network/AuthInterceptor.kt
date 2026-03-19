package com.jdasense.app.network

import com.jdasense.app.security.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {

    private val API_KEY = "HxuBPWNWqxaZO6MPmwGYB93UnIqhk0W34DWlJcWY"

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenManager.getToken()

        val requestBuilder = originalRequest.newBuilder()
            .header("x-api-key", API_KEY)

        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        Log.d("AuthInterceptor", "Sending request to ${request.url}")

        val response = chain.proceed(request)
        Log.d("AuthInterceptor", "Received response with code ${response.code} from ${request.url}")

        return response
    }
}
