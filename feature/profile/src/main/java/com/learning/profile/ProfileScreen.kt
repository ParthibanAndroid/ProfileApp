package com.learning.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = SnackbarHostState()
    val scope = rememberCoroutineScope()
    val photoUrlLabel = stringResource(id = R.string.photo_url_label)

    LaunchedEffect(uiState.errors) {
        if (uiState.errors.containsKey(photoUrlLabel.lowercase(Locale.getDefault()))) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = uiState.errors[photoUrlLabel.lowercase(Locale.getDefault())] ?: "",
                )
            }
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
            }

            uiState.errorMessage != null -> {
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
    errors: Map<String, String>,
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
    val context = LocalContext.current

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
            isError = errors.containsKey(nameLabel.lowercase(LocalLocale.current.platformLocale)),
            error = errors[nameLabel.lowercase(LocalLocale.current.platformLocale)] ?: "",
            keyboardType = KeyboardType.PersonName,
            imeAction = ImeAction.Next,
        )
        ProfileTextInputField(
            placeholderText = emailLabel,
            value = email,
            onValueChange = {
                event(ProfileEvent.EmailChanged(it))
            },
            isError = errors.containsKey(emailLabel.lowercase(LocalLocale.current.platformLocale)),
            error = errors[emailLabel.lowercase(LocalLocale.current.platformLocale)] ?: "",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        )
        ProfileTextInputField(
            placeholderText = phoneLabel,
            value = phone,
            onValueChange = {
                event(ProfileEvent.PhoneChanged(it))
            },
            isError = errors.containsKey(phoneLabel.lowercase(LocalLocale.current.platformLocale)),
            error = errors[phoneLabel.lowercase(LocalLocale.current.platformLocale)] ?: "",
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
                event(ProfileEvent.SaveClicked(context = context))
            }) {
                Text(text = stringResource(id = R.string.save_button))
            }
            Button(modifier = Modifier.weight(1f), onClick = {
                event(ProfileEvent.UpdateClicked(context = context))
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
