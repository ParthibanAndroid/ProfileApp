package com.learning.profile

data class ProfileUiState(
    val isLoading: Boolean = false,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String = "",
    val error: String? = null,
)
