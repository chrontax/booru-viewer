package org.chrontax.booru_viewer.data.source

import org.chrontax.booru_viewer.data.preferences.proto.BooruSite

/**
 * Factory interface for creating Booru data sources.
 */
interface BooruSourceFactory {
    /**
     * Creates a [BooruSource] for the given [booruSite].
     *
     * @param booruSite The Booru site configuration.
     * @return A [BooruSource] instance for the specified site.
     */
    fun create(booruSite: BooruSite): BooruSource
}