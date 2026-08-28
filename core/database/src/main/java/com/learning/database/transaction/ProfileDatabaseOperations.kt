package com.learning.database.transaction

import com.learning.database.profile.ProfileEntity
import com.learning.database.profile.SyncOperationEntity

interface ProfileDatabaseOperations {
    suspend fun saveProfileAndQueueOperation(
        profile: ProfileEntity,
        operation: SyncOperationEntity,
    )
}
