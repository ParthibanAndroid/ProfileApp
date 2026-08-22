package com.learning.profile

sealed interface ProfileError {
    data object Network : ProfileError

    data class Server(
        val code: Int,
        val message: String?,
    ) : ProfileError

    data object Unknown : ProfileError
}
