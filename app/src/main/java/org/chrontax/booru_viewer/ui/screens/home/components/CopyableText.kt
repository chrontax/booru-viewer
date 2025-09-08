package org.chrontax.booru_viewer.ui.screens.home.components

import android.content.ClipData
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.launch

@Composable
fun CopyableText(text: AnnotatedString, modifier: Modifier = Modifier, copyDataLabel: String, copyDataValue: String) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Text(
        text, modifier = modifier.clickable {
            scope.launch {
                clipboard.setClipEntry(
                    ClipEntry(
                        ClipData.newPlainText(
                            copyDataLabel, text
                        )
                    )
                )
            }
        })
}

@Composable
fun CopyableText(text: String, modifier: Modifier = Modifier, copyDataLabel: String, copyDataValue: String = text) {
    CopyableText(AnnotatedString(text), modifier, copyDataLabel, copyDataValue)
}