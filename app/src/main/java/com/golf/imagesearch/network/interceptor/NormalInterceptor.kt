package com.golf.imagesearch.network.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class NormalInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest: Request.Builder = chain.request().newBuilder()
        newRequest.addHeader("accept", "application/json")
        newRequest.addHeader(
            "Authorization", "lWwMXgI4pH9uhwFyGyCmpERCgtmRIzofBUZxsZmmWiuhOLWu5n8Ua0Ff"
        )

        newRequest.addHeader("content-type", "application/json")
        return chain.proceed(newRequest.build())
    }
}