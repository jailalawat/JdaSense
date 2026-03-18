package com.jdasense.app.network

import com.jdasense.app.security.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {
    
    private val API_KEY = "YOUR_API_KEY_HERE" // Replace with key from sam deploy output

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenManager.getToken()

        val requestBuilder = originalRequest.newBuilder()
            .header("x-api-key", API_KEY)

        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
