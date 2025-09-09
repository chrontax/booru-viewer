package org.chrontax.booru_viewer.data.preferences

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.Preferences
import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality
import org.chrontax.booru_viewer.util.checkUrl
import javax.inject.Inject

class DefaultPreferencesRepository @Inject constructor(private val preferencesDataStore: DataStore<Preferences>) :
    PreferencesRepository {
    override val preferencesFlow: Flow<Preferences> = preferencesDataStore.data

    override suspend fun addBooruSite(booruSite: BooruSite) {
        checkUrl(booruSite.url)

        preferencesDataStore.updateData { currentPreferences ->
            if (currentPreferences.sitesList.any { it.id == booruSite.id }) {
                throw IllegalArgumentException("A site with ID '${booruSite.id}' already exists.")
            }
            currentPreferences.toBuilder().addSites(booruSite).build()
        }
    }

    override suspend fun removeBooruSite(id: String) {
        preferencesDataStore.updateData { currentPreferences ->
            val siteIndex = currentPreferences.sitesList.indexOfFirst { it.id == id }
            if (siteIndex == -1) {
                throw NoSuchElementException("No site with ID '$id' exists.")
            }
            currentPreferences.toBuilder().removeSites(siteIndex).build()
        }
    }

    override suspend fun updateBooruSite(booruSite: BooruSite) {
        checkUrl(booruSite.url)

        preferencesDataStore.updateData { currentPreferences ->
            val siteIndex = currentPreferences.sitesList.indexOfFirst { it.id == booruSite.id }
            if (siteIndex == -1) {
                throw NoSuchElementException("No site with ID '${booruSite.id}' exists.")
            }
            currentPreferences.toBuilder()
                .setSites(siteIndex, booruSite).build()
        }
    }

    override suspend fun setSelectedBooruId(id: String) {
        preferencesDataStore.updateData { currentPreferences ->
            if (currentPreferences.sitesList.none { it.id == id }) {
                throw NoSuchElementException("No site with ID '$id' exists.")
            }
            currentPreferences.toBuilder().setSelectedBooruId(id).build()
        }
    }

    override suspend fun setPageLimit(limit: Int) {
        preferencesDataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setPageLimit(limit).build()
        }
    }

    override suspend fun setPreviewQuality(quality: PreviewQuality) {
        preferencesDataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setPreviewQuality(quality).build()
        }
    }
}