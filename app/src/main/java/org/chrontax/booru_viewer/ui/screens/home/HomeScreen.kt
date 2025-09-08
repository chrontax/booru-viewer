package org.chrontax.booru_viewer.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.chrontax.booru_viewer.ui.components.ImageWithLoadingIndicator
import org.chrontax.booru_viewer.ui.navigation.AppDestination
import org.chrontax.booru_viewer.ui.screens.home.components.TagInput

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel(), navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
    var booruDropdownExpanded by remember { mutableStateOf(false) }
    val booruSiteList by homeViewModel.booruSiteListFlow.collectAsState(emptyList())
    val suggestedTags by homeViewModel.suggestedTags.collectAsStateWithLifecycle()
    val tagInput by homeViewModel.tagInput.collectAsStateWithLifecycle()

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

    ModalNavigationDrawer(
        drawerState = drawerState, drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxWidth(.8f)) {
                Button(
                    onClick = { booruDropdownExpanded = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(homeViewModel.selectedBooruName)
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
                            })
                        }
                    }
                }

                TagInput(value = tagInput, onValueChange = {
                    homeViewModel.updateTagInput(it)
                }, suggestions = suggestedTags, onTagAdded = {
                    homeViewModel.addTag(tagInput)
                    homeViewModel.updateTagInput("")
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
                    items(homeViewModel.tags) { tag ->
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
                                        .clickable { homeViewModel.removeTag(tag) })
                            }
                        }
                    }
                }
            }
        }) {
        Scaffold { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = postListState
            ) {
                items(homeViewModel.posts) { post ->
                    ImageWithLoadingIndicator(
                        imageUrl = post.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(post.width.toFloat() / post.height)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
