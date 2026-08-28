package com.learning.database.transaction

import androidx.room.withTransaction
import com.learning.database.database.AppDatabase
import com.learning.database.profile.ProfileEntity
import com.learning.database.profile.SyncOperationEntity
import javax.inject.Inject

class ProfileDatabaseOperationsImpl
    @Inject
    constructor(
        private val database: AppDatabase,
    ) : ProfileDatabaseOperations {
        override suspend fun saveProfileAndQueueOperation(
            profile: ProfileEntity,
            operation: SyncOperationEntity,
        ) {
            database.withTransaction {
                database.profileDao().insertProfile(profile)
                database.syncOperationDao().insert(operation)
            }
        }
    }
