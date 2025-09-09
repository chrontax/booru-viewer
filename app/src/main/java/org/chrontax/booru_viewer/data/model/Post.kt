package org.chrontax.booru_viewer.data.model


data class Post(
    val id: String,
    val imageUrl: String,
    val width: Int,
    val height: Int,
    val tags: List<String>,
    val score: Int,
    val rating: Rating,
    val smallPreviewUrl: String,
    val largePreviewUrl: String
)
