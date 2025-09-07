package org.chrontax.booru_viewer.data.preferences

import kotlinx.coroutines.flow.Flow
import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.Preferences

/**
 * Repository to manage user preferences.
 * Provides a Flow of [Preferences] and methods to modify them.
 */
interface PreferencesRepository {
    val preferencesFlow: Flow<Preferences>

    /**
     * @throws IllegalArgumentException if the site URL is invalid or ID already exists.
     */
    suspend fun addBooruSite(booruSite: BooruSite)

    /**
     * @throws NoSuchElementException if no site with the given ID exists.
     */
    suspend fun removeBooruSite(id: String)

    /**
     * @throws NoSuchElementException if no site with the given ID exists.
     * @throws IllegalArgumentException if the site URL is invalid.
     */
    suspend fun updateBooruSite(booruSite: BooruSite)

    /**
     * Sets the selected Booru site by its ID.
     * @throws NoSuchElementException if no site with the given ID exists.
     */
    suspend fun setSelectedBooruId(id: String)

    /**
     * Sets the maximum number of posts to fetch per page.
     */
    suspend fun setPageLimit(limit: Int)
}