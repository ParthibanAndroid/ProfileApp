package com.learning.profileapp.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileBottomAppBar(modifier: Modifier = Modifier) {
    BottomAppBar(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Button(modifier = Modifier.weight(1f), onClick = { }) {
                Text(text = "Save")
            }
            Button(modifier = Modifier.weight(1f), onClick = { }) {
                Text(text = "Update")
            }
            Button(modifier = Modifier.weight(1f), onClick = { }) {
                Text(text = "Delete")
            }
        }
    }
}
