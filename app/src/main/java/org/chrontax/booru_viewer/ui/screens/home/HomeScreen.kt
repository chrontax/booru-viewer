package org.chrontax.booru_viewer.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel()) {
    var tagInput by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = tagInput,
            onValueChange = { tagInput = it },
            label = { Text("Tag") })
        Button(onClick = { homeViewModel.addTag(tagInput) }) {
            Text("Add Tag")
        }
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(homeViewModel.tags.size) { index ->
                Text(homeViewModel.tags[index], modifier = Modifier.padding(4.dp))
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(homeViewModel.images.size) { index ->
                AsyncImage(
                    model = homeViewModel.images[index],
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
