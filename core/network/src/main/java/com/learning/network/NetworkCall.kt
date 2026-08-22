package com.learning.network

import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> =
    try {
        NetworkResult.Success(apiCall())
    } catch (e: HttpException) {
        NetworkResult.HttpError(e.code(), e.message())
    } catch (e: IOException) {
        NetworkResult.NetworkError
    } catch (e: Exception) {
        NetworkResult.UnknownError(e)
    }
