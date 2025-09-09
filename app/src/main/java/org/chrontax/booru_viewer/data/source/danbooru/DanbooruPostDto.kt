package org.chrontax.booru_viewer.data.source.danbooru

data class DanbooruPostDto(
    val id: Int,
    val score: Int,
    val image_width: Int,
    val image_height: Int,
    val rating: String,
    val tag_string: String,
    val is_banned: Boolean,
    val is_deleted: Boolean,
    val file_url: String?,
    val preview_file_url: String?,
    val large_file_url: String?,
)
