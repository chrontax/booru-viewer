package org.chrontax.booru_viewer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import org.chrontax.booru_viewer.R

@Composable
fun ImageWithLoadingIndicator(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp),
                strokeWidth = 2.dp,
            )
        }

        if (isError) {
            Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_launcher_foreground), // TODO: replace with actual error image
                contentDescription = "Error loading image",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp),
                colorFilter = ColorFilter.tint(androidx.compose.ui.graphics.Color.Gray),
            )
        }

        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxWidth(),
            onState = {
                when (it) {
                    is AsyncImagePainter.State.Loading -> {
                        isLoading = true
                        isError = false
                    }

                    is AsyncImagePainter.State.Error -> {
                        isLoading = false
                        isError = true
                    }

                    is AsyncImagePainter.State.Success -> {
                        isLoading = false
                        isError = false
                    }

                    else -> {}
                }
            })
    }
}