package com.learning.data.profile.data.local

import com.learning.database.profile.ProfileEntity
import com.learning.database.profile.SyncOperationEntity
import com.learning.database.profile.SyncOperationType
import com.learning.data.profile.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileLocalDataSource {
    fun observeProfile(id: String): Flow<ProfileEntity?>

    fun observeProfiles(): Flow<List<Profile>>

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

    suspend fun updatePendingCreateProfile(profile: ProfileEntity)

    suspend fun updatePendingUpdateProfile(profile: ProfileEntity)

    suspend fun markProfileSyncedAndDeleteOperation(
        profile: ProfileEntity,
        operation: SyncOperationEntity,
    )

    suspend fun markProfilePendingDeleteAndQueueOperation(
        profile: ProfileEntity,
        operation: SyncOperationEntity,
    )

    suspend fun deleteProfileAndOperation(
        profileId: String,
        operation: SyncOperationEntity,
    )

    suspend fun cancelPendingCreate(
        profile: ProfileEntity,
        operation: SyncOperationEntity,
    )

    suspend fun getOperation(
        profileId: String,
        operationType: SyncOperationType,
    ): SyncOperationEntity?

    suspend fun replaceUpdateWithDelete(
        profile: ProfileEntity,
        updateOperation: SyncOperationEntity,
        deleteOperation: SyncOperationEntity,
    )

    suspend fun replaceImageOperation(operation: SyncOperationEntity)
}
