package org.chrontax.booru_viewer.data.source.gelbooru

import retrofit2.http.GET
import retrofit2.http.Query

interface GelbooruApi {
    @GET("index.php?page=dapi&s=post&q=index&json=1")
    suspend fun getPosts(
        @Query("tags") tags: String,
        @Query("pid") page: Int,
        @Query("limit") limit: Int
    ): GelbooruPostsDto

    @GET("index.php?page=dapi&s=post&q=index&json=1")
    suspend fun getPost(@Query("id") id: UInt): GelbooruPostsDto

    @GET("index.php?page=autocomplete2&type=tag_query")
    suspend fun getTagSuggestions(@Query("term") query: String): List<GelbooruTagSuggestionDto>
}