package com.learning.profile.data.local

import com.learning.database.profile.ProfileDao
import com.learning.database.profile.ProfileEntity
import com.learning.database.profile.SyncOperationEntity
import com.learning.database.transaction.ProfileDatabaseOperations
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileLocalDataSourceImpl
    @Inject
    constructor(
        private val profileDao: ProfileDao,
        private val databaseOperations: ProfileDatabaseOperations,
    ) : ProfileLocalDataSource {
        override fun observeProfile(id: String): Flow<ProfileEntity?> = profileDao.observeProfile(id = id)

        override suspend fun getProfile(id: String): ProfileEntity? = profileDao.getProfile(id = id)

        override suspend fun insertProfile(profile: ProfileEntity) = profileDao.insertProfile(profile = profile)

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
    }
