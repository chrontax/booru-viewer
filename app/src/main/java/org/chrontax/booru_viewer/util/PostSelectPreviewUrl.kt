package org.chrontax.booru_viewer.util

import org.chrontax.booru_viewer.data.model.Post
import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality

fun Post.selectPreviewUrl(quality: PreviewQuality): String =
    when (quality) {
        PreviewQuality.LOW -> smallPreviewUrl
        PreviewQuality.HIGH -> largePreviewUrl
        PreviewQuality.ORIGINAL -> imageUrl
        PreviewQuality.UNRECOGNIZED -> error("Unrecognized PreviewQuality: $quality")
    }