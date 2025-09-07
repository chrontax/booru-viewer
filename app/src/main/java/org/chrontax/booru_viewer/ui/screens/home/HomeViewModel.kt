package org.chrontax.booru_viewer.ui.screens.home

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.chrontax.booru_viewer.data.model.Post
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

    var posts by mutableStateOf(emptyList<Post>())
        private set
    var tags by mutableStateOf(emptyList<String>())
        private set
    var selectedBooruName by mutableStateOf("")
        private set

    val booruSiteListFlow = preferencesRepository.preferencesFlow.map { it.sitesList }

    init {
        viewModelScope.launch {
            val firstBooru = booruSiteListFlow.first().first()
            selectedBooruName = firstBooru.name
            booruSource = booruSourceFactory.create(firstBooru)
            posts = booruSource.searchPosts(tags, currentPage, 20)
        }
    }

    fun addTag(tag: String) {
        if (tag in tags) return
        tags += tag
        viewModelScope.launch {
            posts = booruSource.searchPosts(tags, currentPage, 20)
        }
    }

    fun removeTag(tag: String) {
        tags -= tag
        viewModelScope.launch {
            posts = booruSource.searchPosts(tags, currentPage, 20)
        }
    }

    fun selectBooru(booruSite: BooruSite) {
        selectedBooruName = booruSite.name
        booruSource = booruSourceFactory.create(booruSite)
        viewModelScope.launch {
            posts = booruSource.searchPosts(tags, currentPage, 20)
        }
    }
}