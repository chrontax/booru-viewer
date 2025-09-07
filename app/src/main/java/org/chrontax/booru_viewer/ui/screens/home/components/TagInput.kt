package org.chrontax.booru_viewer.ui.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import org.chrontax.booru_viewer.data.model.SuggestedTag

@Composable
fun TagInput(
    suggestions: List<SuggestedTag>,
    onTagAdded: () -> Unit,
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {

        Box(
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text("Tag") },
                modifier = Modifier
                    .onSizeChanged { textFieldSize = it }
                    .onFocusChanged {
                        isFocused = it.isFocused
                    })

            if (isFocused && suggestions.isNotEmpty()) {
                Popup(
                    offset = IntOffset(0, textFieldSize.height),
                    alignment = Alignment.TopStart,
                ) {
                    ElevatedCard(
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                    ) {
                        LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                            items(suggestions) { tag ->
                                Row(modifier = Modifier
                                    .clickable {
                                        onValueChange(tag.name)
                                        focusManager.clearFocus()
                                    }
                                    .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Text(tag.name, modifier = Modifier.weight(1f))
                                    if (tag.postCount != null) {
                                        Text(
                                            tag.postCount.toString(), fontWeight = FontWeight.Light
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(onClick = onTagAdded) {
            Icon(
                Icons.Filled.Add, contentDescription = "Add tag", modifier = Modifier.size(16.dp)
            )
        }
    }
}