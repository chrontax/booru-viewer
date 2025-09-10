package org.chrontax.booru_viewer.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.PreviewQuality
import org.chrontax.booru_viewer.ui.components.DefaultTextField
import org.chrontax.booru_viewer.ui.components.DropdownButton
import org.chrontax.booru_viewer.ui.screens.settings.compnents.BooruEditor
import org.chrontax.booru_viewer.util.displayName

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(), navController: NavController
) {
    val booruSites by settingsViewModel.booruSites.collectAsStateWithLifecycle(emptyList())
    val pageLimit by settingsViewModel.pageLimit.collectAsStateWithLifecycle(20)
    val selectedBooruSite by settingsViewModel.selectedBooruSite.collectAsStateWithLifecycle()
    val previewQuality by settingsViewModel.previewQuality.collectAsStateWithLifecycle()
    val defaultTags by settingsViewModel.defaultTags.collectAsStateWithLifecycle(emptyList())

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            GeneralSettings(
                pageLimit,
                defaultTags,
                previewQuality,
                onPageLimitChange = { settingsViewModel.setPageLimit(it) },
                onDefaultTagsChange = { settingsViewModel.setDefaultTags(it) },
                onPreviewQualityChange = { settingsViewModel.setPreviewQuality(it) })

            BooruSettings(
                selectedBooruSite,
                booruSites,
                onCreate = { settingsViewModel.createBooruSite() },
                onDelete = { settingsViewModel.deleteBooruSite(it) },
                onSelect = { settingsViewModel.selectBooruSite(it) },
                onUpdate = { settingsViewModel.updateBooruSite(it) })
        }
    }
}

@Composable
fun GeneralSettings(
    pageLimit: Int,
    defaultTags: List<String>,
    previewQuality: PreviewQuality,
    onPageLimitChange: (Int) -> Unit,
    onDefaultTagsChange: (List<String>) -> Unit,
    onPreviewQualityChange: (PreviewQuality) -> Unit
) {
    var pageLimitInput by remember(pageLimit) { mutableStateOf(pageLimit.toString()) }
    var defaultTagsInput by remember(defaultTags) { mutableStateOf(defaultTags.joinToString(" ")) }

    DefaultTextField(
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        value = pageLimitInput,
        onValueChange = {
            pageLimitInput = it
            val newLimit = it.toIntOrNull() ?: 0
            if (newLimit > 0) {
                onPageLimitChange(newLimit)
            }
        },
        label = "Page limit"
    )

    DefaultTextField(
        value = defaultTagsInput, label = "Default tags", onValueChange = {
            defaultTagsInput = it
            val newTags = it.split(" ").filter { tag -> tag.isNotEmpty() }
            onDefaultTagsChange(newTags)
        })

    Text("Preview quality:")
    DropdownButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        value = previewQuality.displayName(),
        options = PreviewQuality.entries.filter { it != PreviewQuality.UNRECOGNIZED },
        onOptionSelected = { onPreviewQualityChange(it) },
        displayName = { it.displayName() })
}

@Composable
fun BooruSettings(
    selectedBooruSite: BooruSite?,
    booruSites: List<BooruSite>,
    onCreate: () -> Unit,
    onDelete: (String) -> Unit,
    onUpdate: (BooruSite) -> Unit,
    onSelect: (BooruSite) -> Unit
) {
    Text("Booru Configuration", modifier = Modifier.padding(16.dp), fontSize = 20.sp)
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier.padding(16.dp), onClick = onCreate
        ) {
            Icon(
                Icons.Filled.Add, contentDescription = "Add Booru", modifier = Modifier.size(16.dp)
            )
        }

        DropdownButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = selectedBooruSite?.name ?: "Select Booru",
            options = booruSites,
            onOptionSelected = { onSelect(it) },
            displayName = { it.name })
    }

    if (selectedBooruSite != null) {
        BooruEditor(
            booruSite = selectedBooruSite, onDelete = onDelete, onSubmit = onUpdate
        )
    }
}