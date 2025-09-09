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
import org.chrontax.booru_viewer.data.preferences.proto.Tab
import org.chrontax.booru_viewer.data.source.BooruSource
import org.chrontax.booru_viewer.data.source.BooruSourceFactory
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val booruSourceFactory: BooruSourceFactory,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private lateinit var booruSource: BooruSource
    private var currentPage = 0
    private lateinit var selectedBooruId: String

    private val selectedTab =
        preferencesRepository.preferencesFlow.map { prefs -> prefs.tabsList.first { it.id == prefs.selectedTabId } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = Tab.getDefaultInstance()
            )

    private val pageLimit = preferencesRepository.preferencesFlow.map { it.pageLimit }
        .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = 20)

    private var _posts = MutableStateFlow(emptyList<Post>())
    val posts = _posts.asStateFlow()

    val tags = selectedTab.map { it.tagsList }.stateIn(
            scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = emptyList()
        )

    private var _selectedBooruName = MutableStateFlow("")
    val selectedBooruName = _selectedBooruName.asStateFlow()

    private var _suggestedTags = MutableStateFlow(emptyList<SuggestedTag>())
    val suggestedTags = _suggestedTags.asStateFlow()

    private var _tagInput = MutableStateFlow("")
    val tagInput = _tagInput.asStateFlow()

    private var _isRefreshing = MutableStateFlow(true)
    val isRefreshing = _isRefreshing.asStateFlow()

    val selectedTabName = selectedTab.map { it.name }

    val booruSiteListFlow = preferencesRepository.preferencesFlow.map { it.sitesList }
    val previewQualityFlow = preferencesRepository.preferencesFlow.map { it.previewQuality }
    val tabsFlow = preferencesRepository.preferencesFlow.map { it.tabsList }

    init {
        viewModelScope.launch {
            val prefs = preferencesRepository.preferencesFlow.first()
            val tab = prefs.tabsList.first { it.id == prefs.selectedTabId }
            selectedBooruId = tab.booruId
            val selectedBooru = prefs.sitesList.first { it.id == tab.booruId }
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
        viewModelScope.launch {
            preferencesRepository.updateTab(selectedTab.value.toBuilder().addTags(tag).build())
            refreshPosts()
        }
    }

    fun removeTag(tag: String) {
        viewModelScope.launch {
            preferencesRepository.updateTab(
                selectedTab.value.toBuilder().clearTags().addAllTags(
                tags.value.filter { it != tag }).build())
            refreshPosts()
        }
    }

    fun updateTagInput(input: String) {
        _tagInput.value = input
    }

    fun selectBooru(booruSite: BooruSite, setInTab: Boolean = true) {
        if (booruSite.id == selectedBooruId) return
        selectedBooruId = booruSite.id
        _selectedBooruName.value = booruSite.name
        booruSource = booruSourceFactory.create(booruSite)
        viewModelScope.launch {
            if (setInTab) {
                preferencesRepository.updateTab(
                    selectedTab.value.toBuilder().setBooruId(booruSite.id).build()
                )
            }
            refreshPosts()
        }
    }

    fun loadNextPage() {
        if (_isRefreshing.value) return
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

    fun createTab() {
        viewModelScope.launch {
            val tags = preferencesRepository.preferencesFlow.first().defaultTagsList
            preferencesRepository.addTab(
                Tab.newBuilder().setName("New Tab").addAllTags(tags).setBooruId(selectedBooruId)
                    .setId(Uuid.random().toString()).build()
            )
        }
    }

    fun selectTab(tab: Tab) {
        if (tab.booruId != selectedBooruId) {
            viewModelScope.launch {
                val prefs = preferencesRepository.preferencesFlow.first()
                val booruSite = prefs.sitesList.first { it.id == tab.booruId }
                selectBooru(booruSite, setInTab = false)
            }
        }
        viewModelScope.launch {
            preferencesRepository.selectTab(tab.id)
            refreshPosts()
        }
    }

    fun deleteSelectedTab() {
        viewModelScope.launch {
            val prefs = preferencesRepository.preferencesFlow.first()
            var newSelectedTabId: String

            if (prefs.tabsCount == 1) {
                newSelectedTabId = Uuid.random().toString()
                preferencesRepository.addTab(
                    Tab.newBuilder().setName("Default").setBooruId(selectedBooruId)
                        .setId(newSelectedTabId)
                        .addAllTags(preferencesRepository.preferencesFlow.first().defaultTagsList)
                        .build()
                )
            } else {
                val newSelectedTab = prefs.tabsList.first { it.id != prefs.selectedTabId }
                newSelectedTabId = newSelectedTab.id

                if (newSelectedTab.booruId != selectedBooruId) {
                    val booruSite = prefs.sitesList.first { it.id == newSelectedTab.booruId }
                    selectBooru(booruSite, setInTab = false)
                }
            }

            val currentSelectedTabId = selectedTab.value.id
            preferencesRepository.selectTab(newSelectedTabId)
            preferencesRepository.removeTab(currentSelectedTabId)
            refreshPosts()
        }
    }
}