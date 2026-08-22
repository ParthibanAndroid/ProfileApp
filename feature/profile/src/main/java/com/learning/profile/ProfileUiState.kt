package com.learning.profile

import android.net.Uri

data class ProfileUiState(
    val isLoading: Boolean = false,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String? = null,
    val selectedImageUri: Uri? = null,
    val errors: Map<ProfileField, ProfileValidationError> = emptyMap(),
    val snackbarError: ProfileError? = null,
)
