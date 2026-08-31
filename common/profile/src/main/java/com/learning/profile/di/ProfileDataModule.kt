package com.learning.profile.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.learning.profile.data.local.ProfileLocalDataSource
import com.learning.profile.data.local.ProfileLocalDataSourceImpl
import com.learning.profile.data.network.ProfileNetworkDataSource
import com.learning.profile.data.network.ProfileNetworkDataSourceImpl
import com.learning.profile.data.repository.ProfileRepositoryImpl
import com.learning.profile.data.sync.ProfileSyncHandlerImpl
import com.learning.profile.domain.repository.ProfileRepository
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
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
