package org.chrontax.booru_viewer.data.source.gelbooru

data class GelbooruPostDto(
    val id: Int,
    val score: Int,
    val width: Int,
    val height: Int,
    val rating: String,
    val tags: String,
    val file_url: String,
)
