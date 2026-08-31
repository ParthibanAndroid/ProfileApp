package com.learning.database.transaction

import com.learning.database.profile.ProfileEntity
import com.learning.database.profile.SyncOperationEntity

interface ProfileDatabaseOperations {
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

    suspend fun replaceUpdateWithDelete(
        profile: ProfileEntity,
        updateOperation: SyncOperationEntity,
        deleteOperation: SyncOperationEntity,
    )

    suspend fun replaceImageOperation(operation: SyncOperationEntity)
}
