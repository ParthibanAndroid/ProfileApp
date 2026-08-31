package com.learning.profile.di

import com.learning.profile.data.local.ProfileLocalDataSource
import com.learning.profile.data.local.ProfileLocalDataSourceImpl
import com.learning.profile.data.network.ProfileNetworkDataSource
import com.learning.profile.data.network.ProfileNetworkDataSourceImpl
import com.learning.profile.data.repository.ProfileRepositoryImpl
import com.learning.profile.data.sync.ProfileSyncHandlerImpl
import com.learning.profile.domain.repository.ProfileRepository
import com.learning.sync.ProfileSyncHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileDataModule {
    @Binds
    @Singleton
    abstract fun bindProfileNetworkDataSource(impl: ProfileNetworkDataSourceImpl): ProfileNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindProfileLocalDataSource(implementation: ProfileLocalDataSourceImpl): ProfileLocalDataSource

    @Binds
    @Singleton
    abstract fun bindProfileRepository(implementation: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindProfileSyncHandler(implementation: ProfileSyncHandlerImpl): ProfileSyncHandler
}
