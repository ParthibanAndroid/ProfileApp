package com.learning.profile

import android.net.Uri

data class ProfileUiState(
    val isLoading: Boolean = false,
    val id: String? = null,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String? = null,
    val selectedImageUri: Uri? = null,
    val errors: Map<ProfileField, ProfileValidationError> = emptyMap(),
)
