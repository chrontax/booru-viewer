package org.chrontax.booru_viewer.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun BoxWithOverlay(
    overlayEnabled: Boolean,
    modifier: Modifier = Modifier,
    overlayBackgroundColor: Color,
    overlayContent: @Composable BoxScope.() -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()

        if (overlayEnabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(overlayBackgroundColor)) {
                overlayContent()
            }
        }
    }
}