package org.chrontax.booru_viewer.data.preferences

import kotlinx.coroutines.flow.Flow
import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.Preferences
import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality
import org.chrontax.booru_viewer.data.preferences.proto.Tab

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
     * Sets the maximum number of posts to fetch per page.
     */
    suspend fun setPageLimit(limit: Int)

    /**
     * Sets the quality of the preview images.
     */
    suspend fun setPreviewQuality(quality: PreviewQuality)

    /**
     * Sets the default tags to apply when creating a tab.
     */
    suspend fun setDefaultTags(tags: List<String>)

    /**
     * @throws IllegalArgumentException if a tag with the same ID already exists.
     * @throws NoSuchElementException if a booru with the given ID does not exist.
     */
    suspend fun addTab(tab: Tab)

    /**
     * @throws NoSuchElementException if no tab with the given ID exists.
     */
    suspend fun removeTab(id: String)

    /**
     * @throws NoSuchElementException if no tab with the given ID exists.
     */
    suspend fun updateTab(tab: Tab)

    /**
     * @throws NoSuchElementException if no tab with the given ID exists.
     */
    suspend fun selectTab(id: String)
}