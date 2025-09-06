package org.chrontax.booru_viewer.data.source.gelbooru

import okhttp3.Interceptor

class GelbooruAuthInterceptor(val userId: String, val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()

        val newUrl = chain.request().url.newBuilder().apply {
            addQueryParameter("api_key", apiKey)
            addQueryParameter("user_id", userId)
        }.build()

        return chain.proceed(request.url(newUrl).build())
    }
}