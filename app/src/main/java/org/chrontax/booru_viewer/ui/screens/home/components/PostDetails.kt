package org.chrontax.booru_viewer.ui.screens.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.chrontax.booru_viewer.data.model.Post

@Composable
fun PostDetails(post: Post) {
    val propertyModifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        PropertyText("ID", post.id.toString(), modifier = propertyModifier)
        PropertyText(
            "Score", post.score.toString(), modifier = propertyModifier
        )
        PropertyText(
            "Dimensions", "${post.width}x${post.height}", modifier = propertyModifier
        )
        PropertyText(
            "Rating", post.rating.displayName, modifier = propertyModifier
        )
        Spacer(modifier = Modifier.size(8.dp))

        Text(
            "Tags", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.size(4.dp))

        LazyColumn() {
            itemsIndexed(post.tags) { index, tag ->
                if (index > 0) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                }

                CopyableText(
                    tag, copyDataLabel = "Tag", modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}