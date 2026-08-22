package com.learning.profile

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    photoUrl: String?,
    selectedImageUri: Uri?,
    onProfileImageClick: (String) -> Unit,
) {
    val imageUrl = "http://192.168.0.103:8080$photoUrl"

    Box(
        modifier =
            modifier.clickable(onClick = {
                onProfileImageClick("")
            }),
    ) {
        AsyncImage(
            modifier =
                Modifier
                    .size(100.dp)
                    .clip(CircleShape),
            model = selectedImageUri ?: imageUrl,
            placeholder = painterResource(R.drawable.ic_profile),
            error = painterResource(R.drawable.ic_profile),
            contentScale = ContentScale.FillBounds,
            contentDescription = "Profile Image",
        )
        Icon(
            modifier = Modifier.align(Alignment.TopEnd),
            painter = painterResource(R.drawable.ic_edit),
            contentDescription = "Edit Icon",
        )
    }
}
