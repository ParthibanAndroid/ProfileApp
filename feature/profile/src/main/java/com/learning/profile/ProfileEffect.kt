package com.learning.profile

sealed interface ProfileEffect {
    data class ShowSnackbar(
        val error: ProfileSnackbarError,
    ) : ProfileEffect
}
