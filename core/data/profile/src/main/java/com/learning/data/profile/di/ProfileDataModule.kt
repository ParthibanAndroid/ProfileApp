package com.learning.data.profile.di

import com.learning.data.profile.data.local.ProfileLocalDataSource
import com.learning.data.profile.data.local.ProfileLocalDataSourceImpl
import com.learning.data.profile.data.network.ProfileNetworkDataSource
import com.learning.data.profile.data.network.ProfileNetworkDataSourceImpl
import com.learning.data.profile.data.repository.ProfileRepositoryImpl
import com.learning.data.profile.data.sync.ProfileSyncHandlerImpl
import com.learning.data.profile.domain.repository.ProfileRepository
import com.learning.database.profile.ProfileDao
import com.learning.database.profile.SyncOperationDao
import com.learning.database.transaction.ProfileDatabaseOperations
import com.learning.network.api.ProfileApi
import com.learning.sync.ProfileSyncHandler
import com.learning.sync.ProfileSyncScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileDataModule {

    @Provides
    @Singleton
    fun provideProfileRepository(
        localDataSource: ProfileLocalDataSource,
        networkDataSource: ProfileNetworkDataSource,
        syncScheduler: ProfileSyncScheduler,
    ): ProfileRepository {
        return ProfileRepositoryImpl(
            localDataSource = localDataSource,
            networkDataSource = networkDataSource,
            syncScheduler = syncScheduler,
        )
    }

    @Provides
    @Singleton
    fun provideProfileLocalDataSource(
        profileDao: ProfileDao,
        syncOperationDao: SyncOperationDao,
        databaseOperations: ProfileDatabaseOperations,
    ): ProfileLocalDataSource {
        return ProfileLocalDataSourceImpl(
            profileDao = profileDao,
            syncOperationDao = syncOperationDao,
            databaseOperations = databaseOperations,
        )
    }

    @Provides
    @Singleton
    fun provideProfileNetworkDataSource(
        api: ProfileApi,
    ): ProfileNetworkDataSource {
        return ProfileNetworkDataSourceImpl(api)
    }

    @Provides
    @Singleton
    fun provideProfileSyncHandler(
        localDataSource: ProfileLocalDataSource,
        networkDataSource: ProfileNetworkDataSource,
        syncOperationDao: SyncOperationDao,
    ): ProfileSyncHandler {
        return ProfileSyncHandlerImpl(
            localDataSource = localDataSource,
            networkDataSource = networkDataSource,
            syncOperationDao = syncOperationDao,
        )
    }
}
