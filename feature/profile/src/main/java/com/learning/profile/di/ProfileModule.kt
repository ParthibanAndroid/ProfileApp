package com.learning.profile.di

import com.learning.profile.ProfileImageFileProvider
import com.learning.profile.ProfileImageFileProviderImpl
import com.learning.profile.data.local.ProfileLocalDataSource
import com.learning.profile.data.local.ProfileLocalDataSourceImpl
import com.learning.profile.data.network.ProfileNetworkDataSource
import com.learning.profile.data.network.ProfileNetworkDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {
    @Binds
    @Singleton
    abstract fun bindProfileImageFileProvider(impl: ProfileImageFileProviderImpl): ProfileImageFileProvider
}
