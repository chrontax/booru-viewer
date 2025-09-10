package org.chrontax.booru_viewer.util

import org.chrontax.booru_viewer.data.preferences.proto.Tab
import kotlin.uuid.Uuid

fun newTab(
    id: String = Uuid.random().toString(),
    name: String = "New Tab",
    booruId: String,
    tags: List<String> = emptyList()
): Tab = Tab.newBuilder().setId(id).setName(name).setBooruId(booruId).addAllTags(tags).build()