package org.chrontax.booru_viewer.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.chrontax.booru_viewer.data.model.Post
import org.chrontax.booru_viewer.data.model.SuggestedTag
import org.chrontax.booru_viewer.data.preferences.PreferencesRepository
import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.source.BooruSource
import org.chrontax.booru_viewer.data.source.BooruSourceFactory
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val booruSourceFactory: BooruSourceFactory,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private lateinit var booruSource: BooruSource
    private val currentPage = 0
    private val pageLimit = preferencesRepository.preferencesFlow.map { it.pageLimit }.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = 20
    )

    var posts by mutableStateOf(emptyList<Post>())
        private set
    var tags by mutableStateOf(emptyList<String>())
        private set
    var selectedBooruName by mutableStateOf("")
        private set

    private var _suggestedTags = MutableStateFlow(emptyList<SuggestedTag>())
    val suggestedTags = _suggestedTags.asStateFlow()

    private var _tagInput = MutableStateFlow("")
    val tagInput = _tagInput.asStateFlow()

    val booruSiteListFlow = preferencesRepository.preferencesFlow.map { it.sitesList }

    init {
        viewModelScope.launch {
            val firstBooru = booruSiteListFlow.first().first()
            selectedBooruName = firstBooru.name
            booruSource = booruSourceFactory.create(firstBooru)
            posts = booruSource.searchPosts(tags, currentPage, pageLimit.value)
            tagInput.collectLatest {
                delay(500) // Debounce
                _suggestedTags.value =
                    if (it.isEmpty()) emptyList() else booruSource.tagSuggestions(it)
            }
        }
    }

    fun addTag(tag: String) {
        if (tag in tags) return
        tags += tag
        viewModelScope.launch {
            posts = booruSource.searchPosts(tags, currentPage, pageLimit.value)
        }
    }

    fun removeTag(tag: String) {
        tags -= tag
        viewModelScope.launch {
            posts = booruSource.searchPosts(tags, currentPage, pageLimit.value)
        }
    }

    fun updateTagInput(input: String) {
        _tagInput.value = input
    }

    fun selectBooru(booruSite: BooruSite) {
        selectedBooruName = booruSite.name
        booruSource = booruSourceFactory.create(booruSite)
        viewModelScope.launch {
            preferencesRepository.setSelectedBooruId(booruSite.id)
            posts = booruSource.searchPosts(tags, currentPage, 20)
        }
    }
}