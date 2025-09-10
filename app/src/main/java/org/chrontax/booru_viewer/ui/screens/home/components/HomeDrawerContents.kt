package org.chrontax.booru_viewer.ui.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.chrontax.booru_viewer.data.model.SuggestedTag
import org.chrontax.booru_viewer.data.preferences.proto.BooruSite
import org.chrontax.booru_viewer.data.preferences.proto.Tab
import org.chrontax.booru_viewer.ui.components.DropdownButton
import org.chrontax.booru_viewer.ui.navigation.AppDestination

@Composable
fun HomeDrawerContents(
    tabs: List<Tab>,
    selectedTabName: String,
    tagInput: String,
    tags: List<String>,
    booruSiteList: List<BooruSite>,
    selectedBooruName: String,
    onBooruSelected: (BooruSite) -> Unit,
    onCreateTab: () -> Unit,
    onDeleteTab: () -> Unit,
    onTabSelected: (Tab) -> Unit,
    onTagInputChanged: (String) -> Unit,
    onTagAdded: () -> Unit,
    onTagRemoved: (String) -> Unit,
    suggestedTags: List<SuggestedTag>,
    navController: NavController
) {
    SettingsButtonAndBooruDropdown(
        booruSiteList = booruSiteList,
        selectedBooruName = selectedBooruName,
        navController = navController,
        onBooruSelected = onBooruSelected
    )

    TabControls(
        selectedTabName = selectedTabName,
        tabs = tabs,
        onCreateTab = onCreateTab,
        onDeleteTab = onDeleteTab,
        onTabSelected = onTabSelected
    )

    TagInput(
        value = tagInput,
        onValueChange = onTagInputChanged,
        suggestions = suggestedTags,
        onTagAdded = onTagAdded,
        modifier = Modifier.padding(8.dp)
    )
    TagList(tags = tags, onRemoveTag = onTagRemoved)
}

@Composable
fun SettingsButtonAndBooruDropdown(
    booruSiteList: List<BooruSite>,
    selectedBooruName: String,
    navController: NavController,
    onBooruSelected: (BooruSite) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Icon(Icons.Filled.Settings, contentDescription = "Settings", modifier = Modifier
            .clickable {
                navController.navigate(AppDestination.Settings.route)
            }
            .padding(end = 16.dp))

        DropdownButton(
            value = selectedBooruName,
            modifier = Modifier.fillMaxWidth(),
            options = booruSiteList,
            onOptionSelected = onBooruSelected,
            displayName = { it.name })
    }
}