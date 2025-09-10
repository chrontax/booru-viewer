package org.chrontax.booru_viewer.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality
import org.chrontax.booru_viewer.ui.screens.home.components.BoxWithOverlay
import org.chrontax.booru_viewer.ui.screens.home.components.HomeDrawerContents
import org.chrontax.booru_viewer.ui.screens.home.components.PostPreviewColumn
import org.chrontax.booru_viewer.ui.screens.home.components.PostView

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel(), navController: NavController) {
    val booruSiteList by homeViewModel.booruSiteListFlow.collectAsStateWithLifecycle(emptyList())
    val suggestedTags by homeViewModel.suggestedTags.collectAsStateWithLifecycle()
    val tagInput by homeViewModel.tagInput.collectAsStateWithLifecycle()
    val posts by homeViewModel.posts.collectAsStateWithLifecycle()
    val tags by homeViewModel.tags.collectAsStateWithLifecycle()
    val selectedBooruName by homeViewModel.selectedBooruName.collectAsState()
    val isRefreshing by homeViewModel.isRefreshing.collectAsStateWithLifecycle()
    val previewQuality by homeViewModel.previewQualityFlow.collectAsStateWithLifecycle(initialValue = PreviewQuality.LOW)
    val tabs by homeViewModel.tabsFlow.collectAsStateWithLifecycle(emptyList())
    val selectedTabName by homeViewModel.selectedTabName.collectAsStateWithLifecycle("")

    var postViewOverlayEnabled by remember { mutableStateOf(false) }
    val postViewPagerState = rememberPagerState(pageCount = { posts.size })
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val postListState = rememberLazyListState()
    val postCountBelow = remember {
        derivedStateOf {
            val lastVisible = postListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = postListState.layoutInfo.totalItemsCount
            maxOf(0, totalItems - (lastVisible + 1))
        }
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(postCountBelow.value) {
        if (postCountBelow.value < 5) {
            homeViewModel.loadNextPage()
        }
    }
    LaunchedEffect(postViewPagerState.currentPage) {
        postListState.scrollToItem(postViewPagerState.currentPage)
    }

    Scaffold { paddingValues ->
        BoxWithOverlay(
            overlayEnabled = postViewOverlayEnabled,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            overlayBackgroundColor = MaterialTheme.colorScheme.background,
            overlayContent = {
                PostView(
                    pagerState = postViewPagerState,
                    posts = posts,
                    previewQuality = previewQuality,
                    onDisableOverlay = { postViewOverlayEnabled = false })
            }) {
            ModalNavigationDrawer(
                drawerState = drawerState, drawerContent = {
                    if (drawerState.isOpen) {
                        BackHandler {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    }

                    ModalDrawerSheet(modifier = Modifier.fillMaxWidth(.8f)) {
                        HomeDrawerContents(
                            tabs = tabs,
                            selectedTabName = selectedTabName,
                            tagInput = tagInput,
                            suggestedTags = suggestedTags,
                            tags = tags,
                            booruSiteList = booruSiteList,
                            selectedBooruName = selectedBooruName,
                            onBooruSelected = {
                                homeViewModel.selectBooru(it)
                                scope.launch {
                                    postListState.scrollToItem(0)
                                }
                            },
                            onCreateTab = { homeViewModel.createTab() },
                            onDeleteTab = { homeViewModel.deleteSelectedTab() },
                            onTabSelected = { homeViewModel.selectTab(it) },
                            onTagInputChanged = { homeViewModel.updateTagInput(it) },
                            onTagAdded = {
                                homeViewModel.addTag(tagInput)
                                homeViewModel.updateTagInput("")
                                scope.launch {
                                    postListState.scrollToItem(0)
                                }
                            },
                            onTagRemoved = {
                                homeViewModel.removeTag(it)
                                scope.launch {
                                    postListState.scrollToItem(0)
                                }
                            },
                            navController = navController,
                        )
                    }
                }) {
                PostPreviewColumn(
                    isRefreshing = isRefreshing,
                    onRefresh = { homeViewModel.refreshPostsOnPull() },
                    onImageClick = {
                        scope.launch {
                            postViewPagerState.scrollToPage(it)
                        }
                        postViewOverlayEnabled = true
                    },
                    state = postListState,
                    posts = posts,
                    previewQuality = previewQuality
                )
            }
        }
    }
}
