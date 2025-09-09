package org.chrontax.booru_viewer.data.source.danbooru

import android.util.Log
import org.chrontax.booru_viewer.data.model.Post
import org.chrontax.booru_viewer.data.model.Rating
import org.chrontax.booru_viewer.data.model.SuggestedTag

fun DanbooruPostDto.toPost(): Post? {
    if (is_banned || is_deleted) return null

    return Post(
        id = id.toString(),
        imageUrl = file_url ?: return null,
        width = image_width,
        height = image_height,
        tags = tag_string.split(' ').map { it.trim() }.filter { it.isNotEmpty() },
        score = score,
        rating = when (rating) {
            "g" -> Rating.SAFE
            "s", "q" -> Rating.QUESTIONABLE
            "e" -> Rating.EXPLICIT
            else -> {
                Log.e("Danbooru", "Unknown rating: $rating")
                Rating.EXPLICIT
            }
        },
        smallPreviewUrl = preview_file_url ?: return null,
        largePreviewUrl = large_file_url ?: return null
    )
}

fun List<DanbooruPostDto>.toPosts(): List<Post> = mapNotNull { it.toPost() }

fun DanbooruTagSuggestionDto.toSuggestedTag(): SuggestedTag = SuggestedTag(value, post_count)

fun List<DanbooruTagSuggestionDto>.toSuggestedTags(): List<SuggestedTag> = map { it.toSuggestedTag() }