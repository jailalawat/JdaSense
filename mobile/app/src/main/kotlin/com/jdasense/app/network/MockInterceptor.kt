package com.jdasense.app.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val uri = chain.request().url.toUri().toString()

        if (uri.contains("/predict")) {
            // Simulate 2-second network delay
            Thread.sleep(2000)

            val randomValue = (1..10).random()
            
            // 1. Simulate Network Failure (20%)
            if (randomValue <= 2) {
                return Response.Builder()
                    .code(500)
                    .message("Internal Server Error")
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .body("".toResponseBody(null))
                    .build()
            }

            // 2. Simulate Success Results
            val (resultText, isAnomaly) = if (randomValue <= 8) {
                "Normal Heart Rhythm Detected" to false
            } else {
                "Anomaly Detected - Consult a Professional" to true
            }

            val jsonResponse = """
                {
                    "result": "$resultText",
                    "is_anomaly": $isAnomaly
                }
            """.trimIndent()

            return Response.Builder()
                .code(200)
                .message("OK")
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .body(jsonResponse.toResponseBody("application/json".toMediaTypeOrNull()))
                .addHeader("content-type", "application/json")
                .build()
        }

        return chain.proceed(chain.request())
    }
}
