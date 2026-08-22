package com.learning.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val snackbarMessage =
        when (uiState.snackbarError) {
            ProfileValidationError.ImageRequired -> {
                stringResource(R.string.select_photo_required_error)
            }

            // Future snackbar errors go here
            else -> {
                null
            }
        }

    LaunchedEffect(Unit) {
        viewModel.onEvent(ProfileEvent.LoadProfile)
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(ProfileEvent.SnackbarErrorShown)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                ProfileContent(
                    modifier = Modifier.padding(innerPadding),
                    name = uiState.name,
                    email = uiState.email,
                    phone = uiState.phone,
                    photoUrl = uiState.photoUrl,
                    selectedImageUri = uiState.selectedImageUri,
                    errors = uiState.errors,
                    event = viewModel::onEvent,
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    modifier: Modifier = Modifier,
    name: String,
    email: String,
    phone: String,
    photoUrl: String?,
    selectedImageUri: Uri?,
    errors: Map<ProfileField, ProfileValidationError>,
    event: (ProfileEvent) -> Unit,
) {
    val imagePickLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
        ) { uri ->
            uri?.let {
                event(ProfileEvent.ProfileImageSelected(it))
            }
        }
    val nameLabel = stringResource(id = R.string.name_label)
    val emailLabel = stringResource(id = R.string.email_label)
    val phoneLabel = stringResource(id = R.string.phone_label)

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
            photoUrl = photoUrl,
            selectedImageUri = selectedImageUri,
            onProfileImageClick = {
                imagePickLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly,
                    ),
                )
            },
        )
        ProfileTextInputField(
            placeholderText = nameLabel,
            value = name,
            onValueChange = {
                event(ProfileEvent.NameChanged(it))
            },
            isError = errors.containsKey(ProfileField.NAME),
            error =
                when (errors[ProfileField.NAME]) {
                    ProfileValidationError.NameRequired -> {
                        stringResource(R.string.name_required_error)
                    }

                    ProfileValidationError.NameTooShort -> {
                        stringResource(R.string.name_length_error)
                    }

                    else -> {
                        ""
                    }
                },
            keyboardType = KeyboardType.PersonName,
            imeAction = ImeAction.Next,
        )
        ProfileTextInputField(
            placeholderText = emailLabel,
            value = email,
            onValueChange = {
                event(ProfileEvent.EmailChanged(it))
            },
            isError = errors.containsKey(ProfileField.EMAIL),
            error =
                when (errors[ProfileField.EMAIL]) {
                    ProfileValidationError.EmailRequired -> {
                        stringResource(R.string.email_required_error)
                    }

                    ProfileValidationError.EmailInvalid -> {
                        stringResource(R.string.invalid_email_error)
                    }

                    else -> {
                        ""
                    }
                },
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        )
        ProfileTextInputField(
            placeholderText = phoneLabel,
            value = phone,
            onValueChange = {
                event(ProfileEvent.PhoneChanged(it))
            },
            isError = errors.containsKey(ProfileField.PHONE),
            error =
                when (errors[ProfileField.PHONE]) {
                    ProfileValidationError.PhoneRequired -> {
                        stringResource(R.string.phone_required_error)
                    }

                    ProfileValidationError.PhoneTooShort -> {
                        stringResource(R.string.phone_length_error)
                    }

                    else -> {
                        ""
                    }
                },
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Done,
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Button(modifier = Modifier.weight(1f), onClick = {
                event(ProfileEvent.SaveClicked)
            }) {
                Text(text = stringResource(id = R.string.save_button))
            }
            Button(modifier = Modifier.weight(1f), onClick = {
                event(ProfileEvent.UpdateClicked)
            }) {
                Text(text = stringResource(id = R.string.update_button))
            }
            Button(modifier = Modifier.weight(1f), onClick = {
                event(ProfileEvent.DeleteClicked)
            }) {
                Text(text = stringResource(id = R.string.delete_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
