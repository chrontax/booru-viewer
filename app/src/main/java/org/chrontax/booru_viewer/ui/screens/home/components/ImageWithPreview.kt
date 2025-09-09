package org.chrontax.booru_viewer.ui.screens.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import org.chrontax.booru_viewer.ui.components.ImageWithLoadingIndicator

@Composable
fun ImageWithPreview(
    imageUrl: String, previewUrl: String, modifier: Modifier = Modifier, contentDescription: String?
) {
    var mainImageLoading by remember { mutableStateOf(true) }
    var mainImageError by remember { mutableStateOf(false) }

    Box {
        if (mainImageLoading || mainImageError) {
            ImageWithLoadingIndicator(
                previewUrl, contentDescription = contentDescription, modifier = modifier
            )
        }

        AsyncImage(
            imageUrl,
            contentDescription = contentDescription,
            modifier = modifier,
            onLoading = { mainImageLoading = true },
            onSuccess = { mainImageLoading = false },
            onError = { mainImageLoading = false; mainImageError = true })
    }
}