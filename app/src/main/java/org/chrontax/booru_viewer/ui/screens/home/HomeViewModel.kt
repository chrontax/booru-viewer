package org.chrontax.booru_viewer.ui.screens.home

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
    private var currentPage = 0
    private val pageLimit = preferencesRepository.preferencesFlow.map { it.pageLimit }.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = 20
    )

    private var _posts = MutableStateFlow(emptyList<Post>())
    val posts = _posts.asStateFlow()

    private var _tags = MutableStateFlow(emptyList<String>())
    val tags = _tags.asStateFlow()

    private var _selectedBooruName = MutableStateFlow("")
    val selectedBooruName = _selectedBooruName.asStateFlow()

    private var _suggestedTags = MutableStateFlow(emptyList<SuggestedTag>())
    val suggestedTags = _suggestedTags.asStateFlow()

    private var _tagInput = MutableStateFlow("")
    val tagInput = _tagInput.asStateFlow()

    private var _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val booruSiteListFlow = preferencesRepository.preferencesFlow.map { it.sitesList }
    val previewQualityFlow = preferencesRepository.preferencesFlow.map { it.previewQuality }

    init {
        viewModelScope.launch {
            val prefs = preferencesRepository.preferencesFlow.first()
            val selectedBooruId = prefs.selectedBooruId
            val selectedBooru =
                prefs.sitesList.find { it.id == selectedBooruId } ?: prefs.sitesList.first()
            _selectedBooruName.value = selectedBooru.name
            booruSource = booruSourceFactory.create(selectedBooru)
            refreshPosts()
            tagInput.collectLatest {
                delay(500) // Debounce
                _suggestedTags.value =
                    if (it.isEmpty()) emptyList() else booruSource.tagSuggestions(it)
            }
        }
    }

    fun addTag(tag: String) {
        if (tag in tags.value) return
        _tags.value += tag
        viewModelScope.launch { refreshPosts() }
    }

    fun removeTag(tag: String) {
        _tags.value -= tag
        viewModelScope.launch { refreshPosts() }
    }

    fun updateTagInput(input: String) {
        _tagInput.value = input
    }

    fun selectBooru(booruSite: BooruSite) {
        _selectedBooruName.value = booruSite.name
        booruSource = booruSourceFactory.create(booruSite)
        viewModelScope.launch {
            preferencesRepository.setSelectedBooruId(booruSite.id)
            refreshPosts()
        }
    }

    fun loadNextPage() {
        currentPage += 1
        viewModelScope.launch {
            _posts.value += booruSource.searchPosts(tags.value, currentPage, pageLimit.value)
        }
    }

    suspend fun refreshPosts() {
        _isRefreshing.value = true
        currentPage = 0
        _posts.value = booruSource.searchPosts(tags.value, currentPage, pageLimit.value)
        _isRefreshing.value = false
    }

    fun refreshPostsOnPull() {
        viewModelScope.launch {
            refreshPosts()
        }
    }
}