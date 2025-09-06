package org.chrontax.booru_viewer.data.source.danbooru

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class DanbooruAuthInterceptor(val username: String, val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        val credentials = Credentials.basic(username, apiKey)
        request.addHeader("Authorization", "Basic $credentials")
        return chain.proceed(request.build())
    }
}