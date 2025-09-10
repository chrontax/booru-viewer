package org.chrontax.booru_viewer.ui.screens.home.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.chrontax.booru_viewer.data.model.Post
import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality

@Composable
fun BoxScope.PostView(
    pagerState: PagerState,
    posts: List<Post>,
    onDisableOverlay: () -> Unit,
    previewQuality: PreviewQuality,
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    BackHandler {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            onDisableOverlay()
            scope.launch { drawerState.close() }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState, drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = .5f))
            ) {
                PostDetails(posts[pagerState.currentPage])
            }
        }) {
        PostViewPager(
            state = pagerState,
            posts = posts,
            previewQuality = previewQuality,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        )

        PostViewControls(onShowInfo = {
            scope.launch { drawerState.open() }
        }, onDisableOverlay = {
            onDisableOverlay()
            scope.launch { drawerState.close() }
        })
    }
}