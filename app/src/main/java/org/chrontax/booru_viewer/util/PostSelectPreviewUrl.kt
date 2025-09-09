package org.chrontax.booru_viewer.util

import org.chrontax.booru_viewer.data.model.Post
import org.chrontax.booru_viewer.data.model.PostType
import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality

fun Post.selectPreviewUrl(quality: PreviewQuality): String =
    if (type == PostType.GIF) smallPreviewUrl else // I've never encountered a gif with a non-gif large preview
        when (quality) {
            PreviewQuality.LOW -> smallPreviewUrl
            PreviewQuality.HIGH -> largePreviewUrl
            PreviewQuality.ORIGINAL -> imageUrl
            PreviewQuality.UNRECOGNIZED -> error("Unrecognized PreviewQuality: $quality")
        }