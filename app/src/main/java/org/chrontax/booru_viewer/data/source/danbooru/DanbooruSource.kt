package org.chrontax.booru_viewer.data.source.danbooru

import okhttp3.OkHttpClient
import org.chrontax.booru_viewer.data.model.Post
import org.chrontax.booru_viewer.data.preferences.proto.DanbooruSettings
import org.chrontax.booru_viewer.data.source.BooruSource
import retrofit2.Retrofit

class DanbooruSource : BooruSource {
    private val api: DanbooruApi

    constructor(retrofitBuilder: Retrofit.Builder, baseUrl: String, settings: DanbooruSettings) {
        val okHttpBuilder = OkHttpClient.Builder()
        if (settings.apiKey.isNotEmpty() && settings.username.isNotEmpty()) {
            okHttpBuilder.addInterceptor(
                DanbooruAuthInterceptor(
                    settings.username,
                    settings.apiKey
                )
            )
        }
        val retrofit = retrofitBuilder
            .baseUrl(baseUrl)
            .client(okHttpBuilder.build())
            .build()

        api = retrofit.create(DanbooruApi::class.java)
    }

    override suspend fun getPost(id: String): Post? = api.getPost(id.toUInt()).toPost()

    override suspend fun searchPosts(tags: List<String>, page: Int, limit: Int): List<Post> =
        api.getPosts(tags.joinToString(" "), page + 1, limit) // Danbooru pages are 1-indexed
            .toPosts()

    override suspend fun tagSuggestions(query: String) =
        api.getTagSuggestions(query).toSuggestedTags()
}