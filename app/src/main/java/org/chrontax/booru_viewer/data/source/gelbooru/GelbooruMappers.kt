package org.chrontax.booru_viewer.data.source.gelbooru

import android.util.Log
import org.chrontax.booru_viewer.data.model.Post
import org.chrontax.booru_viewer.data.model.Rating
import org.chrontax.booru_viewer.data.model.SuggestedTag

fun GelbooruPostDto.toPost(): Post = Post(
    id = id.toString(),
    score = score,
    width = width,
    height = height,
    tags = tags.split(" "),
    imageUrl = file_url,
    rating = when (rating) {
        "explicit" -> Rating.EXPLICIT
        "safe", "general" -> Rating.SAFE
        "questionable" -> Rating.QUESTIONABLE
        else -> {
            Log.e("Gelbooru", "Unknown rating: $rating")
            Rating.EXPLICIT
        }
    },
    smallPreviewUrl = preview_url,
    largePreviewUrl = sample_url.ifEmpty { file_url }
)

fun GelbooruPostsDto.toPosts(): List<Post> = post?.map { it.toPost() } ?: emptyList()

fun GelbooruPostsDto.toPost(): Post? = post?.firstOrNull()?.toPost()

fun GelbooruTagSuggestionDto.toSuggestedTag(): SuggestedTag = SuggestedTag(value, post_count)

fun List<GelbooruTagSuggestionDto>.toSuggestedTags(): List<SuggestedTag> = map { it.toSuggestedTag() }