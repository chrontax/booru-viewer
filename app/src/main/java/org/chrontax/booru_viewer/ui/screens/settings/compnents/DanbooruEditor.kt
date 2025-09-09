package org.chrontax.booru_viewer.ui.screens.settings.compnents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.chrontax.booru_viewer.data.preferences.proto.DanbooruSettings

@Composable
fun DanbooruEditor(settings: DanbooruSettings, onSettingsChange: (DanbooruSettings) -> Unit) {
    var settingsBuilder by remember(settings) { mutableStateOf(settings.toBuilder()) }
    var apiKey by remember(settings) { mutableStateOf(settings.apiKey) }
    var username by remember(settings) { mutableStateOf(settings.username) }

    Column {
        OutlinedTextField(value = apiKey, singleLine = true, onValueChange = {
            apiKey = it
            settingsBuilder.apiKey = it
            onSettingsChange(settingsBuilder.build())
        }, modifier = Modifier.fillMaxWidth(), label = { Text("API Key") })

        OutlinedTextField(value = username, singleLine = true, onValueChange = {
            username = it
            settingsBuilder.username = it
            onSettingsChange(settingsBuilder.build())
        }, modifier = Modifier.fillMaxWidth(), label = { Text("Username") })
    }

}