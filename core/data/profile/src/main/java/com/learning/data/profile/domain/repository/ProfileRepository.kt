package com.learning.data.profile.domain.repository

import com.learning.data.profile.domain.model.Profile
import com.learning.network.NetworkResult
import com.learning.network.model.CreateProfileRequest
import com.learning.network.model.ProfileResponse
import com.learning.network.model.UpdateProfileRequest
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ProfileRepository {
    fun observeProfile(id: String): Flow<ProfileResponse?>

    fun observeProfiles(): Flow<List<Profile>>

    suspend fun refreshProfile(id: String): NetworkResult<Unit>

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
