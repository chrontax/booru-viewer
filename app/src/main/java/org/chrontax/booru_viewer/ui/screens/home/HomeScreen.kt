package org.chrontax.booru_viewer.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.chrontax.booru_viewer.ui.components.ImageWithLoadingIndicator
import org.chrontax.booru_viewer.ui.navigation.AppDestination
import org.chrontax.booru_viewer.ui.screens.home.components.BoxWithOverlay
import org.chrontax.booru_viewer.ui.screens.home.components.CopyableText
import org.chrontax.booru_viewer.ui.screens.home.components.PropertyText
import org.chrontax.booru_viewer.ui.screens.home.components.TagInput

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel(), navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var booruDropdownExpanded by remember { mutableStateOf(false) }
    val booruSiteList by homeViewModel.booruSiteListFlow.collectAsState(emptyList())
    val suggestedTags by homeViewModel.suggestedTags.collectAsStateWithLifecycle()
    val tagInput by homeViewModel.tagInput.collectAsStateWithLifecycle()

    val posts by homeViewModel.posts.collectAsStateWithLifecycle()
    val tags by homeViewModel.tags.collectAsStateWithLifecycle()
    val selectedBooruName by homeViewModel.selectedBooruName.collectAsState()

    val postListState = rememberLazyListState()
    val postCountBelow = remember {
        derivedStateOf {
            val lastVisible = postListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = postListState.layoutInfo.totalItemsCount
            maxOf(0, totalItems - (lastVisible + 1))
        }
    }

    LaunchedEffect(postCountBelow.value) {
        if (postCountBelow.value < 5) {
            homeViewModel.loadNextPage()
        }
    }

    var postViewOverlayEnabled by remember { mutableStateOf(false) }
    val postViewPagerState = rememberPagerState(pageCount = { posts.size })
    val postViewDetailsDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val isRefreshing by homeViewModel.isRefreshing.collectAsStateWithLifecycle()

    LaunchedEffect(postViewPagerState.currentPage) {
        postListState.scrollToItem(postViewPagerState.currentPage)
    }

    val scope = rememberCoroutineScope()

    Scaffold { paddingValues ->
        BoxWithOverlay(
            overlayEnabled = postViewOverlayEnabled,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            overlayBackgroundColor = MaterialTheme.colorScheme.background,
            overlayContent = {
                BackHandler {
                    postViewOverlayEnabled = false
                    scope.launch {
                        postViewDetailsDrawerState.close()
                    }
                }

                ModalNavigationDrawer(
                    drawerState = postViewDetailsDrawerState, drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier
                                .fillMaxWidth(.8f)
                                .background(MaterialTheme.colorScheme.background.copy(alpha = .5f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                val post = posts[postViewPagerState.currentPage]
                                val propertyModifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                PropertyText("ID", post.id.toString(), modifier = propertyModifier)
                                PropertyText(
                                    "Score", post.score.toString(), modifier = propertyModifier
                                )
                                PropertyText(
                                    "Dimensions",
                                    "${post.width}x${post.height}",
                                    modifier = propertyModifier
                                )
                                PropertyText(
                                    "Rating", post.rating.displayName, modifier = propertyModifier
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    "Tags",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                LazyColumn() {
                                    itemsIndexed(post.tags) { index, tag ->
                                        if (index > 0) {
                                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                        }

                                        CopyableText(
                                            tag,
                                            copyDataLabel = "Tag",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }) {
                    HorizontalPager(
                        state = postViewPagerState,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .clickable(indication = null, interactionSource = null) {},
                        beyondViewportPageCount = 0
                    ) { page ->
                        ImageWithLoadingIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            imageUrl = posts[page].imageUrl,
                            contentDescription = null
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = .5f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    postViewOverlayEnabled = false
                                    scope.launch {
                                        postViewDetailsDrawerState.close()
                                    }
                                })
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = "Details",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    scope.launch {
                                        postViewDetailsDrawerState.open()
                                    }
                                })
                    }
                }
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
                        Button(
                            onClick = { booruDropdownExpanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(selectedBooruName)
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = "Select Booru type",
                                modifier = Modifier.size(16.dp)
                            )

                            DropdownMenu(
                                modifier = Modifier.fillMaxWidth(),
                                expanded = booruDropdownExpanded,
                                onDismissRequest = { booruDropdownExpanded = false }) {
                                booruSiteList.forEach { booru ->
                                    DropdownMenuItem(text = {
                                        Text(booru.name)
                                    }, onClick = {
                                        homeViewModel.selectBooru(booru)
                                        scope.launch {
                                            postListState.scrollToItem(0)
                                        }
                                    })
                                }
                            }
                        }

                        TagInput(value = tagInput, onValueChange = {
                            homeViewModel.updateTagInput(it)
                        }, suggestions = suggestedTags, onTagAdded = {
                            homeViewModel.addTag(tagInput)
                            homeViewModel.updateTagInput("")
                            scope.launch {
                                postListState.scrollToItem(0)
                            }
                        }, modifier = Modifier.padding(8.dp))
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    navController.navigate(AppDestination.Settings.route)
                                })
                        LazyRow(modifier = Modifier.fillMaxWidth()) {
                            items(tags) { tag ->
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(4.dp)
                                ) {
                                    Row {
                                        Text(tag, modifier = Modifier.padding(4.dp))
                                        Icon(
                                            Icons.Filled.Close,
                                            contentDescription = "Remove tag",
                                            modifier = Modifier
                                                .padding(start = 4.dp)
                                                .padding(2.dp)
                                                .width(16.dp)
                                                .align(Alignment.CenterVertically)
                                                .clickable {
                                                    homeViewModel.removeTag(tag)
                                                    scope.launch {
                                                        postListState.scrollToItem(0)
                                                    }
                                                })
                                    }
                                }
                            }
                        }
                    }
                }) {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { homeViewModel.refreshPostsOnPull() }) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(), state = postListState
                    ) {
                        itemsIndexed(posts) { index, post ->
                            ImageWithLoadingIndicator(
                                imageUrl = post.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(post.width.toFloat() / post.height)
                                    .padding(4.dp)
                                    .clickable {
                                        scope.launch {
                                            postViewPagerState.scrollToPage(index)
                                        }
                                        postViewOverlayEnabled = true
                                    })
                        }
                    }
                }
            }
        }
    }
}
