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
import org.chrontax.booru_viewer.ui.components.DefaultTextField

@Composable
fun DanbooruEditor(settings: DanbooruSettings, onSettingsChange: (DanbooruSettings) -> Unit) {
    var settingsBuilder by remember(settings) { mutableStateOf(settings.toBuilder()) }
    var apiKey by remember(settings) { mutableStateOf(settings.apiKey) }
    var username by remember(settings) { mutableStateOf(settings.username) }

    Column {
        DefaultTextField(value = apiKey, onValueChange = {
            apiKey = it
            settingsBuilder.apiKey = it
            onSettingsChange(settingsBuilder.build())
        }, label = "API Key")

        DefaultTextField(value = username, onValueChange = {
            username = it
            settingsBuilder.username = it
            onSettingsChange(settingsBuilder.build())
        }, label = "Username")
    }

}