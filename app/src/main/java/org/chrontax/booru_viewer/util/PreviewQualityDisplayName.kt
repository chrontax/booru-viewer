package org.chrontax.booru_viewer.util

import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality

fun PreviewQuality.displayName(): String = when (this) {
    PreviewQuality.LOW -> "Low"
    PreviewQuality.HIGH -> "High"
    PreviewQuality.ORIGINAL -> "Original"
    else -> error("Unknown PreviewQuality: $this")
}