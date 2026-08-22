package com.learning.profile

import com.learning.network.NetworkResult
import com.learning.network.api.ProfileApi
import com.learning.network.model.ProfileResponse
import com.learning.network.safeApiCall
import javax.inject.Inject

class ProfileRepositoryImpl
    @Inject
    constructor(
        private val api: ProfileApi,
    ) : ProfileRepository {
        override suspend fun getProfile(id: String): NetworkResult<ProfileResponse> =
            safeApiCall {
                api.getProfile(id)
            }
    }
