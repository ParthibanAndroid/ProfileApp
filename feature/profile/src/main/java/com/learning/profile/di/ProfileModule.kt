package com.learning.profile.di

import android.content.Context
import com.learning.profile.ProfileImageFileProvider
import com.learning.profile.ProfileImageFileProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ProfileModule {

    @Provides
    @ViewModelScoped
    fun provideProfileImageFileProvider(
        @ApplicationContext context: Context,
    ): ProfileImageFileProvider {
        return ProfileImageFileProviderImpl(context)
    }
}
