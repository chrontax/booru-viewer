package org.chrontax.booru_viewer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.chrontax.booru_viewer.data.preferences.PreferencesRepository
import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.BooruType
import org.chrontax.booru_viewer.data.preferences.proto.DanbooruSettings
import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality
import org.chrontax.booru_viewer.data.preferences.proto.Tab
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class AppInitializerViewModel @Inject constructor(private val preferencesRepository: PreferencesRepository) : ViewModel() {
    private var _isAppReady = MutableStateFlow(false)
    val isAppReady = _isAppReady.asStateFlow()

    init {
        viewModelScope.launch {
            val prefs = preferencesRepository.preferencesFlow.first()
            if (prefs.sitesCount == 0) {
                preferencesRepository.addBooruSite(
                    BooruSite.newBuilder().setUrl("https://danbooru.donmai.us")
                        .setType(BooruType.DANBOORU).setName("Danbooru")
                        .setId(Uuid.random().toString()).setDanbooruSettings(
                            DanbooruSettings.newBuilder().build()
                        ).build()
                )
            }
            if (prefs.pageLimit == 0) {
                preferencesRepository.setPageLimit(20)
            }
            if (prefs.previewQuality == PreviewQuality.UNRECOGNIZED) {
                preferencesRepository.setPreviewQuality(PreviewQuality.LOW)
            }
            if (prefs.defaultTagsCount == 0) {
                preferencesRepository.setDefaultTags(listOf("rating:general"))
            }
            if (prefs.tabsCount == 0) {
                val prefs = preferencesRepository.preferencesFlow.first()
                val firstBooruId = prefs.sitesList.first().id
                val defaultTags = prefs.defaultTagsList
                val tabId = Uuid.random().toString()

                preferencesRepository.addTab(
                    Tab.newBuilder().setName("Default").setBooruId(firstBooruId)
                        .setId(tabId).addAllTags(defaultTags).build()
                )
                preferencesRepository.selectTab(tabId)
            }

            _isAppReady.value = true
        }
    }
}