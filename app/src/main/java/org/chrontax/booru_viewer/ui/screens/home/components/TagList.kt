package org.chrontax.booru_viewer.ui.screens.home.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TagList(tags: List<String>, onRemoveTag: (String) -> Unit) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(tags) { tag ->
            Button(onClick = { onRemoveTag(tag) }, modifier = Modifier.padding(4.dp)) {
                Row {
                    Text(tag, modifier = Modifier.padding(2.dp))
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Remove tag",
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .padding(2.dp)
                            .width(16.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}