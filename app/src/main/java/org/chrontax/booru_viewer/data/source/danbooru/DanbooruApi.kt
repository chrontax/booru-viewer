package org.chrontax.booru_viewer.data.source.danbooru

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DanbooruApi {
    @GET("posts.json")
    suspend fun getPosts(
        @Query("tags") tags: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<DanbooruPostDto>

    @GET("posts/{id}.json")
    suspend fun getPost(@Path("id") id: UInt): DanbooruPostDto

    @GET("autocomplete.json?search[type]=tag_query")
    suspend fun getTagSuggestions(@Query("search[query]") query: String): List<DanbooruTagSuggestionDto>
}