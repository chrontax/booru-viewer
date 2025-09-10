package org.chrontax.booru_viewer.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DefaultTextField(
    value: String,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    label: String,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        modifier = modifier,
        label = { Text(label) },
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        isError = isError,
        singleLine = true
    )
}