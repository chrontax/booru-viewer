package org.chrontax.booru_viewer.data.source.gelbooru

import okhttp3.OkHttpClient
import org.chrontax.booru_viewer.data.model.Post
import org.chrontax.booru_viewer.data.model.SuggestedTag
import org.chrontax.booru_viewer.data.preferences.proto.GelbooruSettings
import org.chrontax.booru_viewer.data.source.BooruSource
import retrofit2.Retrofit

class GelbooruSource : BooruSource {
    private val api: GelbooruApi

    constructor(retrofitBuilder: Retrofit.Builder, baseUrl: String, settings: GelbooruSettings) {
        val okHttpBuilder = OkHttpClient.Builder()
        if (settings.apiKey.isNotEmpty() && settings.userId.isNotEmpty()) {
            okHttpBuilder.addInterceptor(
                GelbooruAuthInterceptor(
                    settings.userId,
                    settings.apiKey
                )
            )
        }
        val retrofit = retrofitBuilder
            .baseUrl(baseUrl)
            .client(okHttpBuilder.build())
            .build()

        api = retrofit.create(GelbooruApi::class.java)
    }

    override suspend fun getPost(id: String): Post? = api.getPost(id.toUInt()).toPost()

    override suspend fun searchPosts(tags: List<String>, page: Int, limit: Int): List<Post> =
        api.getPosts(tags.joinToString(" "), page, limit)
            .toPosts()

    override suspend fun tagSuggestions(query: String): List<SuggestedTag> =
        api.getTagSuggestions(query).toSuggestedTags()
}