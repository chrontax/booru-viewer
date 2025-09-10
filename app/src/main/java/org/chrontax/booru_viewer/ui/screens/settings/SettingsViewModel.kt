package org.chrontax.booru_viewer.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.chrontax.booru_viewer.data.preferences.PreferencesRepository
import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.BooruType
import org.chrontax.booru_viewer.data.preferences.proto.DanbooruSettings
import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality
import org.chrontax.booru_viewer.data.preferences.proto.Tab
import org.chrontax.booru_viewer.util.newDefaultDanbooruSite
import org.chrontax.booru_viewer.util.newTab
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class SettingsViewModel @Inject constructor(private val preferencesRepository: PreferencesRepository) :
    ViewModel() {
    val booruSites = preferencesRepository.preferencesFlow.map { it.sitesList }
    val pageLimit = preferencesRepository.preferencesFlow.map { it.pageLimit }
    val defaultTags = preferencesRepository.preferencesFlow.map { it.defaultTagsList }

    private var _selectedBooruSite = MutableStateFlow<BooruSite?>(null)
    val selectedBooruSite = _selectedBooruSite.asStateFlow()

    private var _previewQuality = MutableStateFlow(PreviewQuality.LOW)
    val previewQuality = _previewQuality.asStateFlow()

    init {
        viewModelScope.launch {
            _selectedBooruSite.value = booruSites.first().firstOrNull()
            _previewQuality.value = preferencesRepository.preferencesFlow.first().previewQuality
        }
    }

    fun createBooruSite() {
        val newBooru = newDefaultDanbooruSite(name = "New Booru")
        selectBooruSite(newBooru)
        viewModelScope.launch {
            preferencesRepository.addBooruSite(newBooru)
        }
    }

    fun updateBooruSite(booruSite: BooruSite) {
        viewModelScope.launch {
            preferencesRepository.updateBooruSite(booruSite)
        }
    }

    fun selectBooruSite(booruSite: BooruSite) {
        _selectedBooruSite.value = booruSite
    }

    fun deleteBooruSite(id: String) {
        viewModelScope.launch {
            preferencesRepository.removeBooruSite(id)
            if (selectedBooruSite.value?.id == id) {
                _selectedBooruSite.value = booruSites.first().first()
            }
        }
    }

    fun setPageLimit(limit: Int) {
        viewModelScope.launch {
            preferencesRepository.setPageLimit(limit)
        }
    }

    fun setPreviewQuality(quality: PreviewQuality) {
        viewModelScope.launch {
            preferencesRepository.setPreviewQuality(quality)
        }
        _previewQuality.value = quality
    }

    fun setDefaultTags(tags: List<String>) {
        viewModelScope.launch {
            preferencesRepository.setDefaultTags(tags)
        }
    }
}