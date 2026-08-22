package com.learning.profile

import com.learning.network.api.ProfileApi
import com.learning.network.model.ProfileResponse
import javax.inject.Inject

class ProfileRepositoryImpl
    @Inject
    constructor(
        private val api: ProfileApi,
    ) : ProfileRepository {
        override suspend fun getProfile(id: String): ProfileResponse = api.getProfile(id)
    }
