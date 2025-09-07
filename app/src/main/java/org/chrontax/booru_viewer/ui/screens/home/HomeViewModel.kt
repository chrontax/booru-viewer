package org.chrontax.booru_viewer.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.chrontax.booru_viewer.data.model.Post
import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.BooruType
import org.chrontax.booru_viewer.data.source.BooruSourceFactory
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val booruSourceFactory: BooruSourceFactory) : ViewModel() {
    private val booruSource = booruSourceFactory.create(
        BooruSite.newBuilder().setUrl("https://danbooru.donmai.us").setType(
            BooruType.DANBOORU
        ).build()
    )
    private val currentPage = 0

    var posts by mutableStateOf(emptyList<Post>())
        private set
    var tags by mutableStateOf(emptyList<String>())
        private set

    init {
        viewModelScope.launch {
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
}