package org.chrontax.booru_viewer.ui.screens.settings

import android.inputmethodservice.Keyboard
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.first
import org.chrontax.booru_viewer.ui.screens.settings.compnents.BooruEditor

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(), navController: NavController
) {
    val booruSites by settingsViewModel.booruSites.collectAsState(emptyList())
    var booruDropdownExpanded by remember { mutableStateOf(false) }
    val pageLimit by settingsViewModel.pageLimit.collectAsState(20)
    var pageLimitInput by remember { mutableStateOf(pageLimit.toString()) }

    LaunchedEffect(pageLimit) {
        pageLimitInput = pageLimit.toString()
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = pageLimitInput,
                onValueChange = {
                    pageLimitInput = it
                    val newLimit = it.toIntOrNull() ?: 0
                    if (newLimit >= 0) {
                        settingsViewModel.setPageLimit(newLimit)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text("Page Limit") })

            Text("Booru Configuration", modifier = Modifier.padding(16.dp), fontSize = 20.sp)
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier.padding(16.dp),
                    onClick = { settingsViewModel.createBooruSite() }) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add Booru",
                        modifier = Modifier.size(16.dp)
                    )
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onClick = { booruDropdownExpanded = true }) {
                    Text(settingsViewModel.selectedBooruSite?.name ?: "Select Booru")
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = "Select Booru",
                        modifier = Modifier.size(16.dp)
                    )
                    DropdownMenu(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = booruDropdownExpanded,
                        onDismissRequest = { booruDropdownExpanded = false }) {
                        booruSites.forEach { booruSite ->
                            DropdownMenuItem(text = { Text(booruSite.name) }, onClick = {
                                settingsViewModel.selectBooruSite(booruSite)
                                booruDropdownExpanded = false
                            })
                        }
                    }
                }
            }

            if (settingsViewModel.selectedBooruSite != null) {
                BooruEditor(
                    booruSite = settingsViewModel.selectedBooruSite!!,
                    onDelete = { settingsViewModel.deleteBooruSite(it) },
                    onSubmit = { settingsViewModel.updateBooruSite(it) })
            }
        }
    }
}