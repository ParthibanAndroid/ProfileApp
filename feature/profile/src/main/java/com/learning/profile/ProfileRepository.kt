package com.learning.profile

import com.learning.network.model.ProfileResponse

interface ProfileRepository {
    suspend fun getProfile(id: String): ProfileResponse
}
