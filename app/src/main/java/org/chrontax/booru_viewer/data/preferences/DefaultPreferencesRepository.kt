package org.chrontax.booru_viewer.data.preferences

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.Preferences
import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality
import org.chrontax.booru_viewer.data.preferences.proto.Tab
import org.chrontax.booru_viewer.util.checkUrl
import org.chrontax.booru_viewer.util.newDefaultDanbooruSite
import org.chrontax.booru_viewer.util.newTab
import javax.inject.Inject
import kotlin.uuid.Uuid

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
            val builder = currentPreferences.toBuilder().removeSites(siteIndex)
            var removedSelectedTab = false
            var removed = 0
            currentPreferences.tabsList.forEachIndexed { index, tab ->
                if (tab.booruId != id) return@forEachIndexed
                if (tab.id == currentPreferences.selectedTabId) {
                    removedSelectedTab = true
                }
                builder.removeTabs(index - removed++)
            }
            if (removedSelectedTab) {
                if (builder.tabsCount > 0) {
                    builder.selectedTabId = builder.getTabs(0).id
                } else {
                    val firstBooruId = if (builder.sitesCount > 0) builder.getSites(0).id else {
                        val newBooru = newDefaultDanbooruSite()
                        builder.addSites(newBooru)
                        newBooru.id
                    }
                    val newTab = newTab(booruId = firstBooruId, name = "Default",
                        tags = builder.defaultTagsList
                    )
                    builder.addTabs(newTab)
                    builder.selectedTabId = newTab.id
                }
            }
            builder.build()
        }
    }

    override suspend fun updateBooruSite(booruSite: BooruSite) {
        checkUrl(booruSite.url)

        preferencesDataStore.updateData { currentPreferences ->
            val siteIndex = currentPreferences.sitesList.indexOfFirst { it.id == booruSite.id }
            if (siteIndex == -1) {
                throw NoSuchElementException("No site with ID '${booruSite.id}' exists.")
            }
            currentPreferences.toBuilder().setSites(siteIndex, booruSite).build()
        }
    }

    override suspend fun setPageLimit(limit: Int) {
        if (limit < 1) {
            throw IllegalArgumentException("Page limit must be greater than zero.")
        }
        preferencesDataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setPageLimit(limit.toInt()).build()
        }
    }

    override suspend fun setPreviewQuality(quality: PreviewQuality) {
        preferencesDataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setPreviewQuality(quality).build()
        }
    }

    override suspend fun setDefaultTags(tags: List<String>) {
        preferencesDataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().clearDefaultTags().addAllDefaultTags(tags).build()
        }
    }

    override suspend fun addTab(tab: Tab) {
        preferencesDataStore.updateData { currentPreferences ->
            if (currentPreferences.tabsList.any { it.id == tab.id }) {
                throw IllegalArgumentException("A tab with ID '${tab.id}' already exists.")
            }
            if (currentPreferences.sitesList.none { it.id == tab.booruId }) {
                throw NoSuchElementException("No booru with ID '${tab.booruId}' exists.")
            }
            currentPreferences.toBuilder().addTabs(tab).build()
        }
    }

    override suspend fun removeTab(id: String) {
        preferencesDataStore.updateData { currentPreferences ->
            val tabIndex = currentPreferences.tabsList.indexOfFirst { it.id == id }
            if (tabIndex == -1) {
                throw NoSuchElementException("No tab with ID '$id' exists.")
            }
            val builder = currentPreferences.toBuilder().removeTabs(tabIndex)
            if (id == currentPreferences.selectedTabId) {
                if (builder.tabsCount > 0) {
                    builder.selectedTabId = builder.getTabs(0).id
                } else {
                    val newTabId = Uuid.random().toString()
                    val firstBooruId = builder.getSites(0).id
                    builder.addTabs(
                        Tab.newBuilder().setId(newTabId).setBooruId(firstBooruId).setName("Default")
                            .addAllTags(builder.defaultTagsList).build()
                    )
                    builder.selectedTabId = newTabId
                }
            }
            builder.build()
        }
    }

    override suspend fun updateTab(tab: Tab) {
        preferencesDataStore.updateData { currentPreferences ->
            val tabIndex = currentPreferences.tabsList.indexOfFirst { it.id == tab.id }
            if (tabIndex == -1) {
                throw NoSuchElementException("No tab with ID '${tab.id}' exists.")
            }
            if (currentPreferences.sitesList.none { it.id == tab.booruId }) {
                throw NoSuchElementException("No booru with ID '${tab.booruId}' exists.")
            }
            currentPreferences.toBuilder().setTabs(tabIndex, tab).build()
        }
    }

    override suspend fun selectTab(id: String) {
        preferencesDataStore.updateData { currentPreferences ->
            val tabIndex = currentPreferences.tabsList.indexOfFirst { it.id == id }
            if (tabIndex == -1) {
                throw NoSuchElementException("No tab with ID '$id' exists.")
            }
            currentPreferences.toBuilder().setSelectedTabId(id).build()
        }
    }
}