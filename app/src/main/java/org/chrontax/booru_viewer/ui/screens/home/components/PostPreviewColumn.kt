package org.chrontax.booru_viewer.ui.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.chrontax.booru_viewer.data.model.Post
import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality
import org.chrontax.booru_viewer.ui.components.ImageWithLoadingIndicator
import org.chrontax.booru_viewer.util.selectPreviewUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostPreviewColumn(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    posts: List<Post>,
    state: LazyListState,
    previewQuality: PreviewQuality,
    onImageClick: (Int) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing, onRefresh = onRefresh
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(), state = state
        ) {
            itemsIndexed(posts) { index, post ->
                ImageWithLoadingIndicator(
                    imageUrl = post.selectPreviewUrl(previewQuality),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(post.width.toFloat() / post.height)
                        .padding(4.dp)
                        .clickable { onImageClick(index) })
            }
        }
    }
}