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
import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality
import org.chrontax.booru_viewer.data.preferences.proto.Tab
import org.chrontax.booru_viewer.util.newDefaultDanbooruSite
import org.chrontax.booru_viewer.util.newTab
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
                preferencesRepository.addBooruSite(newDefaultDanbooruSite())
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

                val newTab = newTab(booruId = firstBooruId, name = "Default",
                    tags = prefs.defaultTagsList
                )

                preferencesRepository.addTab(newTab)
                preferencesRepository.selectTab(newTab.id)
            }

            _isAppReady.value = true
        }
    }
}