package org.chrontax.booru_viewer.ui.screens.home.components

import android.content.ClipData
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import kotlinx.coroutines.launch

@Composable
fun PropertyText(name: String, value: String, modifier: Modifier = Modifier) {
    CopyableText(
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$name: ")
            }
            append(value)
        },
        copyDataLabel = name,
        copyDataValue = value,
        modifier = modifier
    )
}