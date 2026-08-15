package com.learning.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ProfileImage(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onProfileImageClick = {
                viewModel.onEvent(ProfileEvent.UpdatePhotoUrl(""))
            }
        )
        ProfileTextInputField(
            placeholderText = "Name",
            value = uiState.name,
            onValueChange = {
                viewModel.onEvent(ProfileEvent.UpdateName(it))
            },
        )
        ProfileTextInputField(
            placeholderText = "Email",
            value = uiState.email,
            onValueChange = {
                viewModel.onEvent(ProfileEvent.UpdateEmail(it))
            },
        )
        ProfileTextInputField(
            placeholderText = "Phone",
            value = uiState.phone,
            onValueChange = {
                viewModel.onEvent(ProfileEvent.UpdatePhone(it))
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
