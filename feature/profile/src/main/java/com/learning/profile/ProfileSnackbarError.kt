package com.learning.profile

sealed interface ProfileSnackbarError {
    data object ImageRequired : ProfileSnackbarError

    data object Network : ProfileSnackbarError

    data class Server(
        val code: Int,
        val message: String?,
    ) : ProfileSnackbarError

    data object Unknown : ProfileSnackbarError
}
