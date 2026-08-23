package com.learning.profile

sealed interface ProfileSnackbarError {
    data object ImageRequired : ProfileSnackbarError

    data object ProfileCreated: ProfileSnackbarError

    data object ProfileUpdated: ProfileSnackbarError

    data object ProfileDeleted: ProfileSnackbarError

    data object Network : ProfileSnackbarError

    data class Server(
        val code: Int,
        val message: String? = null,
    ) : ProfileSnackbarError

    data object Unknown : ProfileSnackbarError
}
