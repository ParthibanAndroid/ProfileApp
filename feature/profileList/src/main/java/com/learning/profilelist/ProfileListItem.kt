package com.learning.profilelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.learning.profile.domain.model.Profile

@Composable
fun ProfileListItem(
    profile: Profile,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = profile.name,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = profile.email,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = profile.phone,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
