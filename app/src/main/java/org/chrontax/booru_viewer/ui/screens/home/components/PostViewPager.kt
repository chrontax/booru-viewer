package org.chrontax.booru_viewer.ui.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.chrontax.booru_viewer.data.model.Post
import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality
import org.chrontax.booru_viewer.util.selectPreviewUrl

@Composable
fun PostViewPager(posts: List<Post>, previewQuality: PreviewQuality, state: PagerState, modifier: Modifier = Modifier) {
    HorizontalPager(
        state = state,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
        beyondViewportPageCount = 0
    ) { page ->
        ZoomableBox {
            ImageWithPreview(
                modifier = Modifier.fillMaxWidth(),
                imageUrl = posts[page].imageUrl,
                previewUrl = posts[page].selectPreviewUrl(previewQuality),
                contentDescription = null
            )
        }
    }}