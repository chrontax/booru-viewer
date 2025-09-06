package org.chrontax.booru_viewer.data.source

import org.chrontax.booru_viewer.data.model.Post
import org.chrontax.booru_viewer.data.model.SuggestedTag

/**
 * Interface representing a Booru source (e.g., Danbooru, Gelbooru).
 * Provides methods for searching posts, getting a specific post, and fetching tag suggestions.
 */
interface BooruSource {
    /**
     * Searches for posts matching the given tags.
     */
    suspend fun searchPosts(tags: List<String>, page: Int, limit: Int): List<Post>

    /**
     * Retrieves a specific post by its ID.
     * Returns null if the post is not found.
     */
    suspend fun getPost(id: String): Post?

    /**
     * Provides tag suggestions based on the given query.
     */
    suspend fun tagSuggestions(query: String): List<SuggestedTag>
}
