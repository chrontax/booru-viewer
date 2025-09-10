package org.chrontax.booru_viewer.util

import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.DanbooruSettings
import kotlin.uuid.Uuid

fun newDefaultDanbooruSite(
    id: String = Uuid.random().toString(),
    name: String = "Danbooru",
    url: String = "https://danbooru.donmai.us"
): BooruSite = BooruSite.newBuilder().setId(id).setName(name).setUrl(url)
    .setDanbooruSettings(DanbooruSettings.getDefaultInstance()).build()
