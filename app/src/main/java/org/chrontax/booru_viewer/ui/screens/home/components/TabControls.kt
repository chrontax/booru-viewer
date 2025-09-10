package org.chrontax.booru_viewer.ui.screens.home.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.chrontax.booru_viewer.data.preferences.proto.Tab
import org.chrontax.booru_viewer.ui.components.DropdownButton

@Composable
fun TabControls(
    selectedTabName: String,
    tabs: List<Tab>,
    onCreateTab: () -> Unit,
    onDeleteTab: () -> Unit,
    onTabSelected: (Tab) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier.padding(8.dp), onClick = onCreateTab
        ) {
            Icon(
                Icons.Filled.Add, contentDescription = "Add tab", modifier = Modifier.size(16.dp)
            )
        }

        Button(
            onClick = onDeleteTab
        ) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Delete tab",
                modifier = Modifier.size(16.dp)
            )
        }

        DropdownButton(
            value = selectedTabName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            options = tabs,
            onOptionSelected = onTabSelected,
            displayName = { it.name })
    }
}