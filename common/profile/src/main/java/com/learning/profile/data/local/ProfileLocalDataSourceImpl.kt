@file:RequiresApi(Build.VERSION_CODES.O)

package com.learning.profile.data.local

import android.os.Build
import androidx.annotation.RequiresApi
import com.learning.database.profile.ProfileDao
import com.learning.database.profile.ProfileEntity
import com.learning.database.profile.SyncOperationDao
import com.learning.database.profile.SyncOperationEntity
import com.learning.database.profile.SyncOperationType
import com.learning.database.transaction.ProfileDatabaseOperations
import com.learning.profile.data.mapper.toDomain
import com.learning.profile.domain.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileLocalDataSourceImpl
    @Inject
    constructor(
        private val profileDao: ProfileDao,
        private val syncOperationDao: SyncOperationDao,
        private val databaseOperations: ProfileDatabaseOperations,
    ) : ProfileLocalDataSource {
        override fun observeProfile(id: String): Flow<ProfileEntity?> = profileDao.observeProfile(id = id)

        override fun observeProfiles(): Flow<List<Profile>> =
            profileDao.observeProfiles().map { entities ->
                entities.map(ProfileEntity::toDomain)
            }

        override suspend fun getProfile(id: String): ProfileEntity? = profileDao.getProfile(id = id)

        override suspend fun insertProfile(profile: ProfileEntity) = profileDao.insertProfile(profile = profile)

        override suspend fun updateProfile(profile: ProfileEntity) = profileDao.updateProfile(profile = profile)

        override suspend fun deleteProfile(profile: ProfileEntity) = profileDao.deleteProfile(profile = profile)

        override suspend fun saveProfileAndQueueOperation(
            profile: ProfileEntity,
            operation: SyncOperationEntity,
        ) {
            databaseOperations.saveProfileAndQueueOperation(
                profile = profile,
                operation = operation,
            )
        }

        override suspend fun updateProfileAndQueueOperation(
            profile: ProfileEntity,
            operation: SyncOperationEntity,
        ) {
            databaseOperations.updateProfileAndQueueOperation(
                profile = profile,
                operation = operation,
            )
        }

        override suspend fun updatePendingCreateProfile(profile: ProfileEntity) {
            databaseOperations.updatePendingCreateProfile(profile = profile)
        }

        override suspend fun updatePendingUpdateProfile(profile: ProfileEntity) {
            databaseOperations.updatePendingUpdateProfile(profile = profile)
        }

        override suspend fun markProfileSyncedAndDeleteOperation(
            profile: ProfileEntity,
            operation: SyncOperationEntity,
        ) {
            databaseOperations.markProfileSyncedAndDeleteOperation(
                profile = profile,
                operation = operation,
            )
        }

        override suspend fun markProfilePendingDeleteAndQueueOperation(
            profile: ProfileEntity,
            operation: SyncOperationEntity,
        ) {
            databaseOperations.markProfilePendingDeleteAndQueueOperation(
                profile = profile,
                operation = operation,
            )
        }

        override suspend fun deleteProfileAndOperation(
            profileId: String,
            operation: SyncOperationEntity,
        ) {
            databaseOperations.deleteProfileAndOperation(
                profileId = profileId,
                operation = operation,
            )
        }

        override suspend fun cancelPendingCreate(
            profile: ProfileEntity,
            operation: SyncOperationEntity,
        ) {
            databaseOperations.cancelPendingCreate(
                profile = profile,
                operation = operation,
            )
        }

        override suspend fun getOperation(
            profileId: String,
            operationType: SyncOperationType,
        ): SyncOperationEntity? =
            syncOperationDao.getOperation(
                profileId = profileId,
                operationType = operationType,
            )

        override suspend fun replaceUpdateWithDelete(
            profile: ProfileEntity,
            updateOperation: SyncOperationEntity,
            deleteOperation: SyncOperationEntity,
        ) {
            databaseOperations.replaceUpdateWithDelete(
                profile = profile,
                updateOperation = updateOperation,
                deleteOperation = deleteOperation,
            )
        }

        override suspend fun replaceImageOperation(operation: SyncOperationEntity) {
            databaseOperations.replaceImageOperation(
                operation = operation,
            )
        }
    }
