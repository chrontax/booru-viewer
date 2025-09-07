package org.chrontax.booru_viewer.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.chrontax.booru_viewer.ui.components.ImageWithLoadingIndicator

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel()) {
    var tagInput by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxWidth(.8f)) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    value = tagInput,
                    onValueChange = { tagInput = it },
                    label = { Text("Tag") })
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onClick = {
                        homeViewModel.addTag(tagInput)
                        tagInput = ""
                    }) {
                    Text("Add Tag")
                }
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(homeViewModel.tags) { tag ->
                        Box(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer).padding(4.dp)) {
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
            Column(modifier = Modifier.padding(paddingValues)) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
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
}
