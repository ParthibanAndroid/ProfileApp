package com.learning.data.profile.data.network

import com.learning.network.NetworkResult
import com.learning.network.model.CreateProfileRequest
import com.learning.network.model.ProfileResponse
import com.learning.network.model.UpdateProfileRequest
import java.io.File

interface ProfileNetworkDataSource {
    suspend fun getProfile(id: String): NetworkResult<ProfileResponse>

    suspend fun createProfile(request: CreateProfileRequest): NetworkResult<ProfileResponse>

    suspend fun updateProfile(
        id: String,
        request: UpdateProfileRequest,
    ): NetworkResult<ProfileResponse>

    suspend fun uploadProfileImage(
        id: String,
        file: File,
    ): NetworkResult<ProfileResponse>

    suspend fun deleteProfile(id: String): NetworkResult<Unit>
}
