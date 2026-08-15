package com.learning.profile

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ProfileTextInputField(
    modifier: Modifier = Modifier,
    placeholderText: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(text = placeholderText)
        },
        value = value,
        onValueChange = onValueChange,
    )
}
