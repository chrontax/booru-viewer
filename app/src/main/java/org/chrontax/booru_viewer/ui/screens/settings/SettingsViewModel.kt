package org.chrontax.booru_viewer.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.chrontax.booru_viewer.data.preferences.PreferencesRepository
import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.BooruType
import org.chrontax.booru_viewer.data.preferences.proto.DanbooruSettings
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class SettingsViewModel @Inject constructor(private val preferencesRepository: PreferencesRepository) :
    ViewModel() {
    val booruSites = preferencesRepository.preferencesFlow.map { it.sitesList }
    val pageLimit = preferencesRepository.preferencesFlow.map { it.pageLimit }
    var selectedBooruSite by mutableStateOf<BooruSite?>(null)
        private set

    init {
        viewModelScope.launch {
            selectedBooruSite = booruSites.first().firstOrNull()
        }
    }

    fun createBooruSite() {
        val newBooru =
            BooruSite.newBuilder().setId(Uuid.random().toString()).setType(BooruType.DANBOORU)
                .setName("New Booru").setDanbooruSettings(DanbooruSettings.newBuilder().build())
                .setUrl("https://danbooru.donmai.us").build()
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
        selectedBooruSite = booruSite
    }

    fun deleteBooruSite(id: String) {
        viewModelScope.launch {
            preferencesRepository.removeBooruSite(id)
            if (selectedBooruSite?.id == id) {
                if (booruSites.first().isEmpty()) {
                    createBooruSite()
                }
                selectedBooruSite = booruSites.first().first()
            }
        }
    }

    fun setPageLimit(limit: Int) {
        viewModelScope.launch {
            preferencesRepository.setPageLimit(limit)
        }
    }
}