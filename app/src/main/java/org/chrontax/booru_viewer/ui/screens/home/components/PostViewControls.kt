package org.chrontax.booru_viewer.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PostViewControls(
    onShowInfo: () -> Unit,
    onDisableOverlay: () -> Unit,
) {
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
                    onDisableOverlay()
                })
        Icon(
            Icons.Filled.Info,
            contentDescription = "Details",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    onShowInfo()
                })
    }
}