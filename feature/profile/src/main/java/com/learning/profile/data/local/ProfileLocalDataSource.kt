package com.learning.profile.data.local

import com.learning.database.profile.ProfileEntity
import com.learning.database.profile.SyncOperationEntity
import kotlinx.coroutines.flow.Flow

interface ProfileLocalDataSource {
    fun observeProfile(id: String): Flow<ProfileEntity?>

    suspend fun getProfile(id: String): ProfileEntity?

    suspend fun insertProfile(profile: ProfileEntity)

    suspend fun updateProfile(profile: ProfileEntity)

    suspend fun deleteProfile(profile: ProfileEntity)

    suspend fun saveProfileAndQueueOperation(
        profile: ProfileEntity,
        operation: SyncOperationEntity,
    )

    suspend fun updateProfileAndQueueOperation(
        profile: ProfileEntity,
        operation: SyncOperationEntity,
    )
}
