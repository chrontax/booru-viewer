package org.chrontax.booru_viewer.ui.screens.settings.compnents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.BooruType
import org.chrontax.booru_viewer.util.checkUrl
import org.chrontax.booru_viewer.util.displayName

@Composable
fun BooruEditor(
    booruSite: BooruSite, onSubmit: (BooruSite) -> Unit, onDelete: (id: String) -> Unit
) {
    var booruBuilder by remember(booruSite) { mutableStateOf(booruSite.toBuilder()) }
    var name by remember(booruSite) { mutableStateOf(booruSite.name) }
    var url by remember(booruSite) { mutableStateOf(booruSite.url) }
    var type by remember(booruSite) { mutableStateOf(booruSite.type) }
    var isUrlValid by remember(booruSite) { mutableStateOf(true) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(value = name, singleLine = true, onValueChange = {
            name = it
            booruBuilder.name = it
        }, modifier = Modifier.fillMaxWidth(), label = { Text("Name") })

        OutlinedTextField(value = url, singleLine = true, onValueChange = {
            url = it
            booruBuilder.url = it
        }, modifier = Modifier.fillMaxWidth(), isError = !isUrlValid, label = { Text("URL") })

        Button(
            onClick = { typeDropdownExpanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(type.displayName())
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = "Select Booru type",
                modifier = Modifier.size(16.dp)
            )

            DropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                expanded = typeDropdownExpanded,
                onDismissRequest = { typeDropdownExpanded = false }) {
                BooruType.entries.forEach { booruType ->
                    if (booruType != BooruType.UNRECOGNIZED) {
                        DropdownMenuItem(text = {
                            Text(booruType.displayName())
                        }, onClick = {
                            type = booruType
                            booruBuilder.type = booruType
                            typeDropdownExpanded = false
                        })
                    }
                }
            }
        }

        when (type) {
            BooruType.DANBOORU -> DanbooruEditor(booruSite.danbooruSettings, onSettingsChange = {
                booruBuilder.danbooruSettings = it
            })

            BooruType.GELBOORU -> GelbooruEditor(booruSite.gelbooruSettings, onSettingsChange = {
                booruBuilder.gelbooruSettings = it
            })

            else -> {}
        }

        Row {
            Button(
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .padding(16.dp), onClick = {
                    try {
                        checkUrl(url)
                        onSubmit(booruBuilder.build())
                    } catch (e: IllegalArgumentException) {
                        isUrlValid = false
                    }
                }) {
                Text("Save")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), onClick = {
                    onDelete(booruSite.id)
                }) {
                Text("Delete")
            }
        }
    }
}