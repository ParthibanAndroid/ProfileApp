package com.learning.profilelist

import com.learning.profile.domain.model.Profile

data class ProfileListUiState(
    val profiles: List<Profile> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
