package com.learning.profile.data.network

import com.learning.network.NetworkResult
import com.learning.network.api.ProfileApi
import com.learning.network.model.CreateProfileRequest
import com.learning.network.model.ProfileResponse
import com.learning.network.model.UpdateProfileRequest
import com.learning.network.safeApiCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ProfileNetworkDataSourceImpl
    @Inject
    constructor(
        private val api: ProfileApi,
    ) : ProfileNetworkDataSource {
        override suspend fun getProfile(id: String): NetworkResult<ProfileResponse> =
            safeApiCall {
                api.getProfile(id = id)
            }

        override suspend fun createProfile(request: CreateProfileRequest): NetworkResult<ProfileResponse> =
            safeApiCall {
                api.createProfile(request = request)
            }

        override suspend fun updateProfile(
            id: String,
            request: UpdateProfileRequest,
        ): NetworkResult<ProfileResponse> =
            safeApiCall {
                api.updateProfile(id = id, request = request)
            }

        override suspend fun uploadProfileImage(
            id: String,
            file: File,
        ): NetworkResult<ProfileResponse> =
            safeApiCall {
                val mimeType =
                    when (file.extension.lowercase()) {
                        "jpg", "jpeg" -> "image/jpeg"

                        "png" -> "image/png"

                        "webp" -> "image/webp"

                        else -> throw IllegalArgumentException(
                            "Only JPEG, PNG and WebP images are supported",
                        )
                    }
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val multipartFile = MultipartBody.Part.createFormData("file", file.name, requestFile)

                api.uploadProfileImage(id = id, file = multipartFile)
            }

        override suspend fun deleteProfile(id: String): NetworkResult<Unit> =
            safeApiCall {
                api.deleteProfile(id = id)
            }
    }
