package com.learning.network

sealed interface NetworkResult<out T> {
    data class Success<T>(
        val data: T,
    ) : NetworkResult<T>

    data class HttpError(
        val code: Int,
        val message: String? = null,
    ) : NetworkResult<Nothing>

    data object NetworkError : NetworkResult<Nothing>

    data class UnknownError(
        val throwable: Throwable,
    ) : NetworkResult<Nothing>
}
