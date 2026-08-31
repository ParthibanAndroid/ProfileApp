package com.learning.database.transaction

import androidx.room.withTransaction
import com.learning.database.database.AppDatabase
import com.learning.database.profile.ProfileEntity
import com.learning.database.profile.SyncOperationEntity
import com.learning.database.profile.SyncOperationType
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

        override suspend fun updateProfileAndQueueOperation(
            profile: ProfileEntity,
            operation: SyncOperationEntity,
        ) {
            database.withTransaction {
                database.profileDao().updateProfile(profile)
                database.syncOperationDao().insert(operation)
            }
        }

        override suspend fun updatePendingCreateProfile(profile: ProfileEntity) {
            database.withTransaction {
                database.profileDao().updateProfile(profile)
            }
        }

        override suspend fun updatePendingUpdateProfile(profile: ProfileEntity) {
            database.withTransaction {
                database.profileDao().updateProfile(profile)
            }
        }

        override suspend fun markProfileSyncedAndDeleteOperation(
            profile: ProfileEntity,
            operation: SyncOperationEntity,
        ) {
            database.withTransaction {
                database.profileDao().updateProfile(profile)
                database.syncOperationDao().delete(operation)
            }
        }

        override suspend fun markProfilePendingDeleteAndQueueOperation(
            profile: ProfileEntity,
            operation: SyncOperationEntity,
        ) {
            database.withTransaction {
                database.profileDao().updateProfile(profile)
                database.syncOperationDao().insert(operation)
            }
        }

        override suspend fun deleteProfileAndOperation(
            profileId: String,
            operation: SyncOperationEntity,
        ) {
            database.withTransaction {
                database.profileDao().deleteProfileById(profileId)
                database.syncOperationDao().delete(operation)
            }
        }

        override suspend fun cancelPendingCreate(
            profile: ProfileEntity,
            operation: SyncOperationEntity,
        ) {
            database.withTransaction {
                database.profileDao().deleteProfileById(profile.id)
                database.syncOperationDao().delete(operation)
            }
        }

        override suspend fun replaceUpdateWithDelete(
            profile: ProfileEntity,
            updateOperation: SyncOperationEntity,
            deleteOperation: SyncOperationEntity,
        ) {
            database.withTransaction {
                database.syncOperationDao().delete(updateOperation)
                database.profileDao().updateProfile(profile)
                database.syncOperationDao().insert(deleteOperation)
            }
        }

        override suspend fun replaceImageOperation(operation: SyncOperationEntity) {
            database.withTransaction {
                val existingOperation =
                    database
                        .syncOperationDao()
                        .getOperation(
                            profileId = operation.profileId,
                            operationType = SyncOperationType.UPLOAD_IMAGE,
                        )

                existingOperation?.let {
                    database.syncOperationDao().delete(it)
                }

                database.syncOperationDao().insert(operation)
            }
        }
    }
