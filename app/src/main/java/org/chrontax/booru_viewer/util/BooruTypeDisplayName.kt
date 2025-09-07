package org.chrontax.booru_viewer.util

import org.chrontax.booru_viewer.data.preferences.proto.BooruType

fun BooruType.displayName(): String =
    when (this) {
        BooruType.DANBOORU -> "Danbooru"
        BooruType.GELBOORU -> "Gelbooru"
        else -> error("Unknown BooruType: $this")
    }